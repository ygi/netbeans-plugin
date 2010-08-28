
package org.netbeans.modules.php.nette.utils;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.openide.util.Exceptions;

/**
 *
 * @author Radek Ježdík
 */
public class LexUtils {

	public static TokenSequence<LatteTopTokenId> getTopSequence(Document document) {
		try {
			return getTopSequence(document.getText(0, document.getLength()));
		} catch(BadLocationException e) {
			Exceptions.printStackTrace(e);
		}
		return null;
	}

	public static TokenSequence<LatteTopTokenId> getTopSequence(String text) {
		TokenHierarchy<String> th = TokenHierarchy.create(text, LatteTopTokenId.language());
		return th.tokenSequence(LatteTopTokenId.language());
	}

}
