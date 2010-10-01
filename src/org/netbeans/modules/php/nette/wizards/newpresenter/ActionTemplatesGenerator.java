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

package org.netbeans.modules.php.nette.wizards.newpresenter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;

/**
 * Generates action templates.
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public class ActionTemplatesGenerator {

	private Object[] actions;

	/**
	 * Generates templates for passed actions array.
	 *
	 * @param actions
	 * @param presenterName
	 * @param templatesDirectory
	 * @param dottedNotation
	 */
	public void generate(Object[] actions, String presenterName, String templatesDirectory, boolean dottedNotation) {
		this.actions = actions;
		
		if (isTemplateForGeneration()) {
            File templatesDir = null;
            String latteTemplatePrefix = null;
			presenterName = presenterName.replaceAll(".php", "");

            if (dottedNotation) {
                templatesDir = new File(templatesDirectory);
                latteTemplatePrefix = presenterName + ".";
            } else {
                templatesDir = new File(templatesDirectory + "/" + presenterName);
                templatesDir.mkdirs();
                latteTemplatePrefix = "";
            }

            FileObject foTemplatesDir = FileUtil.toFileObject(templatesDir);
            DataFolder templatesDf = DataFolder.findFolder(foTemplatesDir);

			try {
            FileObject latteTemplate = FileUtil.getConfigFile("Templates/Nette Framework/LatteTemplate.phtml");
            DataObject latteDTemplate = DataObject.find(latteTemplate);

            for (Object wholeAction : actions) {
                HashMap<String, Object> action = (HashMap<String, Object>) wholeAction;

                boolean generateTemplate = (Boolean) action.get("template");

                if (generateTemplate) {
                    String actionName = (String) action.get("name");
                    latteDTemplate.createFromTemplate(templatesDf, latteTemplatePrefix + EditorUtils.firstLetterSmall(actionName));
                }
            }
			} catch (IOException ex) {
				Exceptions.printStackTrace(ex);
			}

            FileUtil.refreshAll();
        }
	}

	/**
	 * Checks if some of the actions shoud have a template.
	 *
	 * @return
	 */
	private boolean isTemplateForGeneration() {
        for (Object wholeAction : actions) {
            HashMap<String, Object> action = (HashMap<String, Object>) wholeAction;

            if ((Boolean) action.get("template")) {
                return true;
            }
        }

        return false;
    }

}
