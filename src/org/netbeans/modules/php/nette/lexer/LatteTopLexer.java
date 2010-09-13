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

import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;
import org.netbeans.spi.lexer.TokenPropertyProvider;

/**
 * Top Lexer for text/latte-template mime-type
 * @author Radek Ježdík
 */
class LatteTopLexer implements Lexer<LatteTopTokenId> {
    
    private static final int EOF = LexerInput.EOF;
	
    private final LatteTopColoringLexer scanner;

    private LexerInput input;

    private TokenFactory<LatteTopTokenId> tokenFactory;

	/** stores macro name for n:attr (it is passed in the token as token property) */
    private String property = null;

    LatteTopLexer(LexerRestartInfo<LatteTopTokenId> info) {
        State state = info.state() == null ? State.OUTER : ((LexerState)info.state()).getState();
        State substate = info.state() == null ? State.OUTER : ((LexerState)info.state()).getSubstate();
        this.input = info.input();
        this.tokenFactory = info.tokenFactory();
        this.scanner = new LatteTopColoringLexer(info, state, substate);
    }

    /*public static synchronized LatteTopLexer create(LexerRestartInfo<LatteTopTokenId> info) {
        return new LatteTopLexer(info);
    }*/

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
        OUTER,					// outer html code
        AFTER_LD,				// after left delimiter
        IN_LATTE,				// in latte general
        IN_LATTE_TAG,			// in <n:tag
        IN_HTML_TAG,			// in <tag
        IN_HTML_ATTR,			// in attribute <tag attr=""
        IN_LATTE_ATTR,			// in attribute <tag n:attr=""
        IN_LATTE_ATTR_CLOSE		// in n:attr closing quote
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
            int c = input.read();									// next character from the input
            CharSequence text;										// whole text read
            int textLength;
            if (c == EOF) {
                return null;										// end of file
            }
            if(state != State.IN_LATTE_ATTR)						// if not in n:attr remove property
                property = null;
            while (c != EOF) {
                char cc = (char) c;
                text = input.readText();							// whole text read
                textLength = text.length();							// length of the whole text

                if (cc == '{') {									// possible start of a macro
                    substate = state;								// store a top state (to return to after)
                    if(textLength > 1) {							// if it is part of a longer text
                        input.backup(1);							// exlude left delimiter
                        return LatteTopTokenId.HTML;				// end return the rest as HTMl
                    }
                        
                    c = input.read();								// next character
                    if(!Character.isJavaIdentifierPart(c) && c != '!' && c != '?'
                            && c != '=' && c != '/' && c != '*') {	// it is not a macro
                        return LatteTopTokenId.HTML;
                    }
					if(c == '*') {                                  // if comment starts
						c = input.read();
						cc = (char)c;
						while(true) {
							if(c == '*')
							{
								c = input.read();
								cc = (char)c;
								if(c == '}' || c == EOF) {          // if closing comment found or EOF
									state = substate;
									return LatteTopTokenId.LATTE;	// it is comment macro
								}
								input.backup(1);
							}
							if(c == EOF) {
								return LatteTopTokenId.HTML;		// else it is HTML
							}
							c = input.read();
							cc = (char)c;
						}
					}
                    state = State.AFTER_LD;								// it is macro - change to after
                    while(true) {
                        cc = (char)c;
                        if(c == '{') {									// another { char - possibly not a macro
                            input.backup(1);
                            return LatteTopTokenId.HTML;
                        }
                        if(c == '}') {									// end macro delim
                            state = substate;
                            return LatteTopTokenId.LATTE;
                        }
                        if(c == EOF) {									// EOF - just HTML
                            state = substate;
                            return LatteTopTokenId.HTML;
                        }
                        c = input.read();
                    }
                }
                //parsing <tag n:attr>
                if(state == State.OUTER && c == '<') {					// tag opening char found
                    if(input.readLength() > 1) {						// if it is part of longer text
                        input.backup(1);								// exclude it
                        break;											// end return the rest
                    }
                    c = input.read();									// next char after <
                    int i = 0;
                    while(true) {
                        if(!Character.isLetter(c) || c == EOF) {
                            if((c != '/' || i != 0) && c != ':' && c != '-') {
                                input.backup(1);
                                if(input.readLength() == 1) {					// nothing found - HTML
                                    state = State.OUTER;
                                    return LatteTopTokenId.HTML;
                                }
                                if(input.readText().toString().startsWith(("<n:"))) {		// <n:tag !
                                    state = State.IN_LATTE_TAG;
                                    property = input.readText().toString().substring(3);	// gets macro name
                                    return LatteTopTokenId.LATTE_TAG;
                                } else {										// normal HTMl tag
                                    state = State.IN_HTML_TAG;
                                    return LatteTopTokenId.HTML_TAG;
                                }
                            }
                        }
                        c = input.read();
                        i++;
                    }
                }
                if(state == State.IN_HTML_TAG || state == State.IN_LATTE_TAG) {	// inside <tag > or <n:tag >
                    if(c == '/') {
                        if(input.readLength() > 1) {					// don't want any preceding characters
                            input.backup(1);
                            break;
                        }
                        c = input.read();
                        if(c == '>') {									// closing no-pair tag <tag />
                            if(state == State.IN_LATTE_TAG) {
								state = State.OUTER;
                                return LatteTopTokenId.LATTE_TAG;		// <n:tag />
							} else {
								state = State.OUTER;
                                return LatteTopTokenId.HTML_TAG;		// <tag />
							}
                        }
                        input.backup(1);
                        break;
                    }
                    if(c == '>') {										// closing tag
                        if(input.readLength() > 1) {					// don't want any preceding characters
                            input.backup(1);
                            break;
                        }
                        if(state == State.IN_LATTE_TAG) {
                            state = State.OUTER;
                            return LatteTopTokenId.LATTE_TAG;			// intetional TAG
                        } else {
                            state = State.OUTER;
                            return LatteTopTokenId.HTML;				// return normal HTML
                        }
                    }
                    if(c == '<') {										// opening tag
                        if(input.readLength() > 1) {					// don't want any preceding characters
                            input.backup(1);
                            if(state == State.IN_LATTE_TAG) {
                                state = State.OUTER;
                                return LatteTopTokenId.LATTE_TAG;		// intetional TAG
                            } else {
                                state = State.OUTER;
                                return LatteTopTokenId.HTML;
                            }
                        }
                    }
                    if(c == '"') {										// found opening qoute for attribute
                        substate = state;								// store top state (to return to later)
                        if(state == State.IN_LATTE_TAG)
                            state = State.IN_LATTE_ATTR;				// in n:attr
                        else
                            state = State.IN_HTML_ATTR;					// in html attr
                        break;
                    }
                    if(c == ' ') {
						//FIXME: should tokenize all whitespace into one whole
                        return LatteTopTokenId.HTML;					// whitespace - html
                    }
                    if(c == 'n') {										// possible start of nette namespace (n:)
                        c = input.read();
                        if(c == ':') {									// found nette namespace
                            property = "";								// clear macro name
                            while(c != EOF) {
                                c = input.read();
                                if(Character.isLetter(c))				// attribute name (macro name)
                                    property += (char)c;				// add next char of macro name
                                else if(c == '-')						// for tag- and inner- prefixes
                                    property = "";						// do not want prefixes in macro name
								else if(c == '"') {							// starting attr value
                                    substate = state;					// stores top state
                                    state = State.IN_LATTE_ATTR;		// in latte attr
                                    return LatteTopTokenId.HTML;
                                } else if(c != '=') {
									return LatteTopTokenId.HTML;
								}
                            }
                            property = null;
                        }
                    }
                }
                if(state == State.IN_LATTE_ATTR && c == '"') {			// in n:attr value and found closing quote
                    if(input.readLength() == 1) {						// there is no value
                        state = substate;								// returning to top state
                        substate = null;
                        break;
                    } else {
                        input.backup(1);								// do not want the trailing quote
                        state = State.IN_LATTE_ATTR_CLOSE;				// closing state see below
                        return LatteTopTokenId.LATTE;					// and it is latte!
                    }
                }
                if(state == State.IN_LATTE_ATTR_CLOSE && c == '"') {	// closing n:attr
                    state = substate;									// returning to top state
                    substate = null;
                    return LatteTopTokenId.HTML;						// quote itself is HTML ofcourse
                }
                if(state == State.IN_HTML_ATTR && c == '"') {			// closing html attr
                    substate = null;
                    state = State.IN_HTML_TAG;							// returning back to HTML tag
                }
                
                c = input.read();
            }

			if(state == null) {
				return LatteTopTokenId.HTML;
			}
            switch (state) {
                case IN_LATTE:
                    return LatteTopTokenId.LATTE;						// if in latte, process as Latte
                default:
                    return LatteTopTokenId.HTML;						// anything else is HTML
            }
        }

        Object getState() {
            return new LexerState(state, substate);
        }
    }

	/**
	 * Stores information about state and substate for future lexer (to start where it ended before)
	 */
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

	/**
	 * Provides setting and getting the macro name for n:attr (as a property of a token)
	 * @param <LatteTopTokenId>
	 */
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
