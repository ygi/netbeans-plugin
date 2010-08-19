
package org.netbeans.modules.php.nette.editor.macros.processors;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.editor.Embedder;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;

/**
 *
 * @author Ondřej Brejla
 */
public class ArrayMacroProcessor extends MacroProcessor {

	@Override
	public void process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2, int start, String macro, boolean endMacro, Embedder embedder) {
		List<Integer> starts = new ArrayList<Integer>();	// array of starts of var assignments
		List<Integer> lengths = new ArrayList<Integer>();	// array of lengths of var assignments

		byte state = -1;									// -1,0 - variable; 1,2 - value
		int numOfBrackets = 0;								// counts nested brackets
		String var = "";									// stores var name and var value
		
		do {
			Token<LatteTokenId> t2 = sequence2.token();
			if (isVariable(state)) {								// var name
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

			if (isValue(state)) {
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

		embedder.embed("<?php ");
		for (int i = 0; i < starts.size(); i += 2) {
			if (starts.get(i) < 0) {										// if position negative prepend with $
				embedder.embed("$");
				embedder.embed(-starts.get(i), lengths.get(i));	// variable
			} else {
				embedder.embed(starts.get(i), lengths.get(i));	// variable
			}

			embedder.embed("=");		// assignment char
			embedder.embed(starts.get(i + 1), lengths.get(i + 1)); // var value
			embedder.embed(";");
		}
		embedder.embed("?>");
	}

	private boolean isVariable(byte state) {
		return state == -1 || state == 0;
	}

	private boolean isValue(byte state) {
		return state == 1 || state == 2;
	}

}
