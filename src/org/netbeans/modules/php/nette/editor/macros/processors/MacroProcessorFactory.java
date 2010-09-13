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

package org.netbeans.modules.php.nette.editor.macros.processors;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ondřej Brejla
 */
abstract public class MacroProcessorFactory {

	private static final List<String> arrayMacros = new ArrayList<String>();
	static {
		arrayMacros.add("var");
		arrayMacros.add("default");
		arrayMacros.add("assign");
	};

	private static final List<String> specialMacros = new ArrayList<String>();
	static {
		specialMacros.add("plink");
		specialMacros.add("link");
		specialMacros.add("widget");
		specialMacros.add("control");
		specialMacros.add("include");
		specialMacros.add("extends");
		specialMacros.add("cache");
	};

	private static final List<String> blockMacros = new ArrayList<String>();
	static {
		blockMacros.add("foreach");
		blockMacros.add("for");
		blockMacros.add("while");
		blockMacros.add("if");
		blockMacros.add("ifset");
		blockMacros.add("ifcurrent");
		blockMacros.add("block");
		blockMacros.add("snippet");
		blockMacros.add("capture");
	};

	private static final List<String> signMacros = new ArrayList<String>();
	static {
		signMacros.add("=");
		signMacros.add("_");
		signMacros.add("!");
		signMacros.add("!=");
		signMacros.add("!_");
		signMacros.add("?");
	};

	public static MacroProcessor getMacroProcessor(String macro) {
		if (arrayMacros.contains(macro)) {
			// {var var => ""} ->  $var = "";
			return new ArrayMacroProcessor();
		} else if (specialMacros.contains(macro)) {
			// {link default var => $val} -> "default"; array(var => $val);
			return new SpecialMacroProcessor();
		} else if (signMacros.contains(macro)) {
			// _, =, !=, ...
			return new SignMacroProcessor();
		} else if (blockMacros.contains(macro)) {
			// {if} {/if} -> if() { }
			return new BlockMacroProcessor();
		} else if (macro.equals("attr")) {
			// {attr class() something()} -> $v->class()->something()
			return new AttrMacroProcessor();
		} else {
			return new CommonMacroProcessor();
		}
	}

	private MacroProcessorFactory() {}

}
