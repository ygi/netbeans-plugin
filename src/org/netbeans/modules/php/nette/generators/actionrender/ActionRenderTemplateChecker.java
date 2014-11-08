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
import org.netbeans.modules.php.nette.NetteFramework;
import org.netbeans.modules.php.nette.utils.EditorUtils;

/**
 * Checks if template exists for a presenter and an action name.
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public class ActionRenderTemplateChecker {

	private String presenterFileName;

	private String templatesDir;

	/**
	 * Initializes template checker.
	 *
	 * @param presenterFileName
	 * @param templatesDir
	 */
	public ActionRenderTemplateChecker(String presenterFileName, String templatesDir) {
		this.presenterFileName = presenterFileName;
		this.templatesDir = templatesDir;
	}

	/**
	 * Initializes template checker.
	 *
	 * @param presenterFile
	 * @param templatesDir
	 */
	public ActionRenderTemplateChecker(File presenterFile, String templatesDir) {
		this(presenterFile.getName(), templatesDir);
	}

	/**
	 * Checks if exists template for passed action.
	 *
	 * @param action
	 * @return
	 */
	public boolean existsActionTemplate(String action) {
		String presenterName = EditorUtils.extractPresenterName(presenterFileName);
		action = EditorUtils.firstLetterSmall(action);

		File newTemplateFile = new File(templatesDir + "/" + presenterName + "/" + action + NetteFramework.NETTE_LATTE_TEMPLATE_EXTENSION);
		File newDottedTemplateFile = new File(templatesDir + "/" + presenterName + "." + action + NetteFramework.NETTE_LATTE_TEMPLATE_EXTENSION);

		return newTemplateFile.exists() || newDottedTemplateFile.exists();
	}

}
