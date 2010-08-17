package org.netbeans.modules.php.nette.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;

/**
 * Provides embedded languages for HTML or LATTE tokens (language is denoted by mime-type)
 * @author Radek Ježdík
 */
public class LatteEmbeddingProvider extends EmbeddingProvider {

	@Override
	public List<Embedding> getEmbeddings(Snapshot snapshot) {
		// for sending atributes for LatteLexer (dynamic variables)
		// may be not necessary
		Document doc = snapshot.getSource().getDocument(true);
		InputAttributes inputAttributes = new InputAttributes();
		LatteParseData tmplParseData = new LatteParseData(doc);
		inputAttributes.setValue(LatteTokenId.language(), LatteParseData.class, tmplParseData, false);
		//inputAttributes.setValue(LatteTokenId.language(), "document", doc, false);
		doc.putProperty(InputAttributes.class, inputAttributes);

		EmbeddingResolver resolver = new EmbeddingResolver(snapshot);

		return resolver.getEmbeddings();
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void cancel() {
		//do nothing
	}

	/**
	 * Factory for creating new LatteEmbeddingProvider.
	 */
	public static final class Factory extends TaskFactory {

		@Override
		public Collection<SchedulerTask> create(final Snapshot snapshot) {
			return Collections.<SchedulerTask>singletonList(new LatteEmbeddingProvider());
		}
	}
	
	private static final List<String> arrayMacros = new ArrayList<String>();
	static {
		arrayMacros.add("var");
		arrayMacros.add("default");
		arrayMacros.add("assign");
	};

	private static final List<String> specialMacros = new ArrayList<String>();
	static {
		specialMacros.add("plink");
		specialMacros.add("link");
		specialMacros.add("widget");
		specialMacros.add("control");
		specialMacros.add("include");
		specialMacros.add("extends");
	};

	private static final List<String> blockMacros = new ArrayList<String>();
	static {
		blockMacros.add("foreach");
		blockMacros.add("for");
		blockMacros.add("if");
		blockMacros.add("ifset");
		blockMacros.add("ifcurrent");
		blockMacros.add("while");
		blockMacros.add("block");
		blockMacros.add("snippet");
	};

	private static final List<String> latteMacros = new ArrayList<String>();
	static {
		latteMacros.add("=");
		latteMacros.add("_");
		latteMacros.add("!");
		latteMacros.add("!=");
		latteMacros.add("!_");
		latteMacros.add("?");
	};

	private class EmbeddingResolver {

		Snapshot snapshot;

		/* Stores all embeddings with text/x-php mime */
		List<Embedding> htmlEmbeddings = new ArrayList<Embedding>();

		// LatteTopTokenId sequence
		TokenSequence<LatteTopTokenId> sequence;

		// macro name used for n:attributes
		String macroName = null;

		// counts number of {block} macros (there may be one unclosed)
		int numOfBlocks = 0;

		// stores sequence of tags and number of code blocks which were defined by n:attributes for that tag
		List<Integer> tags = new ArrayList<Integer>();

		public EmbeddingResolver(Snapshot snapshot) {
			this.snapshot = snapshot;
		}

		public List<Embedding> getEmbeddings() {
			//TODO: neprochazet celou sekvenci (ale par radku pred a po caret)
			// jestli je to vubec mozny...

			TokenHierarchy<CharSequence> th = TokenHierarchy.create(snapshot.getText(), LatteTopTokenId.language());
			sequence = th.tokenSequence(LatteTopTokenId.language());

			sequence.moveStart();

			//String macro = null;
			while(sequence.moveNext()) {
				Token t = sequence.token();
				if(t.id() == LatteTopTokenId.LATTE) {
					getLatteEmbedding(t);											// deals with all latte macros
				} else {
					//otherwise html/php
					LatteTopTokenId id = (LatteTopTokenId) t.id();
					if(t.id() == LatteTopTokenId.HTML_TAG || t.id() == LatteTopTokenId.LATTE_TAG) {
						String tag = t.text().toString();							// tag
						if(tag.startsWith("<") && tag.charAt(1) != '/') {			// opening tag
							if(t.id() == LatteTopTokenId.LATTE_TAG) {
								macroName = (String) t.getProperty("macro");		// if <n:tag, store macro name
							}
							tags.add(0);											// counts nesting
						} else if(tag.equals("/>") || tag.startsWith("</")) {		// closed non-pair tag or closing tag
							if(tags.size() > 0) {									// if there are some tags
								int c = tags.remove(tags.size() - 1);				// remove last tag (opening)
								if(c > 0) {											// if there are some code blocks
									embedHtml("<?php ");
									for(int i = 0; i < c; i++) {
										embedHtml("}"); // close all code blocks
									}
									embedHtml(" ?>");
								}
							}
							macroName = null;
						} else if(tag.equals(">") && t.id() == LatteTopTokenId.LATTE_TAG) {
							macroName = null;										// do nothing here
						}
					}
					// deals as html/php (will color all HTML tags appropriately)
					embedHtml(sequence.offset(), t.length());
				}
			}
			// there can be one {block} macro opened (in that case close it internally)
			if(numOfBlocks == 1) {
				embedHtml("<?php } ?>");
			}

			List<Embedding> embeddings = new ArrayList<Embedding>();		// embedding result
			
			//merges text/x-php5 embeddings into one piece
			if(!htmlEmbeddings.isEmpty()) {
				embeddings.add(Embedding.create(htmlEmbeddings));
			}


			if(embeddings.isEmpty()) {
				return Collections.emptyList();		// no embedding
			} else {
				return embeddings;					// return embedding
			}

		}

		private void getLatteEmbedding(Token t) {
			String props = (String) t.getProperty("macro");								// get macro if it exists
			if(props != null) {
				macroName = props;														// store macro name in macroName
			} else if(t.text().charAt(0) == '{') {
				macroName = null;														// else it is a macro starting with {
			}
			
			TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(t.text(), LatteTokenId.language());
			TokenSequence<LatteTokenId> sequence2 = th2.tokenSequence(LatteTokenId.language());

			boolean endMacro = false;
			String macro = (macroName != null ? macroName : "");					// macro name used internally

			sequence2.moveStart();
			while(sequence2.moveNext()) {
				Token<LatteTokenId> t2 = sequence2.token();
				if(t2.id() == LatteTokenId.SLASH && sequence2.offset() <= 2) {		// is end macro {/
					endMacro = true;
				}
				if(t2.id() == LatteTokenId.MACRO) {									// store macro name
					macro = t2.text().toString();
					macroName = null;
					continue;
				}
				int start = sequence2.offset() + sequence.offset();
				int length = 0;
				if(!macro.equals("")) {
					if(arrayMacros.contains(macro)) {
						// {var var => ""} ->  $var = "";
						processArrayMacro(sequence2, start);
						break;
					}
					if(specialMacros.contains(macro)) {
						// {link default var => $val} -> "default"; array(var => $val);
						processSpecialMacro(sequence2, start);
						break;
					}
					if(latteMacros.contains(macro)) {
						processLatteMacro(sequence2, start, macro);
						break;
					}
					if(blockMacros.contains(macro)) {
						// {if} {/if} -> if() { }
						processBlockMacro(sequence2, start, macro, endMacro);
						break;
					}
					if(macro.equals("attr")) {
						// {attr class() something()} -> $v->class()->something()
						processAttrMacro(sequence2);
						break;
					}

					// default (all other macros)
					boolean toString = true;									// encapsulate with string quotes?
					do {
						t2 = sequence2.token();
						if(t2.id() == LatteTokenId.VARIABLE || t2.id() == LatteTokenId.STRING) {
							toString = false;	//if vthere is variable or string literal do not "convert" to string
						}
						if(t2.id() == LatteTokenId.RD) {
							break;
						}
						length += t2.length();
					} while(sequence2.moveNext());
					
					if(!toString) {
						// if there is a string literal or variable, do not add quotes
						embedHtml("<?php ");
						embedHtml(start, length);
						embedHtml(" ?>");
					} else {
						// otherwise encasulate parametr with double quotes
						embedHtml("<?php \"");
						embedHtml(start, length);
						embedHtml("\"?>");
					}
					break;
				}
				// no macro only variable ( {$variable} )
				// if variable or error starting with $ (as user will write the rest after :) )
				if(t2.id() == LatteTokenId.VARIABLE
						|| (t2.id() == LatteTokenId.ERROR && t2.text().equals("$")))
				{
					do {
						t2 = sequence2.token();
						if(t2.id() == LatteTokenId.RD || t2.id() == LatteTokenId.PIPE) {
							break;
						}
						length += t2.length();
					} while(sequence2.moveNext());
					// we don't need to write any echo or escaping (too long)
					embedHtml("<?php ");
					embedHtml(start, length);
					embedHtml(" ?>");
				}
			}
		}

		private void processAttrMacro(TokenSequence<LatteTokenId> sequence2) {
			int length = 0;
			int numOfBrackets = 0;				// counts number of brackets to clearly match nested brackets
			int whiteSpace = 0;					// counts whitespaces (delimits attr calls)
			List<Integer> starts = new ArrayList<Integer>();	// array of starts of attr call
			List<Integer> lengths = new ArrayList<Integer>();	// array of lengths of attr call
			do {
				Token<LatteTokenId> t2 = sequence2.token();
				if(whiteSpace == 0) {							// expecting first attr call or right delim }
					if(t2.id() == LatteTokenId.WHITESPACE
							|| t2.id() == LatteTokenId.RD) {	// if found one
						whiteSpace++;
						starts.add(sequence.offset() + sequence2.offset() + t2.length());	//add start for new attr call
						length = 0;								// will calculate below
					}
					continue;
				} else {
					if(t2.id() == LatteTokenId.LNB) {			// if left bracket
						numOfBrackets++;						// add bracket
					}
					if(t2.id() == LatteTokenId.RNB) {			// if right bracket
						numOfBrackets--;						// remove bracket
					}
					if((t2.id() == LatteTokenId.WHITESPACE || t2.id() == LatteTokenId.RD)
							&& numOfBrackets == 0) {			// expecting another attr call 
						lengths.add(length);					// add last call length
						if(t2.id() != LatteTokenId.RD) {		// add start for new attr call
							starts.add(sequence.offset() + sequence2.offset() + t2.length());
						} else {
							break;								// or no other call found
						}
						length = 0;
						continue;
					}
				}
				length += t2.length();
			} while(sequence2.moveNext());
			
			if(numOfBrackets != 0) {
				lengths.add(length);											// add last length
			}
			embedHtml("<?php $v");		// $v represents a Html object
			for(int i = 0; i < lengths.size(); i++) {
				// the subsequence is empty or whitespace only
				if(snapshot.getText().subSequence(starts.get(i), starts.get(i) + lengths.get(i)).toString().trim().equals("")) {
					continue;
				}
				embedHtml("->");		// add -> object access
				embedHtml(starts.get(i), lengths.get(i));	// and attr call itself
			}
			embedHtml(";?>");
		}

		
		private void processBlockMacro(TokenSequence<LatteTokenId> sequence2, int start, String macro,
				boolean endMacro) {
			int length = 0;
			if(!endMacro) {												// is not end macro
				boolean pipe = false;									// helper delimiter found
				do {
					Token<LatteTokenId> t2 = sequence2.token();
					if(pipe && t2.id() != LatteTokenId.PIPE) {			// if pipe found and it is not php OR (||)
						length--;										// end cycle
						break;
					} else {
						if(!pipe && t2.id() == LatteTokenId.PIPE) {		// helper delim found
							pipe = true;
						} else {
							pipe = false;
						}
					}
					if(t2.id() == LatteTokenId.RD) {
						break;
					}
					length += t2.length();
				} while(sequence2.moveNext());
				
				if(macro.equals("block") || macro.equals("snippet")) {
					// for block and snippet process as string only
					embedHtml("<?php \"");
					embedHtml(start, length);
					embedHtml("\";{?>");
					// if it is not n: tag or attribute and it is a block
					if(macroName == null && macro.equals("block")) {
						numOfBlocks++;			// counts number of {block} macros (last closing can be ommited)
					}
				} else {
					// for if, foreach, ... process as <?php macro(attr) { ?>
					embedHtml("<?php " + macro + "(");
					embedHtml(start, length);
					embedHtml(");{");
					if(macro.equals("foreach")) {		// in case of foreach create $iterator variable
						embedHtml("$iterator=new SmartCachingIterator;");
					}
					embedHtml("?>");
				}
				// in case of n:tag
				// FIXME: possible error, try <n:block tag and n:block="" attr at the same template?
				if(macroName != null) {
					int i = tags.remove(tags.size() - 1);
					tags.add(i + 1);
				}
			} else {
				// for closing macros just create block closing bracket }
				embedHtml("<?php } ?>");
				if(macro.equals("block")) {
					numOfBlocks--;				// in case of {block} macro only (last closing can be ommited)
				}
			}
		}

		private void processLatteMacro(TokenSequence<LatteTokenId> sequence2, int start, String macro) {
			int length = 0;
			do {
				Token<LatteTokenId> t2 = sequence2.token();
				if(t2.id() == LatteTokenId.RD) {
					break;
				}
				length += t2.length();
			} while(sequence2.moveNext());

			if(macro.equals("_") || macro.equals("=") || macro.equals("!") || macro.equals("!=")
					 || macro.equals("!_"))
			{
				// in case of output macros process as php echo
				embedHtml("<?php echo");
				embedHtml(start, length);
				embedHtml(" ?>");
			} else {
				// other process as regular php code
				embedHtml("<?php ");
				embedHtml(start, length);
				embedHtml(" ?>");
			}
		}
		
		private void processSpecialMacro(TokenSequence<LatteTokenId> sequence2, int start) {
			// include, widget, control, (p)link, extends, ...
			int firstStart = start;
			int whiteSpace = 0;
			boolean toString = true;
			int length = 0;
			do {
				Token<LatteTokenId> t2 = sequence2.token();
				if(whiteSpace < 2) {											// first param ( {mac param ...})
					if(t2.id() == LatteTokenId.VARIABLE || t2.id() == LatteTokenId.STRING) {
						toString = false;										// do not encapsulate with quotes
					}
					if(t2.id() == LatteTokenId.WHITESPACE						// delims parameters
							|| (whiteSpace == 1 && t2.id() == LatteTokenId.COMA) // or delimted by coma!
							|| t2.id() == LatteTokenId.RD)
					{
						whiteSpace++;
						start = sequence.offset() + sequence2.offset() + t2.length();	// start of other params
						if(t2.id() == LatteTokenId.RD) {
							start--;											// exclude right delim
							break;
						}
					}
					continue;
				}
				if(t2.id() == LatteTokenId.RD) {
					break;
				}
				length += t2.length();
			} while(sequence2.moveNext());

			String fParam = snapshot.getText().subSequence(firstStart, start).toString();	// get first param
			int trim = (fParam.endsWith(",") || fParam.endsWith(" ")) ? 1 : 0;	// will remove trailing comma or WS
			if(!toString) {
				// if variable or string literal was found in first param, do not encapsulte with quotes
				embedHtml("<?php ");
				embedHtml(firstStart, start - firstStart - trim);
			} else {
				// otherwise process as php string
				embedHtml("<?php \"");
				embedHtml(firstStart, start - firstStart - trim);
				embedHtml("\"");
			}
			// for other params create array
			embedHtml("; array( ");
			embedHtml(start, length);
			embedHtml(")?>");
		}
		
		private void processArrayMacro(TokenSequence<LatteTokenId> sequence2, int start) {
			// assign, var, default
			int length = 0;

			List<Integer> starts = new ArrayList<Integer>();	// array of starts of var assignments
			List<Integer> lengths = new ArrayList<Integer>();	// array of lengths of var assignments

			byte state = -1;									// -1,0 - variable; 1,2 - value
			int numOfBrackets = 0;								// counts nested brackets
			String var = "";									// stores var name and var value
			do {
				Token<LatteTokenId> t2 = sequence2.token();
				if (state == -1 || state == 0) {								// var name
					if (state == -1 && t2.id() != LatteTokenId.WHITESPACE) {
						start = sequence2.offset() + sequence.offset();			// start of var name
						state = 0;												// don't search for var name start
						length = 0;
						var = "";
					}
					if (t2.id() == LatteTokenId.ASSIGN) {						// assign seq found
						starts.add(var.trim().startsWith("$") ? start : -start);// not $ = negative position (see below)
						lengths.add(length);
						length = 0;
						state = 1;												// search for value
						continue;
					}
					if (t2.id() != LatteTokenId.WHITESPACE) {					// no whitespace = variable name
						length += t2.length();									// add text length
						var += t2.text();										// add text to var name
					}
				}
				if (state == 1 || state == 2) {
					if (state == 1) {
						start = sequence2.offset() + sequence.offset();			// start of value
						length = 0;
						state = 2;
						var = "";												// where value will be stored
					}
					if (t2.id() == LatteTokenId.LNB) {							// left bracket found (count it)
						numOfBrackets++;
					}
					if (t2.id() == LatteTokenId.RNB) {							// right bracket found (remove it)
						numOfBrackets--;
					}
					if (t2.id() == LatteTokenId.RD								// right delim } found
							|| (t2.id() == LatteTokenId.COMA && numOfBrackets == 0)) {	// or comma found (out of brackets)
						starts.add(start);										// add value start
						lengths.add(length);									// add value length
						state = -1;												// search for next variable name
						continue;
					}
					length += t2.length();										// add up value length
					var += t2.text();											// add variable value
				}
			} while (sequence2.moveNext());
			
			embedHtml("<?php ");
			for (int i = 0; i < starts.size(); i += 2) {
				if (starts.get(i) < 0) {										// if position negative prepend with $
					embedHtml("$");
					embedHtml(-starts.get(i), lengths.get(i));	// variable
				} else {
					embedHtml(starts.get(i), lengths.get(i));	// variable
				}

				embedHtml("=");		// assignment char
				embedHtml(starts.get(i + 1), lengths.get(i + 1)); // var value
				embedHtml(";");
			}
			embedHtml("?>");
		}
		
		private boolean embedHtml(String html) {
			return htmlEmbeddings.add(snapshot.create(html, FileUtils.PHP_MIME_TYPE));
		}

		private boolean embedHtml(int start, int length) {
			return htmlEmbeddings.add(snapshot.create(start, length, FileUtils.PHP_MIME_TYPE));
		}

	}

}
