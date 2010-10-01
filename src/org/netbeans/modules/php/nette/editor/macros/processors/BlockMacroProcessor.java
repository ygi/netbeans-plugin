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

/**
 *
 * @author Ondřej Brejla
 */
public class BlockMacroProcessor extends MacroProcessor {

	@Override
	public void process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2, int start, String macro, boolean endMacro, Embedder embedder) {
		if (!endMacro) {												// is not end macro
			boolean pipe = false;									// helper delimiter found

			do {
				Token<LatteTokenId> t2 = sequence2.token();

				if (pipe && t2.id() != LatteTokenId.PIPE) {			// if pipe found and it is not php OR (||)
					length--;										// end cycle

					break;
				} else {
					if(!pipe && t2.id() == LatteTokenId.PIPE) {		// helper delim found
						pipe = true;
					} else {
						pipe = false;
					}
				}
				if (t2.id() == LatteTokenId.RD) {
					break;
				}
				
				length += t2.length();
			} while(sequence2.moveNext());

			if(macro.equals("block") || macro.equals("snippet") || macro.equals("ifCurrent") || macro.equals("capture")) {
				// for block and snippet process as string only
				embedder.embed("<?php \"");
				embedder.embed(start, length);
				embedder.embed("\";{?>");
				// if it is not n: tag or attribute and it is a block
				if (getMacroName() == null && macro.equals("block")) {
					incNumberOfBlocks();			// counts number of {block} macros (last closing can be ommited)
				}
			} else {
				// for if, foreach, ... process as <?php macro(attr) { ?>
				embedder.embed("<?php " + macro + "(");
				if(macro.equals("ifCurrent")) {
					embedder.embed("\"");
				}
				embedder.embed(start, length);
				if(macro.equals("ifCurrent")) {
					embedder.embed("\"");
				}
				embedder.embed(");{");
				if (macro.equals("foreach")) {		// in case of foreach create $iterator variable
					embedder.embed("$iterator=new SmartCachingIterator;");
				}
				embedder.embed("?>");
			}
			// in case of n:tag
			// FIXME: possible error, try <n:block tag and n:block="" attr at the same template?
			if (getMacroName() != null) {
				int i = getTags().remove(getTags().size() - 1);
				getTags().add(i + 1);
			}
		} else {
			// for closing macros just create block closing bracket }
			embedder.embed("<?php } ?>");
			if (macro.equals("block")) {
				decNumberOfBlocks();				// in case of {block} macro only (last closing can be ommited)
			}
		}
	}

}
