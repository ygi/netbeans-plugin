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
import org.netbeans.modules.php.nette.editor.completion.items.HelperCompletionItem;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.modules.php.nette.macros.MacroDefinitions;
import org.netbeans.spi.editor.completion.CompletionItem;

/**
 *
 * @author Radek Ježdík
 */
public class HelperCompletionProcessor {

	public List<CompletionItem> process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2,
			Document document, int caretOffset) {

		List<CompletionItem> list = new ArrayList<CompletionItem>();

		Token<LatteTokenId> token2 = sequence2.token();

		Token<LatteTokenId> token3 = token2;

		// preceding token of helper name should be PIPE token
		if(token2.id() == LatteTokenId.TEXT) {
			sequence2.movePrevious();
			token3 = sequence2.token();
		}

		sequence2.moveNext();

		// is it PIPE token
		if(token3 != null && token3.id() == LatteTokenId.PIPE) {
			String written = token2.text().toString();

			// if caret is position right after pipe char (don't overwrite it)
			if(written.equals("|")) {
				written = "";
			}

			for(String helper : MacroDefinitions.helpers) {
				if(helper.startsWith(written)) {
					list.add(new HelperCompletionItem(
						helper,
						sequence2.offset() + sequence.offset(),
						sequence2.offset() + sequence.offset() + written.length())
					);
				}
			}
		}
		return list;
	}
}
