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

	@Override
    public void reformat(Context context, ParserResult pr) {

    }

	@Override
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

	@Override
    public boolean needsParserResult() {
        return false;
    }

	@Override
    public int indentSize() {
        return 4;
    }

	@Override
    public int hangingIndentSize() {
        return 4;
    }
}
