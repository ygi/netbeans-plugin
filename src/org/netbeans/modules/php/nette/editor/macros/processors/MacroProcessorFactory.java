
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
