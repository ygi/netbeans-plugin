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
public class AttrMacroProcessor extends MacroProcessor {

	@Override
	public void process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2, int start, String macro, boolean endMacro, Embedder embedder) {
		int numOfBrackets = 0;				// counts number of brackets to clearly match nested brackets
		int whiteSpace = 0;					// counts whitespaces (delimits attr calls)
		
		List<Integer> starts = new ArrayList<Integer>();	// array of starts of attr call
		List<Integer> lengths = new ArrayList<Integer>();	// array of lengths of attr call

		do {
			Token<LatteTokenId> t2 = sequence2.token();

			if (whiteSpace == 0) {							// expecting first attr call or right delim }
				if (t2.id() == LatteTokenId.WHITESPACE
						|| t2.id() == LatteTokenId.RD) {	// if found one
					whiteSpace++;
					starts.add(sequence.offset() + sequence2.offset() + t2.length());	//add start for new attr call
					length = 0;								// will calculate below
				}

				continue;
			} else {
				if (t2.id() == LatteTokenId.LNB) {			// if left bracket
					numOfBrackets++;						// add bracket
				}
				if (t2.id() == LatteTokenId.RNB) {			// if right bracket
					numOfBrackets--;						// remove bracket
				}
				if ((t2.id() == LatteTokenId.WHITESPACE || t2.id() == LatteTokenId.RD) && numOfBrackets == 0) {			// expecting another attr call
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
		} while (sequence2.moveNext());

		if (numOfBrackets != 0) {
			lengths.add(length);											// add last length
		}

		embedder.embed("<?php $v");		// $v represents a Html object
		for (int i = 0; i < lengths.size(); i++) {
			// the subsequence is empty or whitespace only
			if(embedder.getSnapshot().getText().subSequence(starts.get(i), starts.get(i) + lengths.get(i)).toString().trim().equals("")) {
				continue;
			}
			embedder.embed("->");		// add -> object access
			embedder.embed(starts.get(i), lengths.get(i));	// and attr call itself
		}
		embedder.embed(";?>");
	}

}
