
package org.netbeans.modules.php.nette.validators;

/**
 *
 * @author Ondřej Brejla
 */
public class NetteEmptyPathValidator implements Validable {

	public boolean validate(Object object) {
		String path = (String) object;

		if (path.trim().isEmpty()) {
			return true;
		}

		return false;
	}

}
