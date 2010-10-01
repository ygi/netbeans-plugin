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

import java.io.File;
import java.util.HashMap;
import javax.swing.text.JTextComponent;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.php.nette.NetteFramework;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.netbeans.modules.php.nette.utils.FileUtils;

/**
 * Generator of action and/or render methods for presenter.
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
class ActionRenderMethodsGenerator {

	private Object[] actions;

	private File presenterFile;

	/**
	 * Generates new action and/or render methods and insets them into the text component.
	 *
	 * @param actions
	 * @param textComp
	 */
	public void generate(Object[] actions, JTextComponent textComp) {
		this.actions = actions;
		presenterFile = FileUtils.getFile(textComp);
		
		CodeTemplateManager manager = CodeTemplateManager.get(textComp.getDocument());
		CodeTemplate template = manager.createTemporary(createActionRenderMethods());
		template.insert(textComp);
	}

	/**
	 * Returns textual representation of new action and/or render methods which will be added into the presenter file.
	 *
	 * @return
	 */
	private String createActionRenderMethods() {
		ActionRenderMethodChecker armc = new ActionRenderMethodChecker(presenterFile);
		StringBuilder sb = new StringBuilder();

		for (Object wholeAction : actions) {
			HashMap<String, Object> action = (HashMap<String, Object>) wholeAction;
			String actionName = (String) action.get("name");

			if ((Boolean) action.get("action") && !armc.existsActionMethod(actionName)) {
				sb.append(getMethod(actionName, NetteFramework.NETTE_ACTION_METHOD_PREFIX));
			}

			if ((Boolean) action.get("render") && !armc.existsRenderMethod(actionName)) {
				sb.append(getMethod(actionName, NetteFramework.NETTE_RENDER_METHOD_PREFIX));
			}
		}
		
		return sb.toString();
	}

	/**
	 * Returns textual representation of the method for the passed name and type.
	 *
	 * @param name
	 * @param type
	 * @return
	 */
	private String getMethod(String name, String type) {
		return "public function " + type + EditorUtils.firstLetterCapital(name) + "() {\n \n}";
	}

}
