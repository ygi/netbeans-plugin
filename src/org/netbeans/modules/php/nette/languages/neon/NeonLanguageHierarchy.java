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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;
import static org.netbeans.modules.php.nette.languages.neon.CCNeonParserConstants.*;

/**
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public class NeonLanguageHierarchy extends LanguageHierarchy<NeonTokenId> {

	private static List<NeonTokenId> tokens;

	private static Map<Integer, NeonTokenId> idToToken;

	private static void init() {
		tokens = Arrays.<NeonTokenId> asList (new NeonTokenId[] {
            new NeonTokenId("EOF", "whitespace", EOF),
			new NeonTokenId("KEYWORD", "keyword", KEYWORD),
			new NeonTokenId("EXTENDS", "interpunction", EXTENDS),
			new NeonTokenId("COLON", "interpunction", COLON),
			new NeonTokenId("COMMA", "interpunction", COMMA),
			new NeonTokenId("DASH", "interpunction", DASH),
			new NeonTokenId("EQUALS", "interpunction", EQUALS),
			new NeonTokenId("ASSIGN", "interpunction", ASSIGN),
			new NeonTokenId("LEFT_CURLY", "interpunction", LEFT_CURLY),
			new NeonTokenId("RIGHT_CURLY", "interpunction", RIGHT_CURLY),
			new NeonTokenId("LEFT_SQUARED", "interpunction", LEFT_SQUARED),
			new NeonTokenId("RIGHT_SQUARED", "interpunction", RIGHT_SQUARED),
			new NeonTokenId("STRING", "string", STRING),
			//new NeonTokenId("INTEGER", "number", INTEGER),
			//new NeonTokenId("FLOAT", "number", FLOAT),
			new NeonTokenId("NUMBER", "number", NUMBER),
			new NeonTokenId("LITERAL", "literal", LITERAL),
			new NeonTokenId("VARIABLE", "variable", VARIABLE),
			new NeonTokenId("EOL", "whitespace", EOL),
			new NeonTokenId("EMPTY_LINE", "whitespace", EMPTY_LINE),
			new NeonTokenId("COMMENT", "comment", COMMENT),
			new NeonTokenId("SPACE", "whitespace", SPACE),
			new NeonTokenId("UNEXPECTED_CHAR", "error", UNEXPECTED_CHAR)
        });

        idToToken = new HashMap<Integer, NeonTokenId>();

        for (NeonTokenId token : tokens) {
            idToToken.put(token.ordinal(), token);
		}
	}

	public static synchronized NeonTokenId getToken(int id) {
        if (idToToken == null) {
            init();
		}

        return idToToken.get(id);
    }

	@Override
	protected synchronized Collection<NeonTokenId> createTokenIds() {
		if (tokens == null) {
			init();
		}

		return tokens;
	}

	@Override
	protected Lexer<NeonTokenId> createLexer(LexerRestartInfo<NeonTokenId> lri) {
		return new NeonLexer(lri);
	}

	@Override
	protected String mimeType() {
		return "text/x-neon";
	}

}
