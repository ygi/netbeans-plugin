/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.editor.completion.items;

import java.awt.Color;

/**
 * Colors the control completion item
 * @author Radek Ježdík
 */
public class ControlCompletionItem extends BaseCompletionItem {

    public ControlCompletionItem(String text, int dotOffset, int caretOffset) {
        super(text, dotOffset, caretOffset);
        fieldColor = Color.decode("0x000000");
    }

    @Override
    public int getSortPriority() {
        return -2000;
    }

}
