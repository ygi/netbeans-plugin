/*
 */

package org.netbeans.modules.php.nette.editor.completion.processors;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.netbeans.spi.editor.completion.CompletionItem;

/**
 *
 * @author Radek Ježdík
 */
public class ParamCompletionProcessor {

	public List<CompletionItem> process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2,
			Document document, int caretOffset) {
		
		List<CompletionItem> list = new ArrayList<CompletionItem>();
		
		sequence2.moveStart();
		while (sequence2.moveNext()) {
			Token<LatteTokenId> token2 = sequence2.token();
			if (sequence2.offset() + sequence.offset() > caretOffset) {
				break;
			}
			if (token2.id() == LatteTokenId.MACRO) {
				String ttext = token2.text().toString();
				if (ttext.equals("plink") || ttext.equals("link")
						|| ttext.equals("widget") || ttext.equals("control")
						|| ttext.equals("extends") || ttext.equals("include"))
				{
					String written = "";					// text written to caret pos
					String whole = "";						// whole text of the param (overwritten by completion)

					int whiteOffset = -1, whiteLength = 0, whiteNum = 0;
					boolean ok = false;

					while (sequence2.moveNext()) {
						token2 = sequence2.token();
						//if processing token after caret position just update whole
						if (sequence2.offset() + sequence.offset() >= caretOffset) {
							if(token2.id() != LatteTokenId.COLON && token2.id() != LatteTokenId.TEXT)
								break;
							whole += token2.text();
						}

						if (whiteNum == 1 && sequence2.offset() + sequence.offset() < caretOffset) {
							written += token2.text();
							whole = written;
							ok = true;
						} else if (whiteNum > 1) {
							ok = false;
							break;
						}
						
						// counts whitespaces, this completion is used in first param only
						if (token2.id() == LatteTokenId.WHITESPACE) {
							whiteOffset = sequence2.offset() + sequence.offset();
							whiteLength = token2.length();
							whiteNum++;
							if(whiteNum == 1)
								ok = true;
						}
					}
					if (ok && (ttext.equals("plink") || ttext.equals("link"))) {
						list.addAll(EditorUtils.parseLink(document, written, whiteOffset + whiteLength, whole.length()));
					}
					if (ok && (ttext.equals("widget") || ttext.equals("control"))) {
						list.addAll(EditorUtils.parseControl(document, written, whiteOffset + whiteLength, whole.length()));
					}
					if (ok && (ttext.equals("extends") || ttext.equals("include"))) {
						list.addAll(EditorUtils.parseLayout(document, written, whiteOffset + whiteLength, whole.length()));
					}
				}
			}
		}
		return list;
	}

}
