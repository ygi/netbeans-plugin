/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.editor;

import java.awt.Color;

/**
 * Colors the macro completion item
 * @author redhead
 */
public class MacroCompletionItem extends VariableCompletionItem {

    public MacroCompletionItem(String text, int dotOffset, int caretOffset) {
        super(text, dotOffset, caretOffset);
        fieldColor = Color.decode("0xDD0000");
    }
}
