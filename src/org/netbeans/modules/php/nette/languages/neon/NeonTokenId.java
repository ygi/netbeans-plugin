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

package org.netbeans.modules.php.nette.languages.neon;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public enum NeonTokenId implements TokenId {

	T_QUOTATION_MARK("\"", "string"),
	T_APOSTROPHE("'", "string"),
	T_LEFT_CURLY("{", "interpunction"),
	T_RIGHT_CURLY("}", "interpunction"),
	T_LEFT_SQUARED("[", "interpunction"),
	T_RIGHT_SQUARED("]", "interpunction"),
	T_LT("<", "interpunction"),
	T_EQUALS("=", "interpunction"),
	T_ASSIGN("=>", "interpunction"),
	T_COLON(":", "interpunction"),
	T_DASH("-", "interpunction"),
	T_COMMA(",", "interpunction"),
	T_NUMBER(null, "number"),
	T_STRING(null, "string"),
	T_LITERAL(null, "literal"),
	T_COMMENT(null, "comment"),
	T_KEYWORD(null, "keyword"),
	T_VARIABLE(null, "variable"),
	T_BLOCK(null, "block"),
	T_KEY(null, "key"),
	T_WHITESPACE(null, "whitespace"),
	T_INDENTATION(null, "whitespace"),
	T_NEW_LINE(null, "whitespace"),
	T_ERROR(null, "error");

	private String primaryCategory;

	private String text;

	private static final Language<NeonTokenId> language = new NeonLanguageHierarchy().language();

	private NeonTokenId(String text, String primaryCategory) {
		this.text = text;
		this.primaryCategory = primaryCategory;
	}

	@Override
	public String primaryCategory() {
		return primaryCategory;
	}

	public String getText() {
		return text;
	}

    public static Language<NeonTokenId> getLanguage() {
        return language;
    }

}
