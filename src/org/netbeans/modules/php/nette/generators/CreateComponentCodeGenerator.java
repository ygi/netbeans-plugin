package org.netbeans.modules.php.nette.generators;

import java.util.Collections;
import java.util.List;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Ondřej Brejla
 */
public class CreateComponentCodeGenerator implements CodeGenerator {

	JTextComponent textComp;

	CreateComponentPanel panel;

	/**
	 *
	 * @param context containing JTextComponent and possibly other items registered by {@link CodeGeneratorContextProvider}
	 */
	private CreateComponentCodeGenerator(Lookup context) { // Good practice is not to save Lookup outside ctor
		textComp = context.lookup(JTextComponent.class);
	}

	public static class Factory implements CodeGenerator.Factory {

		public List<? extends CodeGenerator> create(Lookup context) {
			return Collections.singletonList(new CreateComponentCodeGenerator(context));
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
				Document doc = textComp.getDocument();
				int position = textComp.getCaretPosition();

				String componentFactoryCode = panel.isFormTabSelected() ? 
						generateComponentFactoryCode(panel.getFormName(), panel.getFormClass()) :
						generateComponentFactoryCode(panel.getComponentName(), panel.getComponentClass());

				doc.insertString(position, componentFactoryCode, null);

				textComp.setDocument(doc);
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
		return "\n\tpublic function createComponent" + EditorUtils.capitalize(componentName) + "(" + (panel.isRegisterInConstructor() ? "$name" : "") + ") {\n"
				+ "\t\t$" + componentName + " = new " + componentClass + "(" + (panel.isRegisterInConstructor() ? "$this, $name" : "") + ");\n"
				+ "\t\t\n"
				+ "\t\t\n"
				+ "\t\t\n"
				+ (panel.isCreateValidSubmit() ? "\t\t$" + componentName + "->onSubmit[] = callback($this, 'validSubmit" + componentName + "');\n" : "")
				+ (panel.isCreateInvalidSubmit() ? "\t\t$" + componentName + "->onInvalidSubmit[] = callback($this, 'invalidSubmit" + componentName + "');\n" : "")
				+ (!panel.isRegisterInConstructor() ? "\t\treturn $" + componentName + ";\n" : "")
				+ "\t}\n"
				+ (panel.isCreateValidSubmit() ? "\n\t\tpublic function validSubmit" + componentName + "(" + componentClass + " $" + componentName + ") {\n\t\t\n\t}\n" : "")
				+ (panel.isCreateInvalidSubmit() ? "\n\t\tpublic function invalidSubmit" + componentName + "(" + componentClass + " $" + componentName + ") {\n\t\t\n\t}\n" : "");
	}

	/**
	 * Shows dialog for generating component factory code.
	 *
	 * @return True, if OK was clicked, false otherwise.
	 */
	private boolean processDialog() {
		panel = new CreateComponentPanel();

		DialogDescriptor dd = new DialogDescriptor(panel, "Create component...");
		Object result = DialogDisplayer.getDefault().notify(dd);
		if (result != NotifyDescriptor.OK_OPTION) {
			return false;
		} else {
			return true;
		}
	}

}
