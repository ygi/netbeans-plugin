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

	@Override
	public void parse(Snapshot snpsht, Task task, SourceModificationEvent sme) throws ParseException {
		String source = asString(snpsht.getText());
		lastResult = new NeonParserResult(snpsht);

		//StringBuilder sb = new StringBuilder();
		TokenHierarchy hi = TokenHierarchy.create(source, NeonTokenId.getLanguage());
		TokenSequence ts = hi.tokenSequence();
		String errMsg = NbBundle.getMessage(NeonParser.class, "ERR_unexpected_character");

		while (ts.moveNext()) {
			Token t = ts.token();
			TokenId id = t.id();
			if (id == NeonTokenId.T_ERROR) {
				lastResult.addError(new NeonBadgingError(
						null,
						errMsg,
						errMsg,
						snpsht.getSource().getFileObject(),
						ts.offset(),
						ts.offset() + t.length(),
						//true /* not line error */,
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
