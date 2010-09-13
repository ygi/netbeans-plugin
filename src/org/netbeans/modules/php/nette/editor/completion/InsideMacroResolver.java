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

package org.netbeans.modules.php.nette.editor.completion;

import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.editor.completion.processors.HelperCompletionProcessor;
import org.netbeans.modules.php.nette.editor.completion.processors.MacroCompletionProcessor;
import org.netbeans.modules.php.nette.editor.completion.processors.ParamCompletionProcessor;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author Radek Ježdík
 */
public class InsideMacroResolver {

	public static void resolve(CompletionResultSet completionResultSet, TokenSequence<LatteTopTokenId> sequence,
			Document document, int caretOffset) {
		Token<LatteTopTokenId> token = sequence.token();
		
		// inside macro completion
		TokenHierarchy<CharSequence> th2 = TokenHierarchy.create(token.text(), LatteTokenId.language());
		TokenSequence<LatteTokenId> sequence2 = th2.tokenSequence(LatteTokenId.language());

		// determining if caret is positioned in specially treated macros:
		// (p)link, widget/control, extends, include
		// which provide uncommon completion (presenter names, components, layouts)
		ParamCompletionProcessor pcp = new ParamCompletionProcessor();
		completionResultSet.addAllItems(pcp.process(sequence, sequence2, document, caretOffset));

		// moving sequence for inside macro completion
		sequence2.move(caretOffset - sequence.offset());

		if (sequence2.movePrevious() || sequence2.moveNext()) {
			Token<LatteTokenId> token2 = sequence2.token();
			if(sequence.offset() + sequence2.offset() + token2.length() < caretOffset) {
				if(sequence2.moveNext()) {
					token2 = sequence2.token();
				}
			}
			// for macro name completion ( {macro| ..} )
			if (token2.id() == LatteTokenId.MACRO || token2.id() == LatteTokenId.LD) {
				MacroCompletionProcessor pcp2 = new MacroCompletionProcessor();
				completionResultSet.addAllItems(pcp2.process(sequence, sequence2, document, caretOffset));
			}
			//helper completion
			if(token2.id() == LatteTokenId.PIPE || token2.id() == LatteTokenId.TEXT) {
				HelperCompletionProcessor pcp2 = new HelperCompletionProcessor();
				completionResultSet.addAllItems(pcp2.process(sequence, sequence2, document, caretOffset));
			}
		}
	}

}
