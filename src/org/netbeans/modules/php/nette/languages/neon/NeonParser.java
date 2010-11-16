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

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.api.Error.Badging;
import org.netbeans.modules.csl.api.Severity;
import org.netbeans.modules.csl.spi.DefaultError;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.filesystems.FileObject;
import org.openide.text.NbDocument;

/**
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public class NeonParser extends Parser {

	private Snapshot snapshot;

    private CCNeonParser ccNeonParser;

	@Override
	public void parse(Snapshot snapshot, Task task, SourceModificationEvent sme) {
		this.snapshot = snapshot;
        Reader reader = new StringReader(snapshot.getText().toString());
        ccNeonParser = new CCNeonParser(reader);
        try {
            ccNeonParser.Start();
        } catch (ParseException ex) {
            Logger.getLogger(NeonParser.class.getName()).log(Level.WARNING, null, ex);
        }
	}

	@Override
	public Result getResult(Task task) {
		return new NeonParserResult(snapshot, ccNeonParser);
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

	public static class NeonParserResult extends ParserResult {

		private CCNeonParser ccNeonParser;

		private boolean valid = true;

		NeonParserResult(Snapshot snapshot, CCNeonParser ccNeonParser) {
			super(snapshot);
			this.ccNeonParser = ccNeonParser;
		}

		public CCNeonParser getCCNeonParser() throws org.netbeans.modules.parsing.spi.ParseException {
			if (!valid) {
				throw new org.netbeans.modules.parsing.spi.ParseException();
			}

			return ccNeonParser;
		}

		@Override
		protected void invalidate() {
			valid = false;
		}

		@Override
		public List<? extends Error> getDiagnostics() {
            List<ParseException> syntaxErrors = ccNeonParser.syntaxErrors;
            Document document = getSnapshot().getSource().getDocument(false);
            List<Error> errors = new ArrayList<Error>();

            for (ParseException syntaxError : syntaxErrors) {
                Token token = syntaxError.currentToken;
                int start = NbDocument.findLineOffset((StyledDocument) document, token.beginLine - 1) + token.beginColumn - 1;
                int end = NbDocument.findLineOffset((StyledDocument) document, token.endLine - 1) + token.endColumn;

				errors.add(new NeonBadgingError(
					null,
					syntaxError.getMessage(),
					syntaxError.getMessage(),
					getSnapshot().getSource().getFileObject(),
					start,
					end,
					true,
					Severity.ERROR
				));
            }

			return Collections.unmodifiableList(errors);
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

}
