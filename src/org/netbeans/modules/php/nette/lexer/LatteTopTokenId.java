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
 *
 * @author redhead
 */
public enum LatteTopTokenId implements TokenId {

    LATTE(null, "latte"),
    LATTE_OPEN(null, "open"),
    LATTE_CLOSE(null, "close"),
    LATTE_ATTR(null, "n_attr"),
    LATTE_TAG(null, "n_tag"),
    HTML(null, "lattetop");
    //LD(null, "delimiter"),
    //RD(null, "delimiter");

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

    private enum State {
        OUTER,
        AFTER_LD,
        IN_LATTE
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
                return LanguageEmbedding.create(LatteTokenId.language(), 0, 0, false);
            } else if (id == LatteTopTokenId.LATTE_ATTR) {
                return LanguageEmbedding.create(LatteTokenId.language(), 0, 0, false);
            /*} else if(id == LatteTopTokenId.LATTE_OPEN) {
                return LanguageEmbedding.create(HTMLTokenId.language(), 0, 0, true);
            } else if(id == LatteTopTokenId.LATTE_CLOSE) {
                return LanguageEmbedding.create(HTMLTokenId.language(), 0, 0, true);*/
            } else if(id == LatteTopTokenId.HTML) {
                return LanguageEmbedding.create(HTMLTokenId.language(), 0, 0, true);
            }
            return null;
        }


        @Override
        protected String mimeType() {
            return "text/latte-template";
        }
    }.language();


    /**
     * Gets language denoted by tokens
     * @return Language<LatteTopTokenId>
     */
    public static final Language<LatteTopTokenId> language() {
        return language;
    }
}
