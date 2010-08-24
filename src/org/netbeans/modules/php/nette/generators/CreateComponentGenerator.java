package org.netbeans.modules.php.nette.generators;

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

	JTextComponent textComp;

	CreateComponentGeneratorPanel panel;

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
		String smallComponentName = EditorUtils.firstLetterSmall(componentName);
		String capitalizedComponentName = EditorUtils.firstLetterCapital(componentName);

		return "protected function createComponent" + capitalizedComponentName + "(" + (panel.isRegisterInConstructor() ? "$name" : "") + ") {"
				+ "$" + smallComponentName + " = new " + componentClass + "(" + (panel.isRegisterInConstructor() ? "$this, $name" : "") + ");"
				+ "\n"
				+ "\n"
				+ "\n"
				+ (panel.isFormTabSelected() && panel.isCreateValidSubmit() ? "$" + smallComponentName + "->onSubmit[] = callback($this, 'validSubmit" + capitalizedComponentName + "');" : "")
				+ (panel.isFormTabSelected() && panel.isCreateInvalidSubmit() ? "$" + smallComponentName + "->onInvalidSubmit[] = callback($this, 'invalidSubmit" + capitalizedComponentName + "');" : "")
				+ (!panel.isRegisterInConstructor() ? "return $" + smallComponentName + ";" : "")
				+ "}"
				+ (panel.isFormTabSelected() && panel.isCreateValidSubmit() ? "public function validSubmit" + capitalizedComponentName + "(" + componentClass + " $" + smallComponentName + ") {\n\t\t\n\t}" : "")
				+ (panel.isFormTabSelected() && panel.isCreateInvalidSubmit() ? "public function invalidSubmit" + capitalizedComponentName + "(" + componentClass + " $" + smallComponentName + ") {\n\t\t\n\t}" : "");
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
