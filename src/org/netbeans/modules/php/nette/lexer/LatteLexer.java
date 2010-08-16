
package org.netbeans.modules.php.nette.lexer;

import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 * Lexer for inside-macro tokenizing (must be parsed out by LatteTopLexer as LatteTopTokenId.LATTE)
 * @author redhead
 */
class LatteLexer implements Lexer<LatteTokenId> {

    private static final int EOF = LexerInput.EOF;
	
    private final LatteColoringLexer scanner;

    private LexerInput input;

    private TokenFactory<LatteTokenId> tokenFactory;

    State state;

    LatteLexer(LexerRestartInfo<LatteTokenId> info) {
        state = info.state() == null ? State.OUTER : (State) info.state();
        /*String macro = null;
        if(info.inputAttributes() != null)
            macro = (String) info.inputAttributes().getValue(info.languagePath(), "macro");
        if(state == State.OUTER && macro != null) {
            if(!macro.equals("}"))
                state = State.AFTER_MACRO;
            else {
                state = State.OUTER;
                info.inputAttributes().setValue(info.languagePath(), "macro", null, false);
            }
        }*/
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        this.scanner = new LatteColoringLexer(info, state);
    }

    /**
     * Calls tokenizer and creates Token by token factory
     * @return Token<LatteTokenId>
     */
    public Token<LatteTokenId> nextToken() {
        LatteTokenId tokenId = scanner.nextToken();

		if(tokenId == null)
			return null;

        return (tokenId.fixedText() != null)
            ? tokenFactory.getFlyweightToken(tokenId, tokenId.fixedText())
            : tokenFactory.createToken(tokenId);
    }

    /** keywords which will be highlited differently (like in PHP) */
    private final static List<String> keywords = new ArrayList<String>();
    static {
        keywords.add("true");
        keywords.add("false");
        keywords.add("array");
        keywords.add("null");
        keywords.add("new");
        keywords.add("as");
        keywords.add("or");
        keywords.add("and");
        keywords.add("xor");
        keywords.add("isset");
        keywords.add("instanceof");
    };
    
    /** State of the lexer - where in tokenizing the macro the lexer ended */
    enum State {
        OUTER,				// out of macro
        AFTER_LD,			// after left delimiter {
        AFTER_MACRO,		// after macro name {macroName
        IN_FIRST_PARAM,		// in the first parameter of macro {macroName first_param
        IN_VAR,				// after $ character
        IN_INDEX,			// between [] for array index/key
        IN_HELPER,			// after helper delimiter |
        IN_HELPER_PARAM,	// after |helper:
        IN_PARAMS			// macro parameters (except the first one)
    }

	private class LatteColoringLexer {

        private State state;
        private final LexerInput input;

        public LatteColoringLexer(LexerRestartInfo<LatteTokenId> info, State state) {
            this.input = info.input();
            this.state = state;
        }
		
