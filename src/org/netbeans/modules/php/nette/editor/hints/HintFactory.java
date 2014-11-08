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

import javax.swing.text.Document;

/**
 *
 * @author Radek Ježdík
 */
public enum HintFactory {

	WIDGET_MACRO_DEPRECATED("widgetMacroDeprecated"),
	ASSIGN_MACRO_DEPRECATED("assignMacroDeprecated"),
	VAR_ASSIGN_SYNTAX("varMacroAssignSyntax"),
	PHP_ARRAY_SYNTAX("phpArraySyntax");

	private final String name;

	private HintFactory(String s) {
		name = s;
	}

	public String getName() {
		return name;
	}

	public static void add(Document doc, HintFactory name, int start, int length) {
		if(!HintsSettings.isVisible(name)) {
			return;
		}
		AbstractHint error = null;
		switch(name) {
			case VAR_ASSIGN_SYNTAX:
				error = new VarAssignSyntaxHint(doc, start, length);
				break;
			case ASSIGN_MACRO_DEPRECATED:
				error = new AssignMacroDeprecatedHint(doc, start, length);
				break;
			case WIDGET_MACRO_DEPRECATED:
				error = new WidgetMacroDeprecatedHint(doc, start, length);
				break;
			case PHP_ARRAY_SYNTAX:
				error = new PhpArraySyntaxHint(doc, start, length);
				break;
		}
		HintsCollector.getFor(doc).addErrorDescription(error.getErrorDescription());
	}

}
