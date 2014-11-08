/*
 * The MIT license
 *
 * Copyright (c) 2010 Radek Ježdík <redhead@email.cz>, Ondřej Brejla <ondrej@brejla.cz>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package org.netbeans.modules.php.nette.editor.resolvers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.editor.Embedder;
import org.netbeans.modules.php.nette.editor.macros.processors.MacroProcessorFactory;
import org.netbeans.modules.php.nette.editor.macros.processors.MacroProcessor;
import org.netbeans.modules.php.nette.editor.macros.processors.NoMacroProcessor;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;

/**
 *
 * @author Ondřej Brejla
 */
public class LatteResolver extends TemplateResolver {

	public LatteResolver(Embedder embedder) {
		super(embedder);
	}

	@Override
	public void solve(Token t, TokenSequence<LatteTopTokenId> sequence) {
		String props = (String) t.getProperty("macro");			// get n:attr macro name if it exists

		if (props != null) {
			setMacroName(props);								// store macro name in macroName
		} else if (t.text().charAt(0) == '{') {
			setMacroName(null);									// else it is a macro starting with {
		}

		TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(t.text(), LatteTokenId.language());
		TokenSequence<LatteTokenId> sequence2 = th2.tokenSequence(LatteTokenId.language());

		boolean endMacro = false;
		String macro = (getMacroName() != null ? getMacroName() : "");					// macro name used internally

		sequence2.moveStart();
		while(sequence2.moveNext()) {
			Token<LatteTokenId> t2 = sequence2.token();

			if (isEndMacro(t2, sequence2)) {		// is end macro {/
				endMacro = true;
			}

			if(t2.id() == LatteTokenId.COMMENT) {
				String comment = t2.text().toString();
				parseLatteDoc(comment);
			}

			if (t2.id() == LatteTokenId.MACRO) {									// store macro name
				macro = t2.text().toString();
				setMacroName(null);
				continue;
			}

			int start = sequence2.offset() + sequence.offset();
			if (!macro.equals("")) {
				MacroProcessor macroProcessor = MacroProcessorFactory.getMacroProcessor(macro);
				macroProcessor.process(sequence, sequence2, start, macro, endMacro, embedder);

				break;
			}
			// no macro only variable ( {$variable} )
			// if variable or error starting with $ (as user will write the rest after :) )
			NoMacroProcessor noMacroProcessor = new NoMacroProcessor();
			noMacroProcessor.process(sequence, sequence2, start, macro, endMacro, embedder);
		}
	}

	private boolean isEndMacro(Token<LatteTokenId> t2, TokenSequence<LatteTokenId> sequence2) {
		return (t2.id() == LatteTokenId.SLASH && sequence2.offset() <= 2);
	}

	private void parseLatteDoc(String comment) {
		String[] lines = comment.split("\n");
		Pattern pattern = Pattern.compile("[\\s\\*]*@(param)\\s+([A-Za-z0-9]+)\\s+(\\$[A-Za-z0-9]+)[\\s\\*]*");
		for(String l : lines) {
			Matcher m = pattern.matcher(l);
			if(m.find()) {
				String annotation = m.group(1);
				String type = m.group(2);
				String variable = m.group(3);
				if(annotation.equals("param")) {
					if(type.equals("int") || type.equals("long") || type.equals("float") || type.equals("double")) {
						type = "(" + type + ") 1";
					} else if(type.equals("bool")) {
						type = "true";
					} else if(type.equals("array")) {
						type = "array()";
					} else if(type.equals("string")) {
						type = "\"\"";
					} else {
						type = "new "+type;
					}
					embedder.embed("<?php " + variable + "=" + type + " ?>");
				}
			}
		}
	}

}
