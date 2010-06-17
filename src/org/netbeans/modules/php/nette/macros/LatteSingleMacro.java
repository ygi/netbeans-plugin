/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.macros;

import javax.swing.text.JTextComponent;

/**
 *
 * @author redhead
 */
public class LatteSingleMacro extends LatteMacro {

    public LatteSingleMacro(String macro) {
        super(macro, false);
    }

    @Override
    public void process(JTextComponent jtc, int dotOffset) {
        super.process(jtc, dotOffset);
        jtc.setCaretPosition(jtc.getCaretPosition() - 1);
    }
}
