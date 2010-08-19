/*
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
