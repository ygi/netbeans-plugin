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

package org.netbeans.modules.php.nette.generators.actionrender;

import org.netbeans.modules.php.nette.wizards.newpresenter.ActionRenderTableModel;
import org.openide.util.NbBundle;

/**
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public class ActionRenderCodeGeneratorTableModel extends ActionRenderTableModel {

	private ActionRenderMethodChecker methodChecker;

	public ActionRenderCodeGeneratorTableModel(ActionRenderMethodChecker methodChecker) {
        super(
            null,
            new String [] {NbBundle.getMessage(ActionRenderCodeGeneratorTableModel.class, "TXT_action_name"),
					"action<action>()",
					"render<action>()",
					NbBundle.getMessage(ActionRenderCodeGeneratorTableModel.class, "TXT_generate_template")}
        );

		this.methodChecker = methodChecker;
    }

	@Override
	public boolean isCellEditable(int row, int col) {
		if (col != 0) {
			return true;
		}

		return false;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column) {
		String action = (String) getValueAt(row, 0);
		switch (column) {
			case 1: if (methodChecker.existsActionMethod(action)) {
						super.setValueAt(false, row, column);
					} else {
						super.setValueAt(aValue, row, column);
					}
					break;
			case 2: if (methodChecker.existsRenderMethod(action)) {
						super.setValueAt(false, row, column);
					} else {
						super.setValueAt(aValue, row, column);
					}
					break;
			default: super.setValueAt(aValue, row, column);
		}
	}

}
