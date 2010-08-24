
package org.netbeans.modules.php.nette.validators;

/**
 *
 * @author Ondřej Brejla
 */
public class NetteClassNameValidator implements Validable {

	public boolean validate(Object object) {
		String name = (String) object;

		return !name.trim().isEmpty() && name.matches("^[a-zA-Z0-9_]+$");
	}

}
