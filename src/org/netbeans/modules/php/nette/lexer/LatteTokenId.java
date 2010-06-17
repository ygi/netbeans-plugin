
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
 *
 * @author redhead
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
            return "text/latte";
        }
        
    }.language();

    public static final Language<LatteTokenId> language() {
        return language;
    }

}
