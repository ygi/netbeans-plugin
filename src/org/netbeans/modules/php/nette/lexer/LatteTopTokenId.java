/*
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
    LATTE_ATTR(null, "n_attr"),		// n:attr
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
            if (id == LatteTopTokenId.LATTE || id == LatteTopTokenId.LATTE_ATTR) {
                return LanguageEmbedding.create(LatteTokenId.language(), 0, 0, false);	// if anything latte, process as latte
            } else {
                return LanguageEmbedding.create(HTMLTokenId.language(), 0, 0, true);	// anything else is HTML
            }
        }


        @Override
        protected String mimeType() {
            return "text/latte-template";
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
