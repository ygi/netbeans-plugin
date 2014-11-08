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

package org.netbeans.modules.php.nette.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * Enum of tokens for insed-macro language of latte template and inside-macro language specification
 * @author Radek Ježdík
 */
public enum LatteTokenId implements TokenId {

    WHITESPACE(null, "whitespace"),
    LD("{", "delimiter"),
    RD("}", "delimiter"),
    COLON(":", "colon"),
    SEMICOLON(";", "text"),
    ASSIGN("=>", "assign"),
    ACCESS("->", "operator"),
    PLUS("+", "operator"),
    MINUS("-", "operator"),
    STAR("*", "operator"),
    SLASH("/", "operator"),
    PIPE("|", "operator"),
    COMA(",", "operator"),
    LNB("(", "operator"),
    RNB(")", "operator"),
    LB("[", "operator"),
    RB("]", "operator"),
    NEGATION("!", "operator"),
    EQUALS("=", "operator"),
    LT("<", "operator"),
    GT(">", "operator"),
    AND("&", "text"),
    HASH("#", "text"),
    AT("@", "text"),
	QUESTION("?", "text"),
    MACRO(null, "macro"),
    KEYWORD(null, "keyword"),
    TEXT(null, "text"),
    STRING(null, "string"),
    VARIABLE(null, "variable"),
    NUMBER(null, "number"),
    COMMENT(null, "comment"),
    ERROR(null, "error");

    private final String fixedText;

    private final String primaryCategory;

    private LatteTokenId(String fixedText, String primaryCategory) {
        this.fixedText = fixedText;
        this.primaryCategory = primaryCategory;
    }

    public String fixedText() {
        return fixedText;
    }

	@Override
    public String primaryCategory() {
        return primaryCategory;
    }

    private static final Language<LatteTokenId> language = new LanguageHierarchy<LatteTokenId>() {

        @Override
        protected Collection<LatteTokenId> createTokenIds() {
            return EnumSet.allOf(LatteTokenId.class);
        }

        @Override
        protected Map<String, Collection<LatteTokenId>> createTokenCategories() {
            Map<String,Collection<LatteTokenId>> cats = new HashMap<String,Collection<LatteTokenId>>();
            return cats;
        }

        @Override
        protected Lexer<LatteTokenId> createLexer(LexerRestartInfo<LatteTokenId> info) {
            return new LatteLexer(info);
        }

        @Override
        protected String mimeType() {
            return "text/x-latte";
        }

    }.language();

    public static Language<LatteTokenId> language() {
        return language;
    }

}
