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
 * Base Macro class
 * Handles macro name, if it is pair macro, end and macro name (ifCurrent /if)
 * @author redhead
 */
public class LatteMacro {

    protected String macro;
    protected boolean isPair;
    protected String endMacro;
    
    public LatteMacro(String macro) {
        this(macro, false, macro);
    }

    public LatteMacro(String macro, boolean isPair) {
        this(macro, isPair, macro);
    }

    public LatteMacro(String macro, boolean isPair, String endMacro) {
        this.macro = macro;
        this.isPair = isPair;
        this.endMacro = endMacro;
    }

    public void process(JTextComponent jtc, int dotOffset) {
        StyledDocument doc = (StyledDocument) jtc.getDocument();
        try {
            doc.insertString(dotOffset, "{"+macro+"}", null);
            if(isPair) {
                doc.insertString(jtc.getSelectionEnd(), "{/"+endMacro+"}", null);
            } else {
                doc.remove(jtc.getSelectionStart(), jtc.getSelectionEnd()-jtc.getSelectionStart());
            }
            jtc.setCaretPosition(dotOffset+macro.length()+2);
        }
        catch(Exception ex) {
            Logger.getLogger(LatteCommentMacro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getMacro() {
        return '{'+macro+'}';
    }

    public String getText() {
        String text = getMacro();
        if(isPair)
            text += getEndMacro();
        return text;
    }

    public String getMacroName() {
        return macro;
    }

    public boolean isPair() {
        return isPair;
    }

    public String getEndMacro() {
        return "{/"+endMacro+"}";
    }

}
