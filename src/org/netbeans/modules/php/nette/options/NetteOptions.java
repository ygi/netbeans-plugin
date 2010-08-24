
package org.netbeans.modules.php.nette.options;

import org.netbeans.modules.php.api.util.UiUtils;
import org.openide.util.NbPreferences;

/**
 *
 * @author Ondřej Brejla
 */
public class NetteOptions {

	private static NetteOptions INSTANCE;

	private static final String NETTE_OPTIONS_PATH = "Nette";

	private static final String NETTE_PATH = "nette-path";

	public static NetteOptions getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new NetteOptions();
		}

		return INSTANCE;
	}

	private NetteOptions() {}

	public void setNettePath(String nettePath) {
		NbPreferences.forModule(NetteOptions.class).put(NETTE_PATH, nettePath);
	}

	public String getNettePath() {
		return NbPreferences.forModule(NetteOptions.class).get(NETTE_PATH, "");
	}

	public String getOptionsPath() {
		return UiUtils.OPTIONS_PATH + "/" + NETTE_OPTIONS_PATH;
	}

}
