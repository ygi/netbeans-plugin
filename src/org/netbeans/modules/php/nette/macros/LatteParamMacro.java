/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.macros;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

/**
 * Represents parametrized macro for completion
 * (adds whitespace character after opening macro name and sets caret position there for instant writing of params)
 * @author Radek Ježdík
 */
public class LatteParamMacro extends LatteMacro {

    public LatteParamMacro(String macro, boolean isPair) {
        this(macro, isPair, macro);
    }
    
    public LatteParamMacro(String macro, boolean isPair, String endMacro) {
        super(macro, isPair, endMacro);
    }

	
    @Override
    public void process(JTextComponent jtc, int dotOffset) {
        StyledDocument doc = (StyledDocument) jtc.getDocument();
        try {
            doc.insertString(dotOffset, "{"+macro+" }", null);			// adds space for macro params
            if(isPair) {
				// FIXME get rid of this
				// used when text selected (useless since completion appears only after { char)
                doc.insertString(jtc.getSelectionEnd(), "{/"+endMacro+"}", null);
            } else if(jtc.getSelectedText() != null) {
                doc.remove(jtc.getSelectionStart(), jtc.getSelectionEnd()-jtc.getSelectionStart());
            }
            jtc.setCaretPosition(dotOffset + macro.length() + 2);		// moves caret to param position for user
        }
        catch(Exception ex) {
            Logger.getLogger(LatteCommentMacro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getText() {				// adds space for macro params
        String text = '{'+macro+" }";
        if(isPair)
            text += "{/"+endMacro+'}';
        return text;
    }

}
