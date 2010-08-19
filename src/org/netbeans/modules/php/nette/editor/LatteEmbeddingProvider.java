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

		return embedder.getEmbeddings();
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
