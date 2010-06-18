/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.editor;

import java.awt.Color;

/**
 * Colors the control completion item
 * @author redhead
 */
public class ControlCompletionItem extends VariableCompletionItem {

    public ControlCompletionItem(String text, int dotOffset, int caretOffset) {
        super(text, dotOffset, caretOffset);
        fieldColor = Color.decode("0x000000");
    }

}
