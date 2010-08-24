
package org.netbeans.modules.php.nette.validators;

import java.io.File;

/**
 *
 * @author Ondřej Brejla
 */
public class NetteLoaderPathValidator implements Validable {

	public boolean validate(Object object) {
		String path = (String) object;

		File f = new File(path + "/loader.php");

		if (f.exists()) {
			return true;
		}

		return false;
	}

}
