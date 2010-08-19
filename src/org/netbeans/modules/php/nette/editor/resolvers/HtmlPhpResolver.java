
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
