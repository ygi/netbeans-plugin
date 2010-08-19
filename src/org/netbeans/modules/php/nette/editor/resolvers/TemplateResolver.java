
package org.netbeans.modules.php.nette.editor.resolvers;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.nette.editor.Embedder;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;

/**
 *
 * @author Ondřej Brejla
 */
abstract public class TemplateResolver {

	protected Embedder embedder;

	// stores sequence of tags and number of code blocks which were defined by n:attributes for that tag
	private static List<Integer> tags = new ArrayList<Integer>();

	private static String macroName = null;

	private static int numberOfBlocks = 0;

	public static String getMacroName() {
		return macroName;
	}

	public static void setMacroName(String newMacroName) {
		macroName = newMacroName;
	}

	public static List<Integer> getTags() {
		return tags;
	}

	public static void incNumberOfBlocks() {
		numberOfBlocks++;
	}

	public static void decNumberOfBlocks() {
		numberOfBlocks--;
	}

	public static int getNumberOfBlocks() {
		return numberOfBlocks;
	}

	public static void init() {
		tags = new ArrayList<Integer>();
		macroName = null;
		numberOfBlocks = 0;
	}

	public TemplateResolver(Embedder embedder) {
		this.embedder = embedder;
	}

	abstract public void solve(Token t, TokenSequence<LatteTopTokenId> sequence);

}
