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

package org.netbeans.modules.php.nette.generators.createcomponent;

import java.util.Collections;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.netbeans.spi.editor.codegen.CodeGeneratorContextProvider;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Ondřej Brejla
 */
public class CreateComponentGenerator implements CodeGenerator {

	private JTextComponent textComp;

	private CreateComponentGeneratorPanel panel;

    private String smallComponentName;

    private String capitalizedComponentName;
    
    private String componentClass;

	/**
	 *
	 * @param context containing JTextComponent and possibly other items registered by {@link CodeGeneratorContextProvider}
	 */
	private CreateComponentGenerator(Lookup context) { // Good practice is not to save Lookup outside ctor
		textComp = context.lookup(JTextComponent.class);
	}

	public static class Factory implements CodeGenerator.Factory {
		public List<? extends CodeGenerator> create(Lookup context) {
			return Collections.singletonList(new CreateComponentGenerator(context));
		}
	}

	/**
	 * The name which will be inserted inside Insert Code dialog
	 */
	public String getDisplayName() {
		return "Create component...";
	}
	
	/**
	 * This will be invoked when user chooses this Generator from Insert Code
	 * dialog
	 */
	public void invoke() {
		if (processDialog()) {
			try {
				String componentFactoryCode = panel.isFormTabSelected() ? 
						generateComponentFactoryCode(panel.getFormName(), panel.getFormClass()) :
						generateComponentFactoryCode(panel.getComponentName(), panel.getComponentClass());
				
				CodeTemplateManager manager = CodeTemplateManager.get(textComp.getDocument());
				CodeTemplate template = manager.createTemporary(componentFactoryCode);
				template.insert(textComp);
			} catch (Exception ex) {
				Exceptions.printStackTrace(ex);
			}
		}
	}

	/**
	 * Returns generated source code for a component specified by its name and class.
	 *
	 * @param componentName
	 * @param componentClass
	 * @return
	 */
	private String generateComponentFactoryCode(String componentName, String componentClass) {
		smallComponentName = EditorUtils.firstLetterSmall(componentName);
		capitalizedComponentName = EditorUtils.firstLetterCapital(componentName);
        this.componentClass = componentClass;

		return "protected function createComponent" + capitalizedComponentName + "(" + (panel.isRegisterInConstructor() ? "$name" : "") + ") {"
				+ "$" + smallComponentName + " = new " + componentClass + "(" + (panel.isRegisterInConstructor() ? "$this, $name" : "") + ");"
				+ "\n"
				+ "\n"
				+ "\n"
				+ (panel.isFormTabSelected() && panel.isCreateValidSubmit() ? "$" + smallComponentName + "->onSubmit[] = " + createValidSubmitCallback() : "")
				+ (panel.isFormTabSelected() && panel.isCreateInvalidSubmit() ? "$" + smallComponentName + "->onInvalidSubmit[] = " + createInvalidSubmitCallback() : "")
				+ (!panel.isRegisterInConstructor() ? "\n\nreturn $" + smallComponentName + ";" : "")
				+ "}"
				+ (panel.isFormTabSelected() && panel.isCreateValidSubmit() && !panel.isCreateClosures() ? "public function validSubmit" + capitalizedComponentName + "(" + componentClass + " $" + smallComponentName + ") {\n\n}" : "")
				+ (panel.isFormTabSelected() && panel.isCreateInvalidSubmit() && !panel.isCreateClosures() ? "public function invalidSubmit" + capitalizedComponentName + "(" + componentClass + " $" + smallComponentName + ") {\n\n}" : "");
	}

    private String createClosureCallback() {
        return "function(" + componentClass + " $" + smallComponentName + ") {\n\n};";
    }

    private String createValidSubmitCallback() {
        return panel.isCreateClosures() ? createClosureCallback() : "callback($this, 'validSubmit" + capitalizedComponentName + "');";
    }

    private String createInvalidSubmitCallback() {
        return panel.isCreateClosures() ? createClosureCallback() : "callback($this, 'invalidSubmit" + capitalizedComponentName + "');";
    }

	/**
	 * Shows dialog for generating component factory code.
	 *
	 * @return True, if OK was clicked, false otherwise.
	 */
	private boolean processDialog() {
		panel = new CreateComponentGeneratorPanel();

		DialogDescriptor dd = new DialogDescriptor(panel, "Create component...", true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION, null);
		
		panel.setDialogDescriptor(dd);

		Object result = DialogDisplayer.getDefault().notify(dd);

		if (result != NotifyDescriptor.OK_OPTION) {
			return false;
		} else {
			return true;
		}
	}

}
