/*
 */

package org.netbeans.modules.php.nette.editor.completion.processors;

import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
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
