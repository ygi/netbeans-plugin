
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
public class NoMacroProcessor extends MacroProcessor {

	@Override
	public void process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2, int start, String macro, boolean endMacro, Embedder embedder) {
		Token<LatteTokenId> t2 = sequence2.token();

		if (t2.id() == LatteTokenId.VARIABLE || (t2.id() == LatteTokenId.ERROR && t2.text().equals("$"))) {
			do {
				t2 = sequence2.token();
				if (t2.id() == LatteTokenId.RD || t2.id() == LatteTokenId.PIPE) {
					break;
				}
				length += t2.length();
			} while (sequence2.moveNext());
			// we don't need to write any echo or escaping (too long)
			embedder.embed("<?php ");
			embedder.embed(start, length);
			embedder.embed(" ?>");
		}
	}

}
