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

import java.util.Stack;
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
		IN_BLOCK_TITLE,
		IN_KEY,
		IN_VALUE,
		IN_CURLY_ARRAY,
		IN_SQUARED_ARRAY,
		IN_VARIABLE,
		IN_QUOTATION_STRING,
		IN_APOSTROPHE_STRING
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

		private State state;

		private Stack<State> previousStates = new Stack<State>();

		private LexerInput input;

		private String[] keywords = {"true", "false", "yes", "no", "null"};

		private final char EQUALS = '=';

		private final char COLON = ':';

		private final char HASH = '#';

		private final char PERCENTAGE = '%';

		private final char LEFT_CURLY = '{';

		private final char RIGHT_CURLY = '}';

		private final char LEFT_SQUARED = '[';

		private final char RIGHT_SQUARED = ']';

		private final char SPACE = ' ';

		private final char NEW_LINE = '\n';

		private final char TAB = '\t';

		private final char APOSTROPHE = '\'';

		private final char QUOTATION_MARK = '"';

		private final char COMA = ',';

		private final char LT = '<';

		private final char DASH = '-';
		
		private final String ASSIGN = "=>";

		public NeonColoringLexer(LexerRestartInfo<NeonTokenId> lri, State state) {
			this.input = lri.input();
			
			if (state == null) {
				this.state = State.IN_KEY;
			} else {
				this.state = state;
			}
		}

		public NeonTokenId nextToken() {
			int c = input.read();

			if (c == LexerInput.EOF) {
				return null;
			}

			while (c != LexerInput.EOF) {
				char cc = (char) c;

				switch (state) {
					case IN_KEY:
						if (cc == SPACE || cc == TAB) {
							fetchWhitespace();
							return NeonTokenId.T_WHITESPACE;
						}
						if (cc == LT) {
							return NeonTokenId.T_INTERPUNCTION;
						}
						if (cc != DASH && isPartOfLiteral(cc) && !isPartOfNumber(cc)) {
							fetchLiteral();
							if (hasValuePart()) {
								return NeonTokenId.T_KEY;
							} else {
								return NeonTokenId.T_BLOCK;
							}
						}
						if (isPartOfNumber(cc)) {
							fetchNumber();
							return NeonTokenId.T_NUMBER;
						}
						if (cc == HASH) {
							fetchComment();
							return NeonTokenId.T_COMMENT;
						}
						if (cc == QUOTATION_MARK) {
							previousStates.push(state);
							state = State.IN_QUOTATION_STRING;
							return NeonTokenId.T_QUOTATION_MARK;
						}
						if (cc == APOSTROPHE) {
							previousStates.push(state);
							state = State.IN_APOSTROPHE_STRING;
							return NeonTokenId.T_APOSTROPHE;
						}
						if (cc == PERCENTAGE) {
							previousStates.push(state);
							state = State.IN_VARIABLE;
							return NeonTokenId.T_VARIABLE;
						}
						if (cc == NEW_LINE) {
							previousStates.push(state);
							state = State.IN_KEY;
							return NeonTokenId.T_NEW_LINE;
						}
						if (cc == COLON || cc == DASH) {
							previousStates.push(state);
							state = State.IN_VALUE;
							return NeonTokenId.T_INTERPUNCTION;
						}
						return NeonTokenId.T_ERROR;
					case IN_VALUE:
						if (cc == SPACE || cc == TAB) {
							fetchWhitespace();
							return NeonTokenId.T_WHITESPACE;
						}
						if (cc == HASH) {
							fetchComment();
							return NeonTokenId.T_COMMENT;
						}
						if (isKeyword(cc)) {
							return NeonTokenId.T_KEYWORD;
						}
						if (isPartOfLiteral(cc) && !isKeyword(cc)) {
							fetchLiteral();
							return NeonTokenId.T_LITERAL;
						}
						if (cc == QUOTATION_MARK) {
							previousStates.push(state);
							state = State.IN_QUOTATION_STRING;
							return NeonTokenId.T_QUOTATION_MARK;
						}
						if (cc == APOSTROPHE) {
							previousStates.push(state);
							state = State.IN_APOSTROPHE_STRING;
							return NeonTokenId.T_APOSTROPHE;
						}
						if (cc == PERCENTAGE) {
							previousStates.push(state);
							state = State.IN_VARIABLE;
							return NeonTokenId.T_VARIABLE;
						}
						if (cc == LEFT_CURLY) {
							previousStates.push(state);
							state = State.IN_CURLY_ARRAY;
							return NeonTokenId.T_LEFT_CURLY;
						}
						if (cc == LEFT_SQUARED) {
							previousStates.push(state);
							state = State.IN_SQUARED_ARRAY;
							return NeonTokenId.T_LEFT_SQUARED;
						}
						if (cc == NEW_LINE) {
							previousStates.push(state);
							state = State.IN_KEY;
							return NeonTokenId.T_NEW_LINE;
						}
						return NeonTokenId.T_ERROR;
					case IN_VARIABLE:
						if (cc == PERCENTAGE) {
							State newState = previousStates.pop();
							//previousStates.push(state);
							state = newState;
							return NeonTokenId.T_VARIABLE;
						}
						if (isPartOfVariable(cc)) {
							fetchVariable();
							return NeonTokenId.T_VARIABLE;
						}
						return NeonTokenId.T_ERROR;
					case IN_QUOTATION_STRING:
						if (cc == QUOTATION_MARK) {
							State newState = previousStates.pop();
							previousStates.push(state);
							state = newState;
							return NeonTokenId.T_QUOTATION_MARK;
						}
						fetchString(QUOTATION_MARK);
						return NeonTokenId.T_STRING;
					case IN_APOSTROPHE_STRING:
						if (cc == APOSTROPHE) {
							State newState = previousStates.pop();
							previousStates.push(state);
							state = newState;
							return NeonTokenId.T_APOSTROPHE;
						}
						fetchString(APOSTROPHE);
						return NeonTokenId.T_STRING;
					case IN_CURLY_ARRAY:
						if (cc == RIGHT_CURLY) {
							State newState = previousStates.pop();
							previousStates.push(state);
							state = newState;
							return NeonTokenId.T_RIGHT_CURLY;
						}
						if (cc == PERCENTAGE) {
							previousStates.push(state);
							state = State.IN_VARIABLE;
							return NeonTokenId.T_VARIABLE;
						}
						if (cc == LEFT_CURLY) {
							return NeonTokenId.T_LEFT_CURLY;
						}
						if (cc == LEFT_SQUARED) {
							return NeonTokenId.T_LEFT_SQUARED;
						}
						if (cc == RIGHT_SQUARED) {
							return NeonTokenId.T_RIGHT_SQUARED;
						}
						if (cc == COMA) {
							return NeonTokenId.T_INTERPUNCTION;
						}
						if (cc == SPACE || cc == TAB) {
							fetchWhitespace();
							return NeonTokenId.T_WHITESPACE;
						}
						return NeonTokenId.T_ERROR;
					case IN_SQUARED_ARRAY:
						if (cc == RIGHT_SQUARED) {
							State newState = previousStates.pop();
							previousStates.push(state);
							state = newState;
							return NeonTokenId.T_RIGHT_SQUARED;
						}
						if (cc == PERCENTAGE) {
							previousStates.push(state);
							state = State.IN_VARIABLE;
							return NeonTokenId.T_VARIABLE;
						}
						if (cc == LEFT_CURLY) {
							return NeonTokenId.T_LEFT_CURLY;
						}
						if (cc == LEFT_SQUARED) {
							return NeonTokenId.T_LEFT_SQUARED;
						}
						if (cc == RIGHT_CURLY) {
							return NeonTokenId.T_RIGHT_CURLY;
						}
						if (cc == COMA) {
							return NeonTokenId.T_INTERPUNCTION;
						}
						if (cc == SPACE || cc == TAB) {
							fetchWhitespace();
							return NeonTokenId.T_WHITESPACE;
						}
						return NeonTokenId.T_ERROR;
				}

				c = input.read();
			}
			
			return NeonTokenId.T_ERROR;
		}

		public Object getState() {
			return new Object();
		}

		private boolean hasValuePart() {
			char newCh;
			int counter = 0;
			boolean result = false;

			// fetch 'key' part
			do {
				newCh = (char) input.read();
				counter++;

			} while (newCh != COLON && newCh != NEW_LINE);

			// find whatever except whitespace or comment in value part
			do {
				newCh = (char) input.read();
				counter++;

				if (newCh == HASH) {
					break;
				} else if (!Character.isWhitespace(newCh)) {
					result = true;
					break;
				}
			} while (Character.isWhitespace(newCh) && newCh != NEW_LINE);
			input.backup(counter);
			
			return result;
		}

		private boolean isPartOfNumber(char ch) {
			char newCh;
			int counter = 0;
			boolean result = false;

			if (ch == '-' || Character.isDigit(ch)) {
				do {
					newCh = (char) input.read();
					counter++;
				} while (Character.isDigit(newCh));
			}
			input.backup(counter);

			return result;
		}

		private void fetchNumber() {
			
		}

		private boolean isPartOfLiteral(char ch) {
			if (!String.valueOf(ch).matches("[\\!\"\\#\\$\\&'\\(\\)\\*\\+,:;<=>\\?@\\[\\]\\^\\{\\|\\}%]") && ch != SPACE && ch != TAB && ch != NEW_LINE) {
				return true;
			}

			return false;
		}

		private void fetchLiteral() {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (isPartOfLiteral(newCh));
			input.backup(1);
		}

		private boolean isPartOfVariable(char ch) {
			if (isPartOfLiteral(ch) && ch != PERCENTAGE) {
				return true;
			}

			return false;
		}

		private void fetchString(char endMark) {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (newCh != endMark);
			input.backup(1);
		}

		private void fetchVariable() {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (isPartOfLiteral(newCh));
			input.backup(1);
		}

		private void fetchWhitespace() {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (newCh == SPACE || newCh == TAB);
			input.backup(1);
		}

		private void fetchComment() {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (newCh != NEW_LINE);
			input.backup(1);
		}

		private boolean isKeyword(char firstLetter) {
			if (firstLetter == 'f' || firstLetter == 'F'
					|| firstLetter == 't' || firstLetter == 'T' || firstLetter == 'y'
					|| firstLetter == 'Y' || firstLetter == 'n' || firstLetter == 'N') {
				char newCh = (char) input.read();
				int counter = 0;
				String text = "" + firstLetter;

				while (!Character.isWhitespace(newCh) && newCh != COMA) {
					text += newCh;

					newCh = (char) input.read();
					counter++;
				}
				// exclude last character (whitespace, or comma)
				input.backup(1);

				for (String keyword : keywords) {
					if (text.equals(keyword.toLowerCase()) || text.equals(keyword.toUpperCase())) {
						return true;
					}
				}

				// it's not a keyword, so rollback to the beginning of whole string
				input.backup(counter);
			}

			return false;
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
