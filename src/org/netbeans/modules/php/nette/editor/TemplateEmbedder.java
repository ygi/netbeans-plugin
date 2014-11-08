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
import org.netbeans.modules.php.nette.utils.SyntaxUtils;

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
				SyntaxUtils.findArrayForHint(getSnapshot().getSource().getDocument(false), sequence);
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
