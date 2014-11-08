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
import org.netbeans.modules.php.nette.editor.completion.items.MacroCompletionItem;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.modules.php.nette.macros.LatteMacro;
import org.netbeans.modules.php.nette.macros.MacroDefinitions;
import org.netbeans.spi.editor.completion.CompletionItem;

/**
 *
 * @author Radek Ježdík
 */
public class MacroCompletionProcessor {

	public List<CompletionItem> process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2,
			Document document, int caretOffset) {

		List<CompletionItem> list = new ArrayList<CompletionItem>();

		String written = "";

		Token<LatteTokenId> token2 = sequence2.token();

		// if caret is position just after left delimiter
		if (token2.id() == LatteTokenId.LD) {
			sequence2.moveNext();
			token2 = sequence2.token();
		}

		// when only {} is written
		if (token2.id() != LatteTokenId.RD) {
			written = token2.text().toString().replace("}", "");
			written = written.substring(0, caretOffset - sequence2.offset() - sequence.offset());
		}

		for (LatteMacro macro : MacroDefinitions.macros) {
			if (macro.getMacroName().startsWith(written)) {
				list.add(
					new MacroCompletionItem(
						macro.getMacroName(),
						sequence2.offset() + sequence.offset(),
						sequence2.offset() + sequence.offset() + token2.length()
					));
			}
		}
		return list;
	}

}
