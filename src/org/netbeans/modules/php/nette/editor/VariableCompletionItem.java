/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;

/**
 * Representers completion item for variable (is super class for others) //FIXME: ?
 * @author redhead
 */
public class VariableCompletionItem implements CompletionItem {

    protected Color fieldColor = Color.decode("0xEE7700");
    
    protected String text;
    protected int dotOffset;
    protected int caretOffset;

    public VariableCompletionItem(String text, int dotOffset, int caretOffset) {
        this.text = text;
        this.dotOffset = dotOffset;
        this.caretOffset = caretOffset;
    }


    public void defaultAction(JTextComponent jtc) {
        try {
            StyledDocument doc = (StyledDocument) jtc.getDocument();
            doc.remove(dotOffset, caretOffset-dotOffset);
            doc.insertString(dotOffset, text, null);
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            Logger.getLogger(LatteCompletionItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void processKeyEvent(KeyEvent ke) {
        
    }

    public int getPreferredWidth(Graphics grphcs, Font font) {
        String s = text.replace("<", "&lt;").replace("\"", "&quot;");
        return CompletionUtilities.getPreferredWidth(s, null, grphcs, font);
    }

    public void render(Graphics grphcs, Font font, Color color, Color color1, int width, int height, boolean selected) {
        String s = text.replace("<", "&lt;").replace("\"", "&quot;");
        CompletionUtilities.renderHtml(null, s, null, grphcs, font,
            (selected ? Color.white : fieldColor), width, height, selected);
    }

    public CompletionTask createDocumentationTask() {
        return null;
    }

    public CompletionTask createToolTipTask() {
        return null;
    }

    public boolean instantSubstitution(JTextComponent jtc) {
        defaultAction(jtc);
        return true;
    }

    public int getSortPriority() {
        return 0;
    }

    public CharSequence getSortText() {
        return text;
    }

    public CharSequence getInsertPrefix() {
        return text;
    }

}
