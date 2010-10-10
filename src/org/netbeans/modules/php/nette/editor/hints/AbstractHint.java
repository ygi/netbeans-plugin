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

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Radek Ježdík
 */
public abstract class AbstractHint {

	protected Document doc;
	protected int start;
	protected int length;

	public AbstractHint(Document doc, int start, int length) {
		this.doc = doc;
		this.start = start;
		this.length = length;
	}

	abstract public ErrorDescription getErrorDescription();

	protected Position getStart() {
		try {
			return doc.createPosition(start);
		} catch(BadLocationException ex) {
			Exceptions.printStackTrace(ex);
		}
		return null;
	}

	protected Position getEnd() {
		try {
			return doc.createPosition(start + length);
		} catch(BadLocationException ex) {
			Exceptions.printStackTrace(ex);
		}
		return null;
	}

	public static String getDescription(String name) {
		return NbBundle.getMessage(AbstractHint.class, name);
	}
}
