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
package org.netbeans.modules.php.nette.editor.completion.processors;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.netbeans.spi.editor.completion.CompletionItem;

/**
 *
 * @author Radek Ježdík
 */
public class ParamCompletionProcessor {

	public List<CompletionItem> process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2,
			Document document, int caretOffset) {

		List<CompletionItem> list = new ArrayList<CompletionItem>();

		String macroName = (String) sequence.token().getProperty("macro");
		boolean isAttr = (macroName != null);

		sequence2.moveStart();
		while(isAttr || sequence2.moveNext()) {
			Token<LatteTokenId> token2 = (isAttr ? null : sequence2.token());
			if(!isAttr && sequence2.offset() + sequence.offset() > caretOffset) {
				break;
			}
			if((token2 != null && token2.id() == LatteTokenId.MACRO) || macroName != null) {
				if(!isAttr) {
					macroName = token2.text().toString();
				}
				if(macroName.equals("plink") || macroName.equals("link")
						|| macroName.equals("widget") || macroName.equals("control")
						|| macroName.equals("extends") || macroName.equals("include")
						|| macroName.equals("syntax")) {
					String written = "";					// text written to caret pos
					String whole = "";						// whole text of the param (overwritten by completion)

					int whiteOffset = -1, whiteLength = 0, whiteNum = 0;
					boolean ok = false;

					while(sequence2.moveNext()) {
						token2 = sequence2.token();
						//if processing token after caret position just update whole
						if(sequence2.offset() + sequence.offset() >= caretOffset) {
							if(token2.id() != LatteTokenId.COLON && token2.id() != LatteTokenId.TEXT) {
								break;
							}
							whole += token2.text();
						}

						if(isAttr && whiteNum == 0) {
							whiteNum++;
							whiteOffset = sequence2.offset() + sequence.offset();
						}
						if(whiteNum == 1 && sequence2.offset() + sequence.offset() < caretOffset) {
							written += token2.text();
							whole = written;
							ok = true;
						} else if(whiteNum > 1) {
							ok = false;
							break;
						}

						// counts whitespaces, this completion is used in first param only
						if(token2.id() == LatteTokenId.WHITESPACE && !isAttr) {
							whiteOffset = sequence2.offset() + sequence.offset();
							whiteLength = token2.length();
							whiteNum++;
							if(whiteNum == 1) {
								ok = true;
							}
						}
					}
					if(ok && (macroName.equals("plink") || macroName.equals("link"))) {
						list.addAll(EditorUtils.parseLink(document, written, whiteOffset + whiteLength, whole.length()));
					}
					if(ok && (macroName.equals("widget") || macroName.equals("control"))) {
						list.addAll(EditorUtils.parseControl(document, written, whiteOffset + whiteLength, whole.length()));
					}
					if(ok && (macroName.equals("extends") || macroName.equals("include"))) {
						list.addAll(EditorUtils.parseLayout(document, written, whiteOffset + whiteLength, whole.length()));
					}
					if(ok && macroName.equals("syntax")) {
						list.addAll(EditorUtils.getSyntaxCompletions(written.trim(), whiteOffset + whiteLength, whole.length()));
					}
				}
				break;
			}
		}
		return list;
	}
}
