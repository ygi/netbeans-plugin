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
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.netbeans.spi.editor.hints.ChangeInfo;
import org.netbeans.spi.editor.hints.EnhancedFix;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.Fix;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.util.Exceptions;


/**
 *
 * @author Radek Ježdík
 */
class VarAssignSyntaxHint extends AbstractHint {

	private static final String name = "hint.varMacroAssignSyntax";

	public VarAssignSyntaxHint(Document doc, int start, int length) {
		super(doc, start, length);
	}

	@Override
	public ErrorDescription getErrorDescription() {
		List<Fix> fixes = new ArrayList<Fix>();
		fixes.add(new RemoveAssignFix());
		return ErrorDescriptionFactory.createErrorDescription(Severity.HINT, getDescription(name), fixes, doc, getStart(), getEnd());
	}

	private class RemoveAssignFix implements EnhancedFix {

		private static final String name = "hint.varMacroAssignSyntax.fix";

		public CharSequence getSortText() {
			return "a";
		}

		public String getText() {
			return AbstractHint.getDescription(name);
		}

		public ChangeInfo implement() throws Exception {
			String text = doc.getText(start, length);
			final String replaced = text.replaceAll("(\\$?[a-zA-Z0-9_]+ *=)(\\>)", "$1");
			
			final BaseDocument bdoc = (BaseDocument) doc;
			bdoc.runAtomic(new Runnable() {
				public void run() {
					try {
						bdoc.remove(start, length);
						bdoc.insertString(start, replaced, null);
					} catch(BadLocationException ex) {
						Exceptions.printStackTrace(ex);
					}
				}
			});
			return null;
		}
	}

}
