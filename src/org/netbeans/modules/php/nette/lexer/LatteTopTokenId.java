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
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 * Enum of tokens for top language of latte template and top language specification
 * @author Radek Ježdík
 */
public enum LatteTopTokenId implements TokenId {

    LATTE(null, "latte"),			// macros
    LATTE_TAG(null, "n_tag"),		// <n:tag
    HTML(null, "lattetop"),			// anything which is not latte
    HTML_TAG(null, "lattetop");		// starndard html <tag

    private final String fixedText;
    private final String primaryCategory;

    private LatteTopTokenId(String fixedText, String primaryCategory) {
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


    private static final Language<LatteTopTokenId> language = new LanguageHierarchy<LatteTopTokenId>() {

        @Override
        protected Collection<LatteTopTokenId> createTokenIds() {
            return EnumSet.allOf(LatteTopTokenId.class);
        }

        @Override
        protected Map<String, Collection<LatteTopTokenId>> createTokenCategories() {
            return new HashMap<String, Collection<LatteTopTokenId>>();
        }

        @Override
        protected Lexer<LatteTopTokenId> createLexer(LexerRestartInfo<LatteTopTokenId> info) {
            return new LatteTopLexer(info);
        }

        @Override
        protected LanguageEmbedding<?> embedding(Token<LatteTopTokenId> token,
                LanguagePath lp, InputAttributes ia)
        {
            LatteTopTokenId id = token.id();
            if (id == LatteTopTokenId.LATTE) {
                return LanguageEmbedding.create(LatteTokenId.language(), 0, 0, false);	// if anything latte, process as latte
            } else {
                return LanguageEmbedding.create(HTMLTokenId.language(), 0, 0, true);	// anything else is HTML
            }
        }


        @Override
        protected String mimeType() {
            return "text/x-latte-template";
        }
    }.language();


    /**
     * Gets language used for these tokens
     * @return language
     */
    public static Language<LatteTopTokenId> language() {
        return language;
    }
}
