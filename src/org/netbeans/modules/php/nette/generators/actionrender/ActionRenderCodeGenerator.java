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
import java.util.Collections;
import java.util.List;
import javax.swing.text.JTextComponent;
import org.netbeans.modules.php.nette.utils.FileUtils;
import org.netbeans.modules.php.nette.wizards.newpresenter.ActionRenderVisualPanel;
import org.netbeans.spi.editor.codegen.CodeGenerator;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;

/**
 * 
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public class ActionRenderCodeGenerator implements CodeGenerator {

	JTextComponent textComp;
	
	private ActionRenderVisualPanel panel;
	
	/**
	 *
	 * @param context containing JTextComponent and possibly other items registered by {@link CodeGeneratorContextProvider}
	 */
	private ActionRenderCodeGenerator(Lookup context) { // Good practice is not to save Lookup outside ctor
		textComp = context.lookup(JTextComponent.class);
	}

	public static class Factory implements CodeGenerator.Factory {

		public List<? extends CodeGenerator> create(Lookup context) {
			return Collections.singletonList(new ActionRenderCodeGenerator(context));
		}
	}

	/**
	 * The name which will be inserted inside Insert Code dialog
	 */
	public String getDisplayName() {
		return "Add action and/or render method...";
	}

	/**
	 * This will be invoked when user chooses this Generator from Insert Code
	 * dialog
	 */
	public void invoke() {
		if (processDialog()) {
			ActionRenderMethodsGenerator armg = new ActionRenderMethodsGenerator();
			armg.generate(panel.getActions(), textComp);

			ActionRenderTemplatesGenerator artg = new ActionRenderTemplatesGenerator();
			artg.generate(panel.getActions(), getPresenterFile().getName(), panel.getTemplatesDirectory(), panel.isDottedNotationSelected());
		}
	}

	/**
	 * Processes dialog for adding action and/or render methods.
	 *
	 * @return
	 */
	private boolean processDialog() {
		ActionRenderMethodChecker methodChecker = new ActionRenderMethodChecker(getPresenterFile());
		panel = new ActionRenderVisualPanel(new ActionRenderCodeGeneratorTableModel(methodChecker));

		panel.setMethodChecker(methodChecker);
		panel.setPresentersDirectory(getPresenterDir());

		DialogDescriptor dd = new DialogDescriptor(panel, "Add action and/or render method...", true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION, null);

		Object result = DialogDisplayer.getDefault().notify(dd);

		if (result != NotifyDescriptor.OK_OPTION) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns presenter file.
	 *
	 * @return
	 */
	private File getPresenterFile() {
		return FileUtils.getFile(textComp);
	}

	/**
	 * Returns directory of the current textComp's presenter file.
	 *
	 * @return
	 */
	private String getPresenterDir() {
        String presenterPath = getPresenterFile().getPath();
		
		return presenterPath.replaceAll("/" + getPresenterFile().getName(), "");
	}

}
