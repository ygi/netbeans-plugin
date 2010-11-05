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

import javax.swing.event.ChangeListener;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.csl.api.Error.Badging;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.csl.spi.DefaultError;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public class NeonParser extends Parser {

	private NeonParserResult lastResult;

	private State state = State.OUTER;

	private static final String ERR_UNEXPECTED_CHARACTER = "ERR_unexpected_character";

	private static final String ERR_MISSING_STRING_DELIMITER = "ERR_missing_string_delimiter";

	private static final String ERR_UNEXPECTED_ARRAY_END = "ERR_unexpected_array_end";

	private static final String ERR_BAD_ARRAY_END_DELIMITER = "ERR_bad_array_end_delimiter";

	private enum State {
		OUTER,
		AFTER_STRING_LD,
		ON_STRING_RD,
		IN_ARRAY,
	}

	@Override
	public void parse(Snapshot snpsht, Task task, SourceModificationEvent sme) throws ParseException {
		String source = asString(snpsht.getText());
		lastResult = new NeonParserResult(snpsht);

		TokenHierarchy hi = TokenHierarchy.create(source, NeonTokenId.getLanguage());
		TokenSequence ts = hi.tokenSequence();
		Token openingToken = null;
		NeonTokenId okClosingTokenId = null;
		NeonTokenId badClosingTokenId = null;

		while (ts.moveNext()) {
			Token t = ts.token();
			TokenId id = t.id();

			switch (state) {
				case OUTER:
					if (id == NeonTokenId.T_APOSTROPHE || id == NeonTokenId.T_QUOTATION_MARK) {
						state = State.AFTER_STRING_LD;
						openingToken = t;
						break;
					}
					if (id == NeonTokenId.T_LEFT_CURLY) {
						okClosingTokenId = NeonTokenId.T_RIGHT_CURLY;
						badClosingTokenId = NeonTokenId.T_RIGHT_SQUARED;
						state = State.IN_ARRAY;
						break;
					}
					if (id == NeonTokenId.T_LEFT_SQUARED) {
						okClosingTokenId = NeonTokenId.T_RIGHT_SQUARED;
						badClosingTokenId = NeonTokenId.T_RIGHT_CURLY;
						state = State.IN_ARRAY;
						break;
					}
					break;
				
				case AFTER_STRING_LD:
					if (id == NeonTokenId.T_STRING) {
						state = State.ON_STRING_RD;
						break;
					}
					if (id == openingToken.id()) {
						state = State.OUTER;
						break;
					}
					lastResult.addError(new NeonBadgingError(
							null,
							NbBundle.getMessage(NeonParser.class, ERR_MISSING_STRING_DELIMITER, openingToken.text()),
							NbBundle.getMessage(NeonParser.class, ERR_MISSING_STRING_DELIMITER, openingToken.text()),
							snpsht.getSource().getFileObject(),
							ts.offset(),
							ts.offset() + t.length(),
							Severity.ERROR));
					state = State.OUTER;
					break;
				case ON_STRING_RD:
					if (id != openingToken.id()) {
						lastResult.addError(new NeonBadgingError(
								null,
								NbBundle.getMessage(NeonParser.class, ERR_MISSING_STRING_DELIMITER, openingToken.text()),
								NbBundle.getMessage(NeonParser.class, ERR_MISSING_STRING_DELIMITER, openingToken.text()),
								snpsht.getSource().getFileObject(),
								ts.offset(),
								ts.offset() + t.length(),
								Severity.ERROR));
					}
					state = State.OUTER;
					break;
				case IN_ARRAY:
					if (id == NeonTokenId.T_NEW_LINE) {
						lastResult.addError(new NeonBadgingError(
								null,
								NbBundle.getMessage(NeonParser.class, ERR_UNEXPECTED_ARRAY_END, okClosingTokenId.getText()),
								NbBundle.getMessage(NeonParser.class, ERR_UNEXPECTED_ARRAY_END, okClosingTokenId.getText()),
								snpsht.getSource().getFileObject(),
								ts.offset(),
								ts.offset() + t.length(),
								Severity.ERROR));
						state = State.OUTER;
					}
					if (id == okClosingTokenId) {
						state = State.OUTER;
						break;
					}
					if (id == badClosingTokenId) {
						lastResult.addError(new NeonBadgingError(
								null,
								NbBundle.getMessage(NeonParser.class, ERR_BAD_ARRAY_END_DELIMITER, okClosingTokenId.getText()),
								NbBundle.getMessage(NeonParser.class, ERR_BAD_ARRAY_END_DELIMITER, okClosingTokenId.getText()),
								snpsht.getSource().getFileObject(),
								ts.offset(),
								ts.offset() + t.length(),
								Severity.ERROR));
						state = State.OUTER;
					}
					break;
			}

			if (id == NeonTokenId.T_ERROR) {
				lastResult.addError(new NeonBadgingError(
						null,
						NbBundle.getMessage(NeonParser.class, ERR_UNEXPECTED_CHARACTER, t.text()),
						NbBundle.getMessage(NeonParser.class, ERR_UNEXPECTED_CHARACTER, t.text()),
						snpsht.getSource().getFileObject(),
						ts.offset(),
						ts.offset(),
						true,
						Severity.ERROR));
			}
		}
	}

	@Override
	public Result getResult(Task task) throws ParseException {
		return lastResult;
	}

	@Override
	public void cancel() {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void addChangeListener(ChangeListener cl) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void removeChangeListener(ChangeListener cl) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	private static String asString(CharSequence sequence) {
		if (sequence instanceof String) {
			return (String)sequence;
		} else {
			return sequence.toString();
		}
	}

	private class NeonBadgingError extends DefaultError implements Badging {

		public NeonBadgingError(String string, String string1, String string2, FileObject fo, int i, int i1, boolean bln, Severity svrt) {
			super(string, string1, string2, fo, i, i1, bln, svrt);
		}

		public NeonBadgingError(String string, String string1, String string2, FileObject fo, int i, int i1, Severity svrt) {
			super(string, string1, string2, fo, i, i1, svrt);
		}

		@Override
		public boolean showExplorerBadge() {
			return true;
		}

	}

}
