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

package org.netbeans.modules.php.nette.editor.resolvers;

import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.editor.Embedder;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;

/**
 *
 * @author Ondřej Brejla
 */
public class HtmlPhpResolver extends TemplateResolver {

	public HtmlPhpResolver(Embedder embedder) {
		super(embedder);
	}

	@Override
	public void solve(Token t, TokenSequence<LatteTopTokenId> sequence) {
		if (t.id() == LatteTopTokenId.HTML_TAG || t.id() == LatteTopTokenId.LATTE_TAG) {
			String tag = t.text().toString();
			
			if(isOpeningTag(tag)) {			
				if(t.id() == LatteTopTokenId.LATTE_TAG) {
					setMacroName((String) t.getProperty("macro"));		// if <n:tag, store macro name
				}

				getTags().add(0);										// counts nesting
			} else if (isClosingTag(tag)) {
				if (getTags().size() > 0) {									
					int c = removeLastTag();							// Opening
					if (c > 0) {										// if there are some code blocks
						closeAllCodeBlocks(c);
					}
				}
				setMacroName(null);
			} else if (tag.equals(">") && t.id() == LatteTopTokenId.LATTE_TAG) {
				setMacroName(null);										// do nothing here
			}
		}
		// deals as html/php (will color all HTML tags appropriately)
		embedder.embed(sequence.offset(), t.length());
	}

	private boolean isOpeningTag(String tag) {
		return tag.startsWith("<") && tag.charAt(1) != '/';
	}

	private boolean isClosingTag(String tag) {
		return tag.equals("/>") || tag.startsWith("</");
	}

	private int removeLastTag() {
		return getTags().remove(getTags().size() - 1);
	}

	private void closeAllCodeBlocks(int c) {
		embedder.embed("<?php ");
		for (int i = 0; i < c; i++) {
			embedder.embed("}");
		}
		embedder.embed(" ?>");
	}

}
