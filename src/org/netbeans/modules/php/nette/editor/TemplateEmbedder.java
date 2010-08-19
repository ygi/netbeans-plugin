
package org.netbeans.modules.php.nette.editor;

import org.netbeans.modules.php.nette.editor.resolvers.HtmlPhpResolver;
import org.netbeans.modules.php.nette.editor.resolvers.LatteResolver;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.nette.editor.resolvers.TemplateResolver;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;

/**
 *
 * @author Ondřej Brejla
 */
public class TemplateEmbedder extends Embedder {

	public TemplateEmbedder(Snapshot snapshot) {
		super(snapshot);
	}

	@Override
	protected String getMimeType() {
		return FileUtils.PHP_MIME_TYPE;
	}

	@Override
	public List<Embedding> getEmbeddings() {
		// Initializes template counters.
		TemplateResolver.init();
		// TODO: neprochazet celou sekvenci (ale par radku pred a po caret)
		// jestli je to vubec mozny...
		TokenHierarchy<CharSequence> th = TokenHierarchy.create(getSnapshot().getText(), LatteTopTokenId.language());
		TokenSequence<LatteTopTokenId> sequence = th.tokenSequence(LatteTopTokenId.language());

		sequence.moveStart();

		LatteResolver latteResolver = new LatteResolver(this);
		HtmlPhpResolver htmlPhpResolver = new HtmlPhpResolver(this);
		
		while(sequence.moveNext()) {
			Token t = sequence.token();
			if(t.id() == LatteTopTokenId.LATTE) {
				latteResolver.solve(t, sequence);										// deals with all latte macros
			} else {
				htmlPhpResolver.solve(t, sequence);
			}
		}

		if (isAllowedBlockOpened()) {
			embed("<?php } ?>");
		}
		
		return super.getEmbeddings();
	}

	private boolean isAllowedBlockOpened() {
		return TemplateResolver.getNumberOfBlocks() == 1;
	}

}
