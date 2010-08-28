/*
 */

package org.netbeans.modules.php.nette.macros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Radek Ježdík
 */
public class MacroDefinitions {

	public static LatteMacro[] macros = {
        new LatteCommentMacro(),
        new LatteSingleMacro("$"),
        new LatteSingleMacro("!$"),
        new LatteSingleMacro("="),
        new LatteSingleMacro("!="),
        new LatteSingleMacro("?"),
        new LatteSingleMacro("_"),
        new LatteSingleMacro("!_"),
        new LatteParamMacro("link", false),
        new LatteParamMacro("plink", false),
        new LatteParamMacro("if", true),
        new LatteParamMacro("ifset", true, "if"),
        new LatteParamMacro("ifcurrent", true, "if"),
        new LatteParamMacro("for", true),
        new LatteParamMacro("foreach", true),
        new LatteParamMacro("while", true),
        new LatteParamMacro("include", false),
        new LatteParamMacro("extends", false),
        new LatteParamMacro("layout", false),
        new LatteParamMacro("widget", false),
        new LatteParamMacro("control", false),
        new LatteParamMacro("cache", true),
        new LatteParamMacro("snippet", true),
        new LatteParamMacro("attr", false),
        new LatteParamMacro("block", true),
        new LatteParamMacro("contentType", false),
        new LatteParamMacro("status", false),
        new LatteParamMacro("capture", false),
        new LatteParamMacro("assign", false),
        new LatteParamMacro("default", false),
        new LatteParamMacro("var", false),
        new LatteParamMacro("dump", false),
        new LatteMacro("debugbreak", false),
        new LatteMacro("l", false),
        new LatteMacro("r", false),
    };

	/**
	 * Defines friend macros of some block macros
	 */
    public final static HashMap<String, LatteMacro[]> friendMacros = new HashMap<String, LatteMacro[]>();
    static {
        friendMacros.put("if", new LatteMacro[] {
            new LatteMacro("else"),
            new LatteParamMacro("elseif", false)
        });
        friendMacros.put("ifset", new LatteMacro[] {
            new LatteMacro("else"),
            new LatteParamMacro("elseif", false),
            new LatteParamMacro("elseifset", false)
        });
        friendMacros.put("ifcurrent", new LatteMacro[] {
            new LatteMacro("else"),
            new LatteParamMacro("elseif", false)
        });
    };

	/**
	 * Defines macros, which can be used as <n:tag
	 */
    public final static HashMap<String, String[]> tagMacros = new HashMap<String, String[]>();
    static {
        tagMacros.put("assign", new String[] { "" });
        tagMacros.put("include", new String[] { "block" });
        tagMacros.put("for", new String[] { "each" });
        tagMacros.put("block", new String[] { "name" });
        tagMacros.put("if", new String[] { "cond" });
        tagMacros.put("elseif", new String[] { "cond" });
    };

	/*
	 * List of helpers (TODO: dynamically)
	 */
    public static String[] helpers = {
        "escapeHtml", "escapeHtmlComment", "escapeXML", "escapeCss", "escapeHtmlCss", "escapeJs",
        "escapeHtmlJs", "strip", "indent", "indentCb", "date", "bytes", "length",
        "replace", "nl2br", "stripTags", "translate", "number",
    };


	/**
	 * Searches for macro object with specified name
	 * @param name
	 * @return
	 */
    public static LatteMacro getMacro(String name) {
		name = name.toLowerCase();
        for(LatteMacro m : macros) {
            if(m.getMacroName().equals(name)) {
                return m;
            }
        }
        return null;
    }

	/**
	 * Searches for all Macro objects with specifiend end name
	 * @param name
	 * @return
	 */
    public static List<LatteMacro> getMacrosByEnd(String name) {
		name = name.toLowerCase();
        List<LatteMacro> list = new ArrayList<LatteMacro>();
        for(LatteMacro m : macros) {
            if(m.isPair && m.getEndMacroName().equals(name)) {
                list.add(m);
            }
        }
        return list;
    }

}
