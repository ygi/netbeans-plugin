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

package org.netbeans.modules.php.nette.editor.hints;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @author Radek Ježdík
 */
public class HintsSettings {

	public static HashMap<String, Boolean> getSettings() {
		Preferences p = getPreferences();
		HintFactory[] values = HintFactory.values();

		HashMap<String, Boolean> settings = new HashMap<String, Boolean>();
		for(HintFactory hf : values) {
			settings.put(hf.getName(), p.getBoolean(hf.getName(), true));
		}

		return settings;
	}

	public static void saveSettings(HashMap<String, Boolean> settings) {
		Preferences p = getPreferences();

		for(Map.Entry<String, Boolean> entry : settings.entrySet()) {
			p.putBoolean(entry.getKey(), entry.getValue());
		}
	}

	public static boolean isVisible(HintFactory hint) {
		return getPreferences().getBoolean(hint.getName(), true);
	}

	private static Preferences getPreferences() {
		return NbPreferences.forModule(HintsSettings.class);
	}

}
