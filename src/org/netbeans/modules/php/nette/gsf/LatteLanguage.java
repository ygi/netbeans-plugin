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

package org.netbeans.modules.php.nette.gsf;

import org.netbeans.modules.csl.api.KeystrokeHandler;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.php.nette.editor.LatteBracketCompleter;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;
import org.openide.util.NbBundle;

/**
 * Registers new language to mime-type text/x-latte-template for .phtml files
 * @author Radek Ježdík
 */
@LanguageRegistration(mimeType="text/x-latte-template")
public class LatteLanguage extends DefaultLanguageConfig {

    @Override
    public boolean isIdentifierChar(char c) {
         /** Includes things you'd want selected as a unit when double clicking in the editor */
        return Character.isJavaIdentifierPart(c) || (c == '$') ||(c == '_');
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(LatteLanguage.class, "TXT_latte_template_language"); //NOI18N
    }

    @Override
    public String getPreferredExtension() {
        return "phtml"; // NOI18N
    }

    @Override
    public boolean isUsingCustomEditorKit() {
        return false;
    }

    @Override
    public Language getLexerLanguage() {
        return LatteTopTokenId.language();
    }

    @Override
    public Parser getParser() {
        // we need the parser and the StructureScanner to enable navigator of embedded languages
        return new LatteGSFParser();
    }

    @Override
    public boolean hasStructureScanner() {
        return true;
    }

    @Override
    public StructureScanner getStructureScanner() {
        return new LatteStructureScanner();
    }

	@Override
	public KeystrokeHandler getKeystrokeHandler() {
		return new LatteBracketCompleter();
	}

	/*@Override
	public boolean hasFormatter() {
		return true;
	}

	@Override
	public Formatter getFormatter() {
		return new LatteFormatter();
	}*/

}
