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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.text.Document;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.EmbeddingProvider;
import org.netbeans.modules.parsing.spi.SchedulerTask;
import org.netbeans.modules.parsing.spi.TaskFactory;
import org.netbeans.modules.php.nette.editor.hints.HintsCollector;
import org.netbeans.modules.php.nette.lexer.LatteTokenId;

/**
 * Provides embedded languages for HTML or LATTE tokens (language is denoted by mime-type)
 * @author Radek Ježdík
 */
public class LatteEmbeddingProvider extends EmbeddingProvider {

	@Override
	public List<Embedding> getEmbeddings(Snapshot snapshot) {
		// for sending atributes for LatteLexer (dynamic variables)
		// may be not necessary
		Document doc = snapshot.getSource().getDocument(true);
		InputAttributes inputAttributes = new InputAttributes();
		LatteParseData tmplParseData = new LatteParseData(doc);
		inputAttributes.setValue(LatteTokenId.language(), LatteParseData.class, tmplParseData, false);
		//inputAttributes.setValue(LatteTokenId.language(), "document", doc, false);
		doc.putProperty(InputAttributes.class, inputAttributes);

		TemplateEmbedder embedder = new TemplateEmbedder(snapshot);

		List<Embedding> embeds = embedder.getEmbeddings();

		HintsCollector.getFor(doc).setupHints();

		return embeds;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public void cancel() {
		//do nothing
	}

	/**
	 * Factory for creating new LatteEmbeddingProvider.
	 */
	public static final class Factory extends TaskFactory {

		@Override
		public Collection<SchedulerTask> create(final Snapshot snapshot) {
			return Collections.<SchedulerTask>singletonList(new LatteEmbeddingProvider());
		}

	}

}
