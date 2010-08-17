/*
 */

package org.netbeans.modules.php.nette.gsf;

import org.netbeans.modules.php.nette.gsf.LatteGSFParser;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.StructureScanner;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.php.nette.lexer.LatteTopTokenId;

/**
 * Registers new language to mime-type text/latte-template for .phtml files
 * @author Radek Ježdík
 */
@LanguageRegistration(mimeType="text/latte-template")
public class LatteLanguage extends DefaultLanguageConfig {

    @Override
    public boolean isIdentifierChar(char c) {
         /** Includes things you'd want selected as a unit when double clicking in the editor */
        return Character.isJavaIdentifierPart(c) || (c == '$') ||(c == '_');
    }

    @Override
    public String getDisplayName() {
        return "Latte Template"; //NOI18N
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

}
