
package org.netbeans.modules.php.nette.lexer;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;
import javax.swing.text.Document;

/**
 * Lexer for inside-macro tokenizing (must be parsed out by LatteTopLexer as LatteTopTokenId.LATTE)
 * @author redhead
 */
class LatteLexer implements Lexer<LatteTokenId> {

    private static final int EOF = LexerInput.EOF;

    private LexerInput input;

    private TokenFactory<LatteTokenId> tokenFactory;

    private Document document;

    State state;

    LatteLexer(LexerRestartInfo<LatteTokenId> info) {
        state = info.state() == null ? State.INIT : (State) info.state();
        /*String macro = null;
        if(info.inputAttributes() != null)
            macro = (String) info.inputAttributes().getValue(info.languagePath(), "macro");
        if(state == State.INIT && macro != null) {
            if(!macro.equals("}"))
                state = State.AFTER_MACRO;
            else {
                state = State.INIT;
                info.inputAttributes().setValue(info.languagePath(), "macro", null, false);
            }
        }*/
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
    }

    /** keywords which will be highlited differently (like in PHP) */
    private final static List<String> keywords = new ArrayList<String>();
    static {
        keywords.add("true");
        keywords.add("false");
        keywords.add("array");
        keywords.add("null");
        keywords.add("as");
        keywords.add("or");
        keywords.add("and");
        keywords.add("xor");
        keywords.add("isset");
        keywords.add("instanceof");
    };
    
    /** State of the lexer - where in tokenizing the macro the lexer ended */
    enum State {
        OUTER,
        INIT,
        AFTER_LD,
        AFTER_MACRO,
        IN_FIRST_PARAM,
        IN_VAR,
        IN_INDEX,
        IN_HELPER,
        IN_HELPER_PARAM,
        IN_PARAMS
    }

