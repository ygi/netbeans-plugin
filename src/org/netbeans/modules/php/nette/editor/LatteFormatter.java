/*
 */

package org.netbeans.modules.php.nette.editor;

import javax.swing.text.BadLocationException;
import org.netbeans.modules.csl.api.Formatter;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.php.nette.macros.LatteMacro;
import org.netbeans.modules.php.nette.macros.MacroDefinitions;
import org.openide.util.Exceptions;

/**
 * FIXME !
 * TODO !
 * Nefunguje!
 * Zprovoznit!
 * Rychle!
 * 
 * @author Radek Ježdík
 */
public class LatteFormatter implements Formatter {

    public void reformat(Context context, ParserResult pr) {

    }

    public void reindent(Context context) {
        try {
            int lineStart = context.lineStartOffset(context.caretOffset());
            int indent = context.lineIndent(lineStart);
            int lineStart2 = context.lineStartOffset(lineStart - indent - 1);
            String start = context.document().getText(lineStart2, context.endOffset() - lineStart2);
            if(start.startsWith("{") && Character.isLetter(start.charAt(1))) {
                int i = 1;
                String macro = "";
                while(i < start.length()) {
                    char c = start.charAt(i);
                    if(Character.isLetter(c)) {
                        macro += c;
                    }
					i++;
                }
				LatteMacro m = MacroDefinitions.getMacro(macro);
				if(m != null && m.isPair()) {
					context.modifyIndent(lineStart, indent + indentSize());
				}
            }
        } catch(BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public boolean needsParserResult() {
        return false;
    }

    public int indentSize() {
        return 4;
    }

    public int hangingIndentSize() {
        return 4;
    }
}
