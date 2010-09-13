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
