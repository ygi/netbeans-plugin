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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.modules.php.nette.macros.LatteMacro;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;

/**
 *
 * @author Radek Ježdík
 */
public class LatteCompletionItem implements CompletionItem {

    private LatteMacro macro;
    private static Color fieldColor = Color.decode("0x0000B2");
    private int dotOffset;
    private int caretOffset;

    public LatteCompletionItem(LatteMacro macro, int dotOffset, int caretOffset) {
        this.macro = macro;
        this.dotOffset = dotOffset;
        this.caretOffset = caretOffset;
    }

    public void defaultAction(JTextComponent jtc) {
        try {
            StyledDocument doc = (StyledDocument) jtc.getDocument();
            doc.remove(dotOffset, caretOffset-dotOffset);
            macro.process(jtc, dotOffset);
            Completion.get().hideAll();
        } catch (BadLocationException ex) {
            Logger.getLogger(BaseCompletionItem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void processKeyEvent(KeyEvent ke) {

    }

    public int getPreferredWidth(Graphics grphcs, Font font) {
        return CompletionUtilities.getPreferredWidth(macro.getText(), null, grphcs, font);
    }

    public void render(Graphics grphcs, Font font, Color color, Color color1, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(null, macro.getText(), null, grphcs, font,
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
        return macro.getText();
    }

    public CharSequence getInsertPrefix() {
        return macro.getText();
    }

}
