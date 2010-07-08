/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.editor;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;

/**
 * Colors the macro completion item
 * @author redhead
 */
public class MacroCompletionItem extends VariableCompletionItem {

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
            Logger.getLogger(LatteCompletionItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getSortPriority() {
        return 90;
    }
}
