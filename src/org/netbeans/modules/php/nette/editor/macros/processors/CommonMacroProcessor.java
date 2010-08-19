
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
public class CommonMacroProcessor extends MacroProcessor {

	@Override
	public void process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2, int start, String macro, boolean endMacro, Embedder embedder) {
		boolean toString = true;									// encapsulate with string quotes?

		do {
			Token<LatteTokenId> t2 = sequence2.token();
			if (t2.id() == LatteTokenId.VARIABLE || t2.id() == LatteTokenId.STRING) {
				toString = false;	//if there is variable or string literal do not "convert" to string
			}
			if (t2.id() == LatteTokenId.RD) {
				break;
			}
			length += t2.length();
		} while (sequence2.moveNext());

		if (!toString) {
			// if there is a string literal or variable, do not add quotes
			embedder.embed("<?php ");
			embedder.embed(start, length);
			embedder.embed(" ?>");
		} else {
			// otherwise encasulate parametr with double quotes
			embedder.embed("<?php \"");
			embedder.embed(start, length);
			embedder.embed("\"?>");
		}
	}

}
