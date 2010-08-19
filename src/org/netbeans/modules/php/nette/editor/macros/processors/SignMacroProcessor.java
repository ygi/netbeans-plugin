
package org.netbeans.modules.php.nette.editor.macros.processors;

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.editor.Embedder;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;

/**
 *
 * @author Ondřej Brejla
 */
public class SignMacroProcessor extends MacroProcessor {

	@Override
	public void process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2, int start, String macro, boolean endMacro, Embedder embedder) {
		do {
			Token<LatteTokenId> t2 = sequence2.token();

			if(t2.id() == LatteTokenId.RD) {
				break;
			}

			length += t2.length();
		} while (sequence2.moveNext());

		if (isOutputMacro(macro)) {
			embedder.embed("<?php echo");
			embedder.embed(start, length);
			embedder.embed(" ?>");
		} else {
			embedder.embed("<?php ");
			embedder.embed(start, length);
			embedder.embed(" ?>");
		}
	}

	private boolean isOutputMacro(String macro) {
		return macro.equals("_") || macro.equals("=") || macro.equals("!") || macro.equals("!=") || macro.equals("!_");
	}

}
