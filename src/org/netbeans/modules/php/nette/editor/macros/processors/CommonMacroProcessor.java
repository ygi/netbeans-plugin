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

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.editor.Embedder;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.modules.php.nette.macros.MacroDefinitions;

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

		boolean ok = false;
		if(!endMacro && macro != null && MacroDefinitions.getMacro(macro) != null) {
			ok = true;
		}
		if(ok) {
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

}
