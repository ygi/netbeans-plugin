/*
 */

package org.netbeans.modules.php.nette.lexer;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;
import org.netbeans.spi.lexer.TokenPropertyProvider;

/**
 * Top Lexer for text/latte-template mime-type
 * @author redhead
 */
class LatteTopLexer implements Lexer<LatteTopTokenId> {
    
    private static final int EOF = LexerInput.EOF;
    private static final Map<String, LatteTopTokenId> keywords = new HashMap<String, LatteTopTokenId>();
    private final LatteTopColoringLexer scanner;

    private LexerInput input;

    private TokenFactory<LatteTopTokenId> tokenFactory;

    LatteTopLexer(LexerRestartInfo<LatteTopTokenId> info) {
        State state = info.state() == null ? State.INIT : (State)info.state();
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        this.scanner = new LatteTopColoringLexer(info, state);
    }

    public static synchronized LatteTopLexer create(LexerRestartInfo<LatteTopTokenId> info) {
        return new LatteTopLexer(info);
    }

    private String property = null;

    /**
     * Tokenizes passed input. Returns Token created with LatteTopTokenId
     * @return Token<LatteTopTokenId>
     */
    public Token<LatteTopTokenId> nextToken() {
        LatteTopTokenId tokenId = scanner.nextToken();
        Token<LatteTopTokenId> token = null;
        if (tokenId != null) {
            if(property == null || tokenId != LatteTopTokenId.LATTE_ATTR)
                token = tokenFactory.createToken(tokenId);
            else
                token = tokenFactory.createPropertyToken(tokenId, input.readLength(),
                        new LattePropertyProvider(property));
        }
        return token;
    }

    public Object state() {
        return null;
    }

    public void release() {

    }

    /**
     * State of the lexer - where in tokenizing the lexer ended
     */
    private enum State {
        INIT,
        OUTER,
        AFTER_LD,
        IN_LATTE,
        IN_HTML_TAG,
        IN_HTML_ATTR,
        IN_LATTE_ATTR,
        IN_LATTE_ATTR_CLOSE
    }

    private class LatteTopColoringLexer {

        private State state;
        private final LexerInput input;

        public LatteTopColoringLexer(LexerRestartInfo<LatteTopTokenId> info, State state) {
            this.input = info.input();
            this.state = state;
        }

        /**
         * Top lexer tokenizer for latte-template
         * Parses out macros as LatteTopTokenId.LATTE and other as LatteTopTokenId.HTML
         * (+ started concept for n:attributes)
         * @return LatteTopTokenId
         */
        public LatteTopTokenId nextToken() {
            int c = input.read();
            CharSequence text;
            int textLength;
            if (c == EOF) {
                return null;
            }
            if(state != State.IN_LATTE_ATTR)
                property = null;
            while (c != EOF) {
                char cc = (char) c;
                text = input.readText();
                textLength = text.length();

                if (cc == '{') {
                    if(textLength > 1) {
                        input.backup(1);
                        return LatteTopTokenId.HTML;
                    }
                        
                    c = input.read();
                    //input.backup(1);
                    if(!Character.isJavaIdentifierPart(c) && c != '!' && c != '?'
                            && c != '=' && c != '/' && c != '*') {
                        return LatteTopTokenId.HTML;
                    }
                    state = State.AFTER_LD;
                    while(true) {
                        cc = (char)c;
                        if(c == '*') {                                  //if comment starts
                            c = input.read();
                            cc = (char)c;
                            while(true) {
                                if(c == '*')
                                {
                                    c = input.read();
                                    cc = (char)c;
                                    if(c == '}' || c == EOF) {          //if closing comment found or EOF
                                        state = State.OUTER;
                                        return LatteTopTokenId.LATTE;
                                    }
                                    input.backup(1);
                                }
                                if(c == EOF) {
                                    return LatteTopTokenId.HTML;
                                }
                                c = input.read();
                                cc = (char)c;
                            }
                        }
                        if(c == '{') {
                            input.backup(1);
                            return LatteTopTokenId.HTML;
                        }
                        if(c == '}') {
                            state = State.OUTER;
                            return LatteTopTokenId.LATTE;
                        }
                        if(c == EOF) {
                            state = State.OUTER;
                            return LatteTopTokenId.HTML;
                        }
                        c = input.read();
                    }
                }
                //parsing <tag n:attr>
                if((state == State.OUTER || state == State.INIT) && c == '<') {
                    c = input.read();
                    if(Character.isLetter(c)) {
                        int c2 = input.read();
                        if(c != 'n' && c2 != ':') {
                            state = State.IN_HTML_TAG;
                            break;
                        }
                    }
                }
                if(state == State.IN_HTML_TAG) {
                    if(c == '>') {
                        state = State.OUTER;
                        break;
                    }
                    if(c == '"') {
                        state = State.IN_HTML_ATTR;
                        break;
                    }
                    if(c == ' ') {
                        return LatteTopTokenId.HTML;
                    }
                    if(c == 'n') {
                        c = input.read();
                        if(c == ':') {
                            property = "";
                            while(c != EOF) {
                                if(Character.isLetter(c))
                                    property += (char)c;
                                if(c == '"') {
                                    state = State.IN_LATTE_ATTR;
                                    return LatteTopTokenId.HTML;
                                }
                                c = input.read();
                            }
                            property = null;
                        }
                    }
                }
                if(state == State.IN_LATTE_ATTR && c == '"') {
                    if(input.readLength() == 1) {
                        state = State.IN_HTML_TAG;
                        break;
                    } else {
                        input.backup(1);
                        state = State.IN_LATTE_ATTR_CLOSE;
                        return LatteTopTokenId.LATTE;
                    }
                }
                if(state == State.IN_LATTE_ATTR_CLOSE && c == '"') {
                    state = State.IN_HTML_TAG;
                    return LatteTopTokenId.HTML;
                }
                if(state == State.IN_HTML_ATTR && c == '"') {
                    state = State.IN_HTML_TAG;
                }
                
                c = input.read();
            }

            switch (state) {
                case IN_LATTE:
                    return LatteTopTokenId.LATTE;
                default:
                    return LatteTopTokenId.HTML;
            }
        }

        Object getState() {
            return state;
        }
    }

    class LattePropertyProvider<LatteTopTokenId> implements TokenPropertyProvider {

        private final String property;

        public LattePropertyProvider(String prop) {
            property = prop;
        }

        public Object getValue(Token token, Object key) {
            if("macro".equals(key))
                return property;
            return null;
        }

    }
}
