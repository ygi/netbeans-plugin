/*
 *  The MIT License
 * 
 *  Copyright (c) 2010 Radek Ježdík <redhead@email.cz>, Ondřej Brejla <ondrej@brejla.cz>
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package org.netbeans.modules.php.nette.languages.neon;

import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerInput;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.spi.lexer.TokenFactory;

/**
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
class NeonLexer implements Lexer<NeonTokenId> {
	
	private final NeonColoringLexer scanner;

	private final TokenFactory<NeonTokenId> tokenFactory;

	enum State {
		OUTER,
		BRACKET,
		COMMENT,
		IN_OBJECT,
		IN_APOSTROPHE_STRING,
		IN_QUOTED_STRING,
		IN_ASSIGNMENT,
		IN_TEXT,
		IN_BRACKETS,
		IN_VARIABLE,
		IN_NAME,
		IN_INNER_NAME,
		IN_VALUE,
		IN_KEYWORD,
		NEW_LINE,
		ERROR,
		WHITESPACE,
		KEYWORD
	}

	public NeonLexer(LexerRestartInfo<NeonTokenId> lri) {
		scanner = new NeonColoringLexer(lri, (State) lri.state());
		this.tokenFactory = lri.tokenFactory();
	}

	@Override
	public Token<NeonTokenId> nextToken() {
		NeonTokenId tokenId = scanner.nextToken();

		if(tokenId == null) {
			return null;
		}

		Token<NeonTokenId> token = null;
		/*
		if (tokenId != null) {
			token = tokenFactory.createToken(tokenId);
		}
		*/
		
        token = (tokenId.getText() != null)
				? tokenFactory.getFlyweightToken(tokenId, tokenId.getText())
				: tokenFactory.createToken(tokenId);


		return token;
	}

	private class NeonColoringLexer {

		private State nextState;

		private State currentState;

		private State previousState;

		private LexerInput input;

		private String[] keywords = {"true", "false", "yes", "no", "null"};

		public NeonColoringLexer(LexerRestartInfo<NeonTokenId> lri, State state) {
			this.input = lri.input();
			
			if (state == null) {
				this.previousState = State.IN_NAME;
				this.currentState = State.IN_NAME;
				this.nextState = State.IN_NAME;
			} else {
				this.previousState = state;
				this.currentState = state;
				this.nextState = state;
			}
		}

		public NeonTokenId nextToken() {
			handleStates();
			int c = input.read();

			if (c == LexerInput.EOF) {
				return null;
			}

			//while (c != LexerInput.EOF) {
				char cc = (char) c;

				switch (cc) {
					case ':':
						setNextState(State.IN_VALUE);
						break;
					case '=':
						setNextState(State.IN_VALUE);
						break;
					case '(':
						setCurrentState(State.BRACKET);
						setNextState(State.IN_VALUE);
						break;
					case ')':
						setCurrentState(State.BRACKET);
						setNextState(previousState);
						break;
					case '[':
						setCurrentState(State.BRACKET);
						setNextState(State.IN_VALUE);
						break;
					case ']':
						setCurrentState(State.BRACKET);
						setNextState(previousState);
						break;
					case '{':
						setCurrentState(State.BRACKET);
						setNextState(State.IN_VALUE);
						break;
					case '}':
						setCurrentState(State.BRACKET);
						setNextState(previousState);
						break;
					case '@':
						setCurrentState(State.IN_OBJECT);
						break;
					case ' ':
						if (!inString() && /*!(*/currentState != State.IN_OBJECT/* || currentState == State.IN_VALUE || currentState == State.IN_BRACKETS)*/) {
							setCurrentState(State.OUTER);
						}
						if (currentState == State.IN_OBJECT/* || currentState == State.IN_VALUE*/) {
							setCurrentState(State.ERROR);
							setNextState(previousState);
						}
						break;
					case '\n':
						setNextState(State.IN_NAME);
						break;
					case '\'':
						if (currentState == State.IN_APOSTROPHE_STRING) {
							setNextState(previousState);
						} else {
							setCurrentState(State.IN_APOSTROPHE_STRING);
						}
						break;
					case '"':
						if (currentState == State.IN_QUOTED_STRING) {
							setNextState(previousState);
						} else {
							setCurrentState(State.IN_QUOTED_STRING);
						}
						break;
					case '%':
						if (currentState == State.IN_VARIABLE) {
							setNextState(previousState);
						} else {
							setCurrentState(State.IN_VARIABLE);
						}
						break;
					case ',':
						setCurrentState(State.OUTER);
						break;
					case '\t':
						setCurrentState(State.OUTER);
						setNextState(State.IN_INNER_NAME);
						break;
					case '#':
						if (!inString()) {
							char comCh = ' ';
							do {
								comCh = (char) input.read();
							} while (!Character.valueOf(comCh).equals('\n'));
							input.backup(1);

							setCurrentState(State.COMMENT);
						}
						break;
					case 'F':
					case 'f':
					case 't':
					case 'T':
					case 'n':
					case 'N':
					case 'y':
					case 'Y':
						if (!inString()) {
							char newCh = (char) input.read();
							String text = "" + cc;

							while (!Character.isWhitespace(newCh) && !Character.valueOf(newCh).equals(',')) {
								text += newCh;

								newCh = (char) input.read();
							}
							input.backup(1);

							for (String keyword : keywords) {
								if (text.equals(keyword.toLowerCase()) || text.equals(keyword.toUpperCase())) {
									setCurrentState(State.KEYWORD);
									break;
								}
							}
						}
						break;
				}

				//if (!inString() && currentState != State.IN_NAME && currentState != State.IN_INNER_NAME && currentState != State.IN_BRACKETS && currentState != State.COMMENT && currentState != State.NEW_LINE) {
				/*
				if (currentState == State.IN_VALUE) {
					input.backup(1);
					char lastChar = (char) input.read();
					input.read();

					if (Character.valueOf(lastChar).equals(' ')) {
						setCurrentState(State.ERROR);
					}
				}*/
/*
				char newCh = (char) input.read();
				int counter = 1;
				while (String.valueOf(newCh).matches("[a-zA-Z0-9_$\\:=/]")) {
					if (newCh == ':' || newCh == '=') {
						newCh = (char) input.read();
						counter++;
						if (newCh != ':') {
							setNextState(State.IN_NAME);
							input.backup(counter);

							return NeonTokenId.T_ASSIGNMENT;
						}
					}

					newCh = (char) input.read();
					counter++;
				}
				input.backup(counter);
*/

				return handleToken();

				//c = input.read();
			//}
			
			//return NeonTokenId.T_ERROR;
		}

		public Object getState() {
			return new Object();
		}

		private boolean inString() {
			return currentState == State.IN_APOSTROPHE_STRING || currentState == State.IN_QUOTED_STRING;
		}

		private NeonTokenId handleToken() {
			switch (currentState) {
				case NEW_LINE:
				case IN_NAME:
					return NeonTokenId.T_NAME;
				case IN_INNER_NAME:
					return NeonTokenId.T_INNER_NAME;
				case IN_VALUE:
				case IN_BRACKETS:
					return NeonTokenId.T_VALUE;
				case BRACKET:
					return NeonTokenId.T_BRACKET;
				case COMMENT:
					return NeonTokenId.T_COMMENT;
				case IN_APOSTROPHE_STRING:
				case IN_QUOTED_STRING:
					return NeonTokenId.T_STRING;
				case IN_OBJECT:
					return NeonTokenId.T_OBJECT;
				case IN_VARIABLE:
					return NeonTokenId.T_VARIABLE;
				case KEYWORD:
				case IN_KEYWORD:
					return NeonTokenId.T_KEYWORD;
				case OUTER:
					return NeonTokenId.T_WHITESPACE;
				case ERROR:
					return NeonTokenId.T_ERROR;
				default:
					return NeonTokenId.T_ERROR;
			}
		}

		private void handleStates() {
			if (currentState != nextState) {
				previousState = currentState;
				currentState = nextState;
			}

			if ((currentState == State.OUTER || currentState == State.BRACKET || currentState == State.KEYWORD)
					&& previousState != State.IN_KEYWORD && previousState != State.IN_OBJECT) {
				currentState = previousState;
			}
		}

		private void setPreviousState(State previousState) {
			this.previousState = previousState;
		}

		private void setCurrentState(State currentState) {
			setPreviousState(this.currentState);
			this.currentState = currentState;
			setNextState(currentState);
		}

		private void setNextState(State nextState) {
			this.nextState = nextState;
		}

	}

	@Override
	public Object state() {
		return scanner.getState();
	}

	@Override
	public void release() {
	}

}
