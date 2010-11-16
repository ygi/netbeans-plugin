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

/**
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
class NeonLexer implements Lexer<NeonTokenId> {

	private LexerRestartInfo<NeonTokenId> info;

    private CCNeonParserTokenManager ccNeonParserTokenManager;

    NeonLexer(LexerRestartInfo<NeonTokenId> info) {
        this.info = info;

		StringBuilder sb = new StringBuilder();

		int ch;
		do {
			ch = info.input().read();
			sb.append((char) ch);
		} while(ch != LexerInput.EOF);

        SimpleCharStream stream = new SimpleCharStream(info.input());
		//SimpleCharStream stream = new SimpleCharStream(new StringReader(sb.toString()));
        ccNeonParserTokenManager = new CCNeonParserTokenManager(stream);
    }

	@Override
    public Token<NeonTokenId> nextToken () {
		org.netbeans.modules.php.nette.languages.neon.Token token = null;
		try {
			token = ccNeonParserTokenManager.getNextToken();
			if (info.input().readLength() < 1) {
				return null;
			}
		} catch (TokenMgrError ex) {
			return info.tokenFactory().createToken(NeonLanguageHierarchy.getToken(CCNeonParserConstants.EOL));
		}
		
		if (info.input().readLength() < 1) {
			return null;
		}
        return info.tokenFactory().createToken(NeonLanguageHierarchy.getToken(token.kind));
    }

	@Override
    public Object state () {
        return null;
    }

	@Override
    public void release () {

    }

}
