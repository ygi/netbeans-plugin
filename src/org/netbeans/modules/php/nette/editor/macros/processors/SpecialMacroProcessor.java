
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
public class SpecialMacroProcessor extends  MacroProcessor {

	@Override
	public void process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2, int start, String macro, boolean endMacro, Embedder embedder) {
		// include, widget, control, (p)link, extends, ...
		int firstStart = start;
		int whiteSpace = 0;
		boolean toString = true;

		do {
			Token<LatteTokenId> t2 = sequence2.token();
			
			if(whiteSpace < 2) {											// first param ( {mac param ...})
				if(t2.id() == LatteTokenId.VARIABLE || t2.id() == LatteTokenId.STRING) {
					toString = false;										// do not encapsulate with quotes
				}
				if(t2.id() == LatteTokenId.WHITESPACE						// delims parameters
						|| (whiteSpace == 1 && t2.id() == LatteTokenId.COMA) // or delimted by coma!
						|| t2.id() == LatteTokenId.RD)
				{
					whiteSpace++;
					start = sequence.offset() + sequence2.offset() + t2.length();	// start of other params
					
					if(t2.id() == LatteTokenId.RD) {
						start--;											// exclude right delim
						break;
					}
				}

				continue;
			}
			if(t2.id() == LatteTokenId.RD) {
				break;
			}

			length += t2.length();
		} while(sequence2.moveNext());

		String fParam = embedder.getSnapshot().getText().subSequence(firstStart, start).toString();	// get first param
		int trim = (fParam.endsWith(",") || fParam.endsWith(" ")) ? 1 : 0;	// will remove trailing comma or WS

		if(!toString) {
			// if variable or string literal was found in first param, do not encapsulte with quotes
			embedder.embed("<?php ");
			embedder.embed(firstStart, start - firstStart - trim);
		} else {
			// otherwise process as php string
			embedder.embed("<?php \"");
			embedder.embed(firstStart, start - firstStart - trim);
			embedder.embed("\"");
		}
		// for other params create array
		embedder.embed("; array( ");
		embedder.embed(start, length);
		embedder.embed(")?>");
	}

}
