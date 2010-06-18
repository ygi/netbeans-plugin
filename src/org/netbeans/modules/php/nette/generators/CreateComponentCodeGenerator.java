package org.netbeans.modules.php.nette.generators;

import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

public class CreateComponentCodeGenerator implements CodeGenerator {

	JTextComponent textComp;

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

		CreateComponentPanel panel = new CreateComponentPanel();

		JOptionPane.showMessageDialog(textComp, panel, "Create component...", JOptionPane.PLAIN_MESSAGE);

		try {
			Document doc = textComp.getDocument();

			int position = textComp.getCaretPosition();

			String componentName = panel.getComponentName();
			doc.insertString(position, "\n\tpublic function createComponent" + EditorUtils.capitalize(componentName) + "() {\n"
					+ "\t\t$" + componentName + " = new " + panel.getComponentClass() + "();\n"
					+ "\t\t\n"
					+ "\t\t\n"
					+ "\t\t\n"
					+ "\t\treturn $" + componentName + ";\n"
					+ "\t}", null);

			textComp.setDocument(doc);
		} catch (Exception ex) {
			Exceptions.printStackTrace(ex);
		}
	}

}
