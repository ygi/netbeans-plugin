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
import java.util.Collection;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpClass.Method;
import org.netbeans.modules.php.nette.NetteFramework;
import org.netbeans.modules.php.nette.utils.EditorUtils;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 * Checks if action and/or render method exists in passed presenter file.
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public class ActionRenderMethodChecker {

	private File presenterFile;

	private EditorSupport editorSupport;

	private PhpClass phpClass;

	private Collection<Method> classMethods;

	/**
	 * Initializes object for a presenter file.
	 *
	 * @param presenterFile
	 */
	public ActionRenderMethodChecker(File presenterFile) {
		this.presenterFile = presenterFile;
		editorSupport = Lookup.getDefault().lookup(EditorSupport.class);

		phpClass = getPhpClass();
		if (phpClass != null) {
			classMethods = phpClass.getMethods();
		}
	}

	/**
	 * Checks if action method 'action' exists or not in the presenter file.
	 *
	 * @param action
	 * @return
	 */
	public boolean existsActionMethod(String action) {
		return existsMethod(action, NetteFramework.NETTE_ACTION_METHOD_PREFIX);
	}

	/**
	 * Checks if render method 'action' exists or not in the presenter file-.
	 *
	 * @param action
	 * @return
	 */
	public boolean existsRenderMethod(String action) {
		return existsMethod(action, NetteFramework.NETTE_RENDER_METHOD_PREFIX);
	}

	/**
	 * Checks if method of a given type exists or not in the presenter file.
	 *
	 * @param action
	 * @param type
	 * @return
	 */
	private boolean existsMethod(String action, String type) {
		if (classMethods != null) {
			for (Method classMethod : classMethods) {
				if (classMethod.getName().equals(type + EditorUtils.firstLetterCapital(action))) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns php class which name corresponds with a presenter file name.
	 *
	 * @return
	 */
	private PhpClass getPhpClass() {
		Collection<PhpClass> classes = editorSupport.getClasses(FileUtil.toFileObject(presenterFile));

		for (PhpClass localPhpClass : classes) {
			if (localPhpClass.getName().contains(presenterFile.getName().replaceAll(NetteFramework.NETTE_PRESENTER_EXTENSION, ""))) {
				return localPhpClass;
			}
		}

		return null;
	}

}
