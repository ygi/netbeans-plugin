/*
 * The MIT license
 *
 * Copyright (c) 2010 Radek Ježdík <redhead@email.cz>, Ondřej Brejla <ondrej@brejla.cz>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

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
