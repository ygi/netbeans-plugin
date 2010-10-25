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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;


/**
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public class NeonParserResult extends ParserResult {

	private final List<Error> errors = new ArrayList<Error>();

	public NeonParserResult(Snapshot snapshot) {
		super(snapshot);
	}

	@Override
	public synchronized List<? extends Error> getDiagnostics() {
		return Collections.unmodifiableList(errors);
	}

	@Override
	protected void invalidate() {
		//throw new UnsupportedOperationException("Not supported yet.");
	}

	public void addError(Error error) {
		errors.add(error);
    }

}
