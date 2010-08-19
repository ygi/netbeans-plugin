
package org.netbeans.modules.php.nette.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.parsing.api.Embedding;
import org.netbeans.modules.parsing.api.Snapshot;

/**
 *
 * @author Ondřej Brejla
 */
abstract public class Embedder {

	private Snapshot snapshot;

	/* Stores all embeddings with text/x-php mime */
	private List<Embedding> embeddings = new ArrayList<Embedding>();

	public Embedder(Snapshot snapshot) {
		this.snapshot = snapshot;
	}

	public boolean embed(String text) {
		return embeddings.add(snapshot.create(text, getMimeType()));
	}

	public boolean embed(int start, int length) {
		return embeddings.add(snapshot.create(start, length, getMimeType()));
	}

	public List<Embedding> getEmbeddings() {
		//merges embeddings into one piece
		if (!embeddings.isEmpty()) {
			List<Embedding> result = new ArrayList<Embedding>();		// embedding result
			
			result.add(Embedding.create(embeddings));

			return result;						// return embedding
		} else {
			return Collections.emptyList();		// no embedding
		}
	}

	public Snapshot getSnapshot() {
		return snapshot;
	}

	abstract protected String getMimeType();

}
