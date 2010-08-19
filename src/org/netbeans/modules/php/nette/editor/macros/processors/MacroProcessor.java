
package org.netbeans.modules.php.nette.editor.macros.processors;

import java.util.List;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.editor.Embedder;
import org.netbeans.modules.php.nette.editor.resolvers.TemplateResolver;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;

/**
 *
 * @author Ondřej Brejla
 */
abstract public class MacroProcessor {

	protected int length = 0;

	protected String getMacroName() {
		return TemplateResolver.getMacroName();
	}

	protected void setMacroName(String macroName) {
		TemplateResolver.setMacroName(macroName);
	}

	protected List<Integer> getTags() {
		return TemplateResolver.getTags();
	}

	protected void incNumberOfBlocks() {
		TemplateResolver.incNumberOfBlocks();
	}

	protected void decNumberOfBlocks() {
		TemplateResolver.decNumberOfBlocks();
	}

	abstract public void process(TokenSequence<LatteTopTokenId> sequence, TokenSequence<LatteTokenId> sequence2, int start, String macro, boolean endMacro, Embedder embedder);

}
