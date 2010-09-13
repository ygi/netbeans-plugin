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
