/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.macros;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

/**
 * Representing comment macro for completion
 * @author Radek Ježdík
 */
public class LatteCommentMacro extends LatteMacro {

    public LatteCommentMacro() {
        super("**", false);
    }

    @Override
    public void process(JTextComponent jtc, int dotOffset) {
        try {
            String sel = jtc.getSelectedText();
            super.process(jtc, dotOffset);
            jtc.setCaretPosition(jtc.getCaretPosition() - 2);
            if (sel != null) {
                ((StyledDocument) jtc.getDocument()).insertString(dotOffset + 2, sel, null);	// sets caret between **
            }
        } catch (BadLocationException ex) {
            Logger.getLogger(LatteCommentMacro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
}
