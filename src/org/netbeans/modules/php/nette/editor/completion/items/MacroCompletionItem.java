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

package org.netbeans.modules.php.nette.editor.completion.items;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;

/**
 * Colors the macro completion item
 * @author Radek Ježdík
 */
public class MacroCompletionItem extends BaseCompletionItem {

    int placeCaret = 0;
    boolean isAttr = false;

    public MacroCompletionItem(String text, int dotOffset, int caretOffset) {
        this(text, dotOffset, caretOffset, false, 0);
    }
    
    public MacroCompletionItem(String text, int dotOffset, int caretOffset, boolean isAttr) {
        this(text, dotOffset, caretOffset, isAttr, 0);
    }

    public MacroCompletionItem(String text, int dotOffset, int caretOffset, boolean isAttr, int placeCaret) {
        super(text, dotOffset, caretOffset);
        fieldColor = Color.decode("0xDD0000");
        this.placeCaret = placeCaret;
        this.isAttr = isAttr;
    }

    @Override
    public void defaultAction(JTextComponent jtc) {
        try {
            StyledDocument doc = (StyledDocument) jtc.getDocument();
            doc.remove(dotOffset, caretOffset-dotOffset);
            if(!isAttr) {
                doc.insertString(dotOffset, text, null);
            } else if(placeCaret > 0) {
                doc.insertString(dotOffset, text, null);
                jtc.setCaretPosition(dotOffset + placeCaret);
            } else {
                doc.insertString(dotOffset, text+"=\"\"", null);
                jtc.setCaretPosition(dotOffset + text.length() + 2);
            }
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            Logger.getLogger(BaseCompletionItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getSortPriority() {
        return 90;
    }
}
