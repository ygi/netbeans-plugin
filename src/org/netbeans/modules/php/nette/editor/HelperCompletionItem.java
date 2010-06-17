/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.editor;

import java.awt.Color;

/**
 *
 * @author redhead
 */
public class HelperCompletionItem extends VariableCompletionItem {

    public HelperCompletionItem(String text, int dotOffset, int caretOffset) {
        super(text, dotOffset, caretOffset);
        fieldColor = Color.decode("0x00AA00");
    }
}
