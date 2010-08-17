/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.editor.completion.items;

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
 * @author Radek Ježdík
 */
public class BaseCompletionItem implements CompletionItem {

    protected Color fieldColor = Color.decode("0x000000");
    
    protected String text;				// text to be inserted instead of that between these two:
    protected int dotOffset;			// start offset where completion will happen
    protected int caretOffset;			// caret (cursor) offset (where CTR+SPACE was pressed)

    public BaseCompletionItem(String text, int dotOffset, int caretOffset) {
        this.text = text;
        this.dotOffset = dotOffset;
        this.caretOffset = caretOffset;
    }

	/**
	 * 
	 * @param jtc
	 */
    public void defaultAction(JTextComponent jtc) {
        try {
            StyledDocument doc = (StyledDocument) jtc.getDocument();
            doc.remove(dotOffset, caretOffset-dotOffset);						// remove from begining to caret
            doc.insertString(dotOffset, text, null);							// complete the text
            Completion.get().hideAll();											// hide completion box
        } catch (BadLocationException ex) {
            Logger.getLogger(LatteCompletionItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void processKeyEvent(KeyEvent ke) {
        // nothing? why? who?
    }

	/**
	 * Gets prefered width for completion box base on the text
	 * @param grphcs
	 * @param font
	 * @return 
	 */
    public int getPreferredWidth(Graphics grphcs, Font font) {
        String s = text.replace("<", "&lt;").replace("\"", "&quot;");
        return CompletionUtilities.getPreferredWidth(s, null, grphcs, font);
    }

	/**
	 * Renders the completion item for the completion box
	 * @param grphcs
	 * @param font
	 * @param color
	 * @param color1
	 * @param width
	 * @param height
	 * @param selected
	 */
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

	/**
	 * Triggered when there is only one item in completion box, so default action will be taken
	 * @param jtc
	 * @return
	 */
    public boolean instantSubstitution(JTextComponent jtc) {
        defaultAction(jtc);
        return true;
    }

	// lower number = higher priority in completion box
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
