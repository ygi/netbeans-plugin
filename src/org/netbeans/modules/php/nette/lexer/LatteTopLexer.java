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
        State state = info.state() == null ? State.INIT : ((LexerState)info.state()).getState();
        State substate = info.state() == null ? State.INIT : ((LexerState)info.state()).getState();
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        this.scanner = new LatteTopColoringLexer(info, state, substate);
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
            if(property != null && tokenId != LatteTopTokenId.HTML) {
                token = tokenFactory.createPropertyToken(tokenId, input.readLength(),
                        new LattePropertyProvider(property));
                property = null;
            } else {
                token = tokenFactory.createToken(tokenId);
            }
        }
        return token;
    }

    public Object state() {
        return scanner.getState();
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
        IN_LATTE_TAG,
        IN_HTML_TAG,
        IN_HTML_ATTR,
        IN_LATTE_ATTR,
        IN_LATTE_ATTR_CLOSE
    }

    private class LatteTopColoringLexer {

        private State state;
        private State substate;
        private final LexerInput input;

        public LatteTopColoringLexer(LexerRestartInfo<LatteTopTokenId> info, State state, State substate) {
            this.input = info.input();
            this.state = state;
            this.substate = substate;
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
                    substate = state;
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
                                        state = substate;
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
                            state = substate;
                            return LatteTopTokenId.LATTE;
                        }
                        if(c == EOF) {
                            state = substate;
                            return LatteTopTokenId.HTML;
                        }
                        c = input.read();
                    }
                }
                //parsing <tag n:attr>
                if((state == State.OUTER || state == State.INIT) && c == '<') {
                    if(input.readLength() > 1) {
                        input.backup(1);
                        break;
                    }
                    c = input.read();
                    int i = 0;
                    while(true) {
                        if(!Character.isLetter(c) || c == EOF) {
                            if((c != '/' || i != 0) && c != ':' && c != '-') {
                                input.backup(1);
                                if(input.readLength() == 1) {
                                    state = State.OUTER;
                                    return LatteTopTokenId.HTML;
                                }
                                if(input.readText().toString().startsWith(("<n:"))) {
                                    state = State.IN_LATTE_TAG;
                                    property = input.readText().toString().substring(3);
                                    return LatteTopTokenId.LATTE_TAG;
                                } else {
                                    state = State.IN_HTML_TAG;
                                    return LatteTopTokenId.HTML_TAG;
                                }
                            }
                        }
                        c = input.read();
                        i++;
                    }
                }
                if(state == State.IN_HTML_TAG || state == State.IN_LATTE_TAG) {
                    if(c == '/') {
                        if(input.readLength() > 1) {
                            input.backup(1);
                            break;
                        }
                        c = input.read();
                        if(c == '>') {
                            state = State.OUTER;
                            if(state == State.IN_LATTE_TAG)
                                return LatteTopTokenId.LATTE_TAG;
                            else
                                return LatteTopTokenId.HTML_TAG;
                        }
                        input.backup(1);
                        break;
                    }
                    if(c == '>') {
                        if(input.readLength() > 1) {
                            input.backup(1);
                            break;
                        }
                        if(state == State.IN_LATTE_TAG) {
                            state = State.OUTER;
                            return LatteTopTokenId.LATTE_TAG;   // intetional TAG
                        } else {
                            state = State.OUTER;
                            return LatteTopTokenId.HTML;
                        }
                    }
                    if(c == '<') {
                        if(input.readLength() > 1) {
                            input.backup(1);
                            if(state == State.IN_LATTE_TAG) {
                                state = State.OUTER;
                                return LatteTopTokenId.LATTE_TAG;   // intetional TAG
                            } else {
                                state = State.OUTER;
                                return LatteTopTokenId.HTML;
                            }
                        }
                    }
                    if(c == '"') {
                        substate = state;
                        if(state == State.IN_LATTE_TAG)
                            state = State.IN_LATTE_ATTR;
                        else
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
                                else if(c == '-')       // for tag- and inner- prefixes
                                    property = "";
                                if(c == '"') {
                                    substate = state;
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
                        state = substate;
                        substate = null;
                        break;
                    } else {
                        input.backup(1);
                        state = State.IN_LATTE_ATTR_CLOSE;
                        return LatteTopTokenId.LATTE;
                    }
                }
                if(state == State.IN_LATTE_ATTR_CLOSE && c == '"') {
                    state = substate;
                    substate = null;
                    return LatteTopTokenId.HTML;
                }
                if(state == State.IN_HTML_ATTR && c == '"') {
                    substate = null;
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
            return new LexerState(state, substate);
        }
    }

    class LexerState {

        State state;
        State substate;

        public LexerState(State state, State substate) {
            this.state = state;
            this.substate = substate;
        }

        public State getState() {
            return state;
        }

        public State getSubstate() {
            return substate;
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
