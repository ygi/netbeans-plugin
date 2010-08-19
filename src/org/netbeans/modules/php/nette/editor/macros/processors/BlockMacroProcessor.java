
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

			if(macro.equals("block") || macro.equals("snippet")) {
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
				embedder.embed(start, length);
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
