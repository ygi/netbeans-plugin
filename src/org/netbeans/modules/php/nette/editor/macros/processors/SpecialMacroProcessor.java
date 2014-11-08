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

import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.editor.Embedder;
import org.netbeans.modules.php.nette.editor.hints.HintFactory;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;

/**
 *
 * @author Ondřej Brejla
 */
public class SpecialMacroProcessor extends  MacroProcessor {

	@Override
	public void process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2, int start, String macro, boolean endMacro, Embedder embedder) {
		// include, widget, control, (p)link, extends, ...
		int firstStart = start;
		int firstEnd = 0;
		int whiteSpace = 0;
		boolean toString = true;

		if(macro.equals("widget")) {
			createDepracatedHint(embedder, sequence.offset() + 1, "widget".length());
		}

		do {
			Token<LatteTokenId> t2 = sequence2.token();

			if(whiteSpace < 2) {												// first param ( {mac param ...})
				if(t2.id() == LatteTokenId.VARIABLE || t2.id() == LatteTokenId.STRING) {
					toString = false;											// do not encapsulate with quotes
				}
				if(t2.id() == LatteTokenId.WHITESPACE							// delimits parameters
						|| (whiteSpace == 1 && t2.id() == LatteTokenId.COMA)	// or delimited by coma!
						|| t2.id() == LatteTokenId.RD)
				{
					whiteSpace++;
					firstEnd = sequence.offset() + sequence2.offset();			// end of first param
					if(t2.id() == LatteTokenId.COMA) {
						start = sequence.offset() + sequence2.offset() + t2.length();			// start of other params
					}
				}

				continue;
			}
			if(firstEnd > 0 && start < firstEnd) {
				if(t2.id() == LatteTokenId.COMA) {
					start = sequence.offset() + sequence2.offset() + t2.length();			// start of other params
					continue;
				}
				start = sequence.offset() + sequence2.offset();
			}

			if(t2.id() == LatteTokenId.RD) {
				break;
			}

			length += t2.length();
		} while(sequence2.moveNext());

		if(!toString) {
			// if variable or string literal was found in first param, do not encapsulte with quotes
			embedder.embed("<?php ");
			embedder.embed(firstStart, firstEnd - firstStart);
		} else {
			// otherwise process as php string
			embedder.embed("<?php \"");
			embedder.embed(firstStart, firstEnd - firstStart);
			embedder.embed("\"");
		}

		// for other params create array
		embedder.embed("; array( ");

		// encapsulates simple text key with quotes, because of php keywords (class, ...)
		String params = embedder.getSnapshot().getText().subSequence(start, start+length).toString();
		params = params.replaceAll("([ ,(\\[])([a-zA-Z]+) *=>", "$1 \"$2\" =>");
		embedder.embed(params);

		embedder.embed(")?>");
	}

	private void createDepracatedHint(Embedder embedder, int start, int length) {
		Document doc = embedder.getSnapshot().getSource().getDocument(false);
		HintFactory.add(doc, HintFactory.WIDGET_MACRO_DEPRECATED, start, length);
	}

}