    /**
     * Tokenizes the input. In this lexer the input must be a latte macro (latte mime-type).
     * For HTML use LatteTopLexer
     * Returns creted Token with LattteTokenId
     * @return Token<LattteTokenId>
     */
    public Token<LatteTokenId> nextToken() {
        while(true) {
            int ch = input.read();
            char chr = (char)ch;
            CharSequence s = input.readText();
            if(ch == EOF)
                return null;
            switch(state) {
                case INIT:
                    if(ch == '{') {
                        state = State.AFTER_LD;
                        return token(LatteTokenId.LD);
                    } else {
                        input.backup(1);
                        state = State.AFTER_MACRO;
                        continue;
                    }
                case AFTER_LD:
                    if(ch == '*') {
                        while(true) {
                            ch = input.read();
                            if(ch == EOF) {
                                input.backup(1);
                                state = State.OUTER;
                                return token(LatteTokenId.COMMENT);
                            }
                            if(ch == '*') {
                                ch = input.read();
                                input.backup(1);
                                if(ch == '}' || ch == EOF) {
                                    state = State.AFTER_MACRO;
                                    return token(LatteTokenId.COMMENT);
                                }
                            }
                        }
                    }
                    if(ch == '!') {
                        ch = input.read();
                        if(ch == '=' || ch == '_' || ch == '$') {
                            if(ch == '$')
                                input.backup(1);
                            state = State.AFTER_MACRO;
                            return token(LatteTokenId.MACRO);
                        }
                        return token(LatteTokenId.ERROR);
                    }
                    if(ch == '?' || ch == '=' || ch == '_') {
                        state = State.AFTER_MACRO;
                        return token(LatteTokenId.MACRO);
                    }
                case AFTER_MACRO:
                    if(ch == '$') {
                        int c = input.read();
                        if(Character.isLetter(c) || c == '_') {
                            while(true) {
                                c = input.read();
                                if((!Character.isLetterOrDigit(c) && c != '_') || ch == EOF) {
                                    input.backup(1);
                                    state = State.AFTER_MACRO;
                                    return token(LatteTokenId.VARIABLE);
                                }
                            }
                        } else {
                            return token(LatteTokenId.ERROR);
                        }
                    }
                    switch(ch) {
                        case '\'':
                        case '"':
                            int q = ch;
                            boolean escape = false;
                            while(true) {
                                int c = input.read();
                                if(c == q && escape == false) {
                                    state = State.AFTER_MACRO;
                                    return token(LatteTokenId.STRING);
                                }
                                escape = false;
                                if(c == '\\')
                                    escape = true;
                                if(c == EOF) {
                                    state = State.AFTER_MACRO;
                                    return token(LatteTokenId.STRING);
                                }
                            }
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                        case '.':
                            return finishIntOrFloatLiteral(ch);

                        case '=':
                            if(input.read() == '>')
                                return token(LatteTokenId.ASSIGN);
                            input.backup(1);
                            return token(LatteTokenId.EQUALS);

                        case ':':
                            return token(LatteTokenId.COLON);

                        case '+': return token(LatteTokenId.PLUS);
                        case '-':
                            if(input.read() == '>')
                                return token(LatteTokenId.ACCESS);
                            input.backup(1);
                            return token(LatteTokenId.MINUS);
                        case '*': return token(LatteTokenId.STAR);
                        case '/': return token(LatteTokenId.SLASH);
                        case '|': return token(LatteTokenId.PIPE);
                        case ',': return token(LatteTokenId.COMA);
                        case '(': return token(LatteTokenId.LNB);
                        case ')': return token(LatteTokenId.RNB);
                        case '[': return token(LatteTokenId.LB);
                        case ']': return token(LatteTokenId.RB);
                        case '!': return token(LatteTokenId.NEGATION);
                        case '<': return token(LatteTokenId.LT);
                        case '>': return token(LatteTokenId.GT);
                        case ';': return token(LatteTokenId.SEMICOLON);
                        case '&': return token(LatteTokenId.AND);
                        case '@': return token(LatteTokenId.AT);
                        case '#': return token(LatteTokenId.HASH);
                    }
                default:
                    if(ch == '}') {
                        state = State.INIT;
                        return token(LatteTokenId.RD);
                    }
                    if(Character.isWhitespace(ch)) {
                        ch = input.read();
                        while (ch != EOF && Character.isWhitespace((char)ch)) {
                            ch = input.read();
                        }
                        input.backup(1);
                        return token(LatteTokenId.WHITESPACE);
                    }
                    if(Character.isLetter(ch) || ch == '_') {
                        while(true) {
                            ch = input.read();
                            if(!Character.isLetterOrDigit(ch) && ch != '_') {
                                input.backup(1);
                                if(state == State.AFTER_LD) {
                                    state = State.AFTER_MACRO;
                                    return token(LatteTokenId.MACRO);
                                }
                                state = State.AFTER_MACRO;
                                if(keywords.contains(input.readText().toString().toLowerCase()))
                                    return token(LatteTokenId.KEYWORD);
                                return token(LatteTokenId.TEXT);
                            }
                        }
                    }
                    return token(LatteTokenId.ERROR);
            }
        }
    }

    /**
     * Returns state in which the lexer ended in current tokenizing
     * @return State
     */
    public Object state() {
        return state;
    }

    /**
     * Finishes current token representing a number
     * FIXME: single dot is also tokenized as number (in php it is a legal operator for string concat.)
     * @param int ch character currently tokenized
     * @return
     */
    private Token<LatteTokenId> finishIntOrFloatLiteral(int ch) {
        boolean floatLiteral = false;
        while (true) {
            switch (ch) {
                case '.':
                    if (floatLiteral) {
                        return token(LatteTokenId.NUMBER);
                    } else {
                        floatLiteral = true;
                    }
                    break;
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    break;
                default:
                    input.backup(1);
                    return token(LatteTokenId.NUMBER);
            }
            ch = input.read();
        }
    }

    /**
     * Creates token from factory
     * @param id
     * @return
     */
    private Token<LatteTokenId> token(LatteTokenId id) {
        return (id.fixedText() != null)
            ? tokenFactory.getFlyweightToken(id, id.fixedText())
            : tokenFactory.createToken(id);
    }

    public void release() {

    }
}
