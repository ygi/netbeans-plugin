/*
 *  The MIT License
 *
 *  Copyright (c) 2010 Radek Ježdík <redhead@email.cz>, Ondřej Brejla <ondrej@brejla.cz>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package org.netbeans.modules.php.nette.editor.hints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.util.NbBundle;

/**
 *
 * @author Radek Ježdík
 */
public class HintsCollector {

	static HashMap<Document, HintsCollector> instances = new HashMap<Document, HintsCollector>();


	Document doc;

	List<ErrorDescription> errors = new ArrayList<ErrorDescription>();

	List<Integer> hashCodes = new ArrayList<Integer>();

	public HintsCollector(Document doc) {
		this.doc = doc;
	}

	public static HintsCollector getFor(Document doc) {
		HintsCollector hc = instances.get(doc);
		if(hc == null) {
			hc = new HintsCollector(doc);
			instances.put(doc, hc);
		}
		return hc;
	}

	public void setupHints() {
		HintsController.setErrors(doc, "lattewarnings", errors);
		instances.remove(doc);
	}

	public void addHint(String desc, final int start, final int end) {
		desc = getDescription(desc);
		ErrorDescription ed = ErrorDescriptionFactory.createErrorDescription(Severity.HINT, desc,
				doc, createPosition(start), createPosition(end));
		if(!hashCodes.contains(ed.hashCode())) {
			errors.add(ed);
			hashCodes.add(ed.hashCode());
		}
	}

	public void addErrorDescription(ErrorDescription error) {
		if(!hashCodes.contains(error.hashCode())) {
			errors.add(error);
			hashCodes.add(error.hashCode());
		}
	}

	private String getDescription(String name) {
		return NbBundle.getMessage(HintsCollector.class, name);
	}

	private Position createPosition(final int pos) {
		return new Position() {
			@Override
			public int getOffset() {
				return pos;
			}
		};
	}

}