		/**
		 * Tokenizes the input. In this lexer the input must be a latte macro (latte mime-type).
		 * For each character (or sequence) returns its LattteTokenId representative
		 *
		 * @return LattteTokenId token of LattteTokenId enum
		 */
		public LatteTokenId nextToken() {
			while(true) {
				int ch = input.read();								// next character from the input
				char chr = (char)ch;								// actual character (for debugging purpouse)
				CharSequence s = input.readText();					// whole text read (for debugging purpouse)
				if(ch == EOF)
					return null;									//end of file
				switch(state) {
					case OUTER:
						if(ch == '{') {								// start of the macro
							state = State.AFTER_LD;					// new state after left delimiter
							return LatteTokenId.LD;
						} else {
							input.backup(1);						// if no left delimiter it is a n:attribute value
							state = State.AFTER_MACRO;				// so start with after macro state
							continue;
						}
					case AFTER_LD:
						if(ch == '*') {								// comment
							while(true) {
								ch = input.read();
								if(ch == EOF) {						// comment macro not closed
									input.backup(1);
									state = State.OUTER;
									return LatteTokenId.COMMENT;
								}
								if(ch == '*') {						// finds closing comment delimiter *}
									ch = input.read();
									input.backup(1);				// right delim should not be tokenized as comment
									if(ch == '}' || ch == EOF) {
										state = State.AFTER_MACRO;
										return LatteTokenId.COMMENT;
									}
								}
							}
						}
						if(ch == '!') {								// no escaping
							ch = input.read();
							if(ch == '=' || ch == '_' || ch == '$') {	// macros which support ! char
								if(ch == '$')
									input.backup(1);				// except $ char (will be used for variable)
								state = State.AFTER_MACRO;
								return LatteTokenId.MACRO;
							}
							return LatteTokenId.ERROR;
						}
						if(ch == '?' || ch == '=' || ch == '_') {	// for all other macros see default: at the end
							state = State.AFTER_MACRO;
							return LatteTokenId.MACRO;
						}
					case AFTER_MACRO:
						if(ch == '$') {								// possible variable
							int c = input.read();
							if(Character.isLetter(c) || c == '_') {	// deals with valid names of php var
								while(true) {
									c = input.read();
									if((!Character.isLetterOrDigit(c) && c != '_') || ch == EOF) {
										input.backup(1);			// found char which is not valid char for php var
										state = State.AFTER_MACRO;
										return LatteTokenId.VARIABLE;
									}
								}
							} else {
								return LatteTokenId.ERROR;	// else variable error
							}
						}
						switch(ch) {
							case '\'':								// string literal
							case '"':
								int q = ch;							// saves type of a quote (double x single)
								boolean escape = false;				// is quote char escaped?
								while(true) {
									int c = input.read();
									if(c == q && escape == false) {	// if char is the closing quote and is not escaped
										state = State.AFTER_MACRO;
										return LatteTokenId.STRING;
									}
									escape = false;
									if(c == '\\')					// next char is escaped
										escape = true;
									if(c == EOF) {
										state = State.AFTER_MACRO;
										return LatteTokenId.STRING;
									}
								}
							// number literal
							case '0': case '1': case '2': case '3': case '4':
							case '5': case '6': case '7': case '8': case '9':
							case '.':
								return finishIntOrFloatLiteral(ch);

							// equals sign or php array assign =>
							case '=':
								if(input.read() == '>')
									return LatteTokenId.ASSIGN;
								input.backup(1);
								return LatteTokenId.EQUALS;

							case ':':
								return LatteTokenId.COLON;

							case '+': return LatteTokenId.PLUS;

							// minus sign or object access (accessing field or method..)
							case '-':
								if(input.read() == '>')
									return LatteTokenId.ACCESS;
								input.backup(1);
								return LatteTokenId.MINUS;

							// all other characters
							case '*': return LatteTokenId.STAR;
							case '/': return LatteTokenId.SLASH;
							case '|': return LatteTokenId.PIPE;
							case ',': return LatteTokenId.COMA;
							case '(': return LatteTokenId.LNB;
							case ')': return LatteTokenId.RNB;
							case '[': return LatteTokenId.LB;
							case ']': return LatteTokenId.RB;
							case '!': return LatteTokenId.NEGATION;
							case '<': return LatteTokenId.LT;
							case '>': return LatteTokenId.GT;
							case ';': return LatteTokenId.SEMICOLON;
							case '&': return LatteTokenId.AND;
							case '@': return LatteTokenId.AT;
							case '#': return LatteTokenId.HASH;
							case '?': return LatteTokenId.QUESTION;
						}
					// if nothing of above did not matched
					default:
						if(ch == '}') {								// closing delimiter
							state = State.OUTER;
							return LatteTokenId.RD;
						}
						if(Character.isWhitespace(ch)) {			// whitespace
							ch = input.read();
							while (ch != EOF && Character.isWhitespace((char)ch)) {
								ch = input.read();
							}
							input.backup(1);
							return LatteTokenId.WHITESPACE;
						}
						if(Character.isLetter(ch) || ch == '_') {	// any text
							while(true) {
								ch = input.read();
								if(!Character.isLetterOrDigit(ch) && ch != '_') {
									input.backup(1);
									if(state == State.AFTER_LD) {	// after left delim, so it is a macro name!
										state = State.AFTER_MACRO;
										return LatteTokenId.MACRO;
									}
									state = State.AFTER_MACRO;		// all else is after macro

									// if read text is one of php keywords, return KEYWORD token
									if(keywords.contains(input.readText().toString().toLowerCase()))
										return LatteTokenId.KEYWORD;

									// all else is just text (action name in plink, template name in include, ...)
									return LatteTokenId.TEXT;
								}
							}
						}
						// anything else that didn't mach is a syntax error!!
						return LatteTokenId.ERROR;
				}
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
     * FIXME: single dot is also tokenized as number (in php it is a legal operator for string concatenation)
     * @param int ch character currently tokenized
     * @return LatteTokenId
     */
    private LatteTokenId finishIntOrFloatLiteral(int ch) {
        boolean floatLiteral = false;
        while (true) {
            switch (ch) {
                case '.':
                    if (floatLiteral) {
                        return LatteTokenId.NUMBER;
                    } else {
                        floatLiteral = true;
                    }
                    break;
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    break;
                default:
                    input.backup(1);
                    return LatteTokenId.NUMBER;
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
		// intentionally
    }
}
