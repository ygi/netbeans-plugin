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

	T_INTERPUNCTION(null, "interpunction"),
	T_NUMBER(null, "number"),
	T_STRING(null, "string"),
	T_LITERAL(null, "literal"),
	T_OBJECT(null, "object"),
	T_COMMENT(null, "comment"),
	T_KEYWORD(null, "keyword"),
	T_VARIABLE(null, "variable"),
	T_BLOCK(null, "block"),
	T_KEY(null, "key"),

	T_VALUE(null, "value"),
	T_ERROR(null, "error"),
	T_NAME(null, "name"),
	T_INNER_NAME(null, "innerName"),
	T_BRACKET(null, "bracket"),
	T_WHITESPACE(null, "whitespace")/*,
	T_FALSE_LARGE("FALSE", "keyword"),
	T_FALSE_SMALL("false", "keyword"),
	T_TRUE_LARGE("TRUE", "keyword"),
	T_TRUE_SMALL("true", "keyword"),
	T_YES_LARGE("YES", "keyword"),
	T_YES_SMALL("yes", "keyword"),
	T_NO_LARGE("NO", "keyword"),
	T_NO_SMALL("no", "keyword"),
	T_NULL_LARGE("NULL", "keyword"),
	T_NULL_SMALL("null", "keyword")*/;

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
