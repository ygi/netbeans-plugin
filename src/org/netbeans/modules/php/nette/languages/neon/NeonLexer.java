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
		IN_ARRAY_KEY,
		IN_ARRAY_VALUE,
		IN_VARIABLE,
		IN_QUOTATION_STRING,
		IN_APOSTROPHE_STRING
	}

	public NeonLexer(LexerRestartInfo<NeonTokenId> lri) {
		scanner = new NeonColoringLexer(lri, (NeonLexerState) lri.state());
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

		private final char COMMA = ',';

		private final char LT = '<';

		private final char GT = '>';

		private final char DASH = '-';

		private final char DOT = '.';
		
		private final String ASSIGN = "=>";

		public NeonColoringLexer(LexerRestartInfo<NeonTokenId> lri, NeonLexerState state) {
			this.input = lri.input();
			
			if (state == null) {
				this.state = State.IN_KEY;
			} else {
				this.state = state.getState();
				this.previousStates = state.getPreviousStates();
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
						if (isPartOfNumber(cc)) {
							fetchNumber();
							return NeonTokenId.T_NUMBER;
						}
						if (cc != DASH && isPartOfLiteral(cc) && !isPartOfNumber(cc)) {
							fetchLiteral();
							if (existsValuePart()) {
								return NeonTokenId.T_KEY;
							} else {
								return NeonTokenId.T_BLOCK;
							}
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
						if (isKeyword(cc)) {
							return NeonTokenId.T_KEYWORD;
						}
						if (isPartOfNumber(cc)) {
							fetchNumber();
							return NeonTokenId.T_NUMBER;
						}
						if (isPartOfLiteral(cc) && !isKeyword(cc) && !isPartOfNumber(cc)) {
							fetchLiteral();
							return NeonTokenId.T_LITERAL;
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
							//previousStates.push(state);
							state = newState;
							return NeonTokenId.T_QUOTATION_MARK;
						}
						if (cc == NEW_LINE) {
							previousStates.push(state);
							state = State.IN_KEY;
							return NeonTokenId.T_NEW_LINE;
						}
						fetchString(QUOTATION_MARK);
						return NeonTokenId.T_STRING;
					case IN_APOSTROPHE_STRING:
						if (cc == APOSTROPHE) {
							State newState = previousStates.pop();
							//previousStates.push(state);
							state = newState;
							return NeonTokenId.T_APOSTROPHE;
						}
						if (cc == NEW_LINE) {
							previousStates.push(state);
							state = State.IN_KEY;
							return NeonTokenId.T_NEW_LINE;
						}
						fetchString(APOSTROPHE);
						return NeonTokenId.T_STRING;
					case IN_CURLY_ARRAY:
					case IN_SQUARED_ARRAY:
						if (cc == SPACE || cc == TAB) {
							fetchWhitespace();
							return NeonTokenId.T_WHITESPACE;
						}
						if (cc == NEW_LINE) {
							previousStates.push(state);
							state = State.IN_KEY;
							return NeonTokenId.T_NEW_LINE;
						}
						
						previousStates.push(state);
						if (existsOnlyKeyPart()) {
							state = State.IN_ARRAY_KEY;
						} else {
							state = State.IN_ARRAY_VALUE;
						}
						input.backup(1);
						break;
						//return NeonTokenId.T_ERROR;
					case IN_ARRAY_VALUE:
						if (cc == SPACE || cc == TAB) {
							fetchWhitespace();
							return NeonTokenId.T_WHITESPACE;
						}
						if (cc == RIGHT_CURLY) {
							State newState = previousStates.pop();
							state = newState;
							return NeonTokenId.T_RIGHT_CURLY;
						}
						if (cc == RIGHT_SQUARED) {
							State newState = previousStates.pop();
							state = newState;
							return NeonTokenId.T_RIGHT_SQUARED;
						}
						if (cc == PERCENTAGE) {
							previousStates.push(state);
							state = State.IN_VARIABLE;
							return NeonTokenId.T_VARIABLE;
						}
						if (cc == HASH) {
							fetchComment();
							return NeonTokenId.T_COMMENT;
						}
						if (cc == QUOTATION_MARK) {
							//previousStates.push(state);
							state = State.IN_QUOTATION_STRING;
							return NeonTokenId.T_QUOTATION_MARK;
						}
						if (cc == APOSTROPHE) {
							//previousStates.push(state);
							state = State.IN_APOSTROPHE_STRING;
							return NeonTokenId.T_APOSTROPHE;
						}
						if (cc == GT) {
							return NeonTokenId.T_INTERPUNCTION;
						}
						if (cc == COMMA) {
							State newState = previousStates.pop();
							state = newState;
							return NeonTokenId.T_INTERPUNCTION;
						}
						if (cc == NEW_LINE) {
							previousStates.push(state);
							state = State.IN_KEY;
							return NeonTokenId.T_NEW_LINE;
						}
						if (isPartOfNumber(cc)) {
							fetchNumber();
							return NeonTokenId.T_NUMBER;
						}
						if (cc != DASH && isPartOfLiteral(cc) && !isPartOfNumber(cc)) {
							fetchLiteral();
							return NeonTokenId.T_LITERAL;
						}
						return NeonTokenId.T_ERROR;
					case IN_ARRAY_KEY:
						if (cc == SPACE || cc == TAB) {
							fetchWhitespace();
							return NeonTokenId.T_WHITESPACE;
						}
						if (cc == RIGHT_CURLY) {
							State newState = previousStates.pop();
							state = newState;
							return NeonTokenId.T_RIGHT_CURLY;
						}
						if (cc == RIGHT_SQUARED) {
							State newState = previousStates.pop();
							state = newState;
							return NeonTokenId.T_RIGHT_SQUARED;
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
						if (cc == COLON || cc == EQUALS) {
							//previousStates.push(state);
							state = State.IN_ARRAY_VALUE;
							return NeonTokenId.T_INTERPUNCTION;
						}
						if (isPartOfNumber(cc)) {
							fetchNumber();
							return NeonTokenId.T_NUMBER;
						}
						if (cc != DASH && isPartOfLiteral(cc) && !isPartOfNumber(cc)) {
							fetchLiteral();
							return NeonTokenId.T_KEY;
						}
						return NeonTokenId.T_ERROR;
				}

				c = input.read();
			}
			
			return NeonTokenId.T_ERROR;
		}

		public Object getState() {
			return new NeonLexerState(state, previousStates);
		}

		/**
		 * Checks if there exists value part of the block (key): key: value
		 *
		 * @return
		 */
		private boolean existsValuePart() {
			char newCh;
			int counter = 0;
			boolean result = false;

			// fetch 'key' part
			do {
				newCh = (char) input.read();
				counter++;
			} while (newCh != COLON && newCh != NEW_LINE);
			
			// find whatever except whitespace, comment or comma in value part
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

		/**
		 * Checks if there is only a key part in array part which is delimited by comma [key: value, whatever...]
		 *
		 * @return
		 */
		private boolean existsOnlyKeyPart() {
			char newCh;
			int counter = 0;
			boolean result = false;

			do {
				newCh = (char) input.read();
				counter++;

				if (newCh == COLON || newCh == EQUALS) {
					result = true;
					break;
				}
			} while (newCh != COMMA && newCh != NEW_LINE && newCh != RIGHT_CURLY && newCh != RIGHT_SQUARED && newCh != HASH);
			input.backup(counter);

			return result;
		}

		/**
		 * Checks if passed character could be part of a number.
		 *
		 * @param ch
		 * @return
		 */
		private boolean isPartOfNumber(char ch) {
			char newCh;
			char lastCh;
			int counter = 0;
			int dots = 0;
			boolean result = false;

			lastCh = ch;

			if (lastCh == DASH || Character.isDigit(lastCh)) {
				newCh = (char) input.read();
				counter++;
				if (newCh == DOT) {
					dots++;
				}
				while (Character.isDigit(newCh) || (newCh == '.' && lastCh != DASH)) {
					lastCh = newCh;
					newCh = (char) input.read();
					counter++;

					if (newCh == DOT) {
						dots++;
						if (dots > 1) {
							break;
						}
					} else if (Character.isWhitespace(newCh) || newCh == COLON || newCh == EQUALS || newCh == COMMA || newCh == RIGHT_CURLY || newCh == RIGHT_SQUARED) {
						result = true;
						break;
					}
				}
				
				input.backup(counter);
			}
			
			return result;
		}

		/**
		 * Moves caret to the end of a number.
		 */
		private void fetchNumber() {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (!Character.isWhitespace(newCh) && newCh != COLON && newCh != EQUALS && newCh != COMMA && newCh != RIGHT_CURLY && newCh != RIGHT_SQUARED);
			input.backup(1);
		}

		/**
		 * Checks if passed character could be part of a literal.
		 *
		 * @param ch
		 * @return
		 */
		private boolean isPartOfLiteral(char ch) {
			if (!String.valueOf(ch).matches("[!#&,:;<=>@%\"\\$\'\\(\\)\\*\\+\\?\\[\\]\\^\\{\\|\\}\\s]")) {
				return true;
			}

			return false;
		}

		/**
		 * Moves caret to the end of a literal.
		 */
		private void fetchLiteral() {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (isPartOfLiteral(newCh));
			input.backup(1);
		}

		/**
		 * Checks if passed character could be part of a variable.
		 *
		 * @param ch
		 * @return
		 */
		private boolean isPartOfVariable(char ch) {
			if (isPartOfLiteral(ch) && ch != PERCENTAGE) {
				return true;
			}

			return false;
		}

		/**
		 * Moves caret to the end of a string (before endMark).
		 *
		 * @param endMark
		 */
		private void fetchString(char endMark) {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (newCh != endMark && newCh != NEW_LINE);
			input.backup(1);
		}

		/**
		 * Moves caret to the end of a variable (before variable delimiter).
		 */
		private void fetchVariable() {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (isPartOfLiteral(newCh));
			input.backup(1);
		}

		/**
		 * Moves caret to the end of whitespaces.
		 */
		private void fetchWhitespace() {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (newCh == SPACE || newCh == TAB);
			input.backup(1);
		}

		/**
		 * Moves caret to the end of a comment.
		 */
		private void fetchComment() {
			char newCh;
			do {
				newCh = (char) input.read();
			} while (newCh != NEW_LINE);
			input.backup(1);
		}

		/**
		 * Checks if passed character is first character of whole keyword.
		 *
		 * @param firstLetter
		 * @return
		 */
		private boolean isKeyword(char firstLetter) {
			if (firstLetter == 'f' || firstLetter == 'F'
					|| firstLetter == 't' || firstLetter == 'T' || firstLetter == 'y'
					|| firstLetter == 'Y' || firstLetter == 'n' || firstLetter == 'N') {
				char newCh = (char) input.read();
				int counter = 0;
				String text = "" + firstLetter;

				while (!Character.isWhitespace(newCh) && newCh != COMMA && newCh != QUOTATION_MARK && newCh != APOSTROPHE) {
					text += newCh;

					newCh = (char) input.read();
					counter++;
				}
				// exclude last character (whitespace, comma or string delimiter)
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

	private class NeonLexerState {

		private State state;

		private Stack<State> previousStates;

		public NeonLexerState(State state, Stack<State> previousStates) {
			this.state = state;
			this.previousStates = previousStates;
		}

		public State getState() {
			return state;
		}

		public Stack<State> getPreviousStates() {
			return previousStates;
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
