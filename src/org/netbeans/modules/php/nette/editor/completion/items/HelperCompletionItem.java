/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.editor.completion.items;

import java.awt.Color;

/**
 * Colors the helper completion item
 * @author Radek Ježdík
 */
public class HelperCompletionItem extends BaseCompletionItem {

    public HelperCompletionItem(String text, int dotOffset, int caretOffset) {
        super(text, dotOffset, caretOffset);
        fieldColor = Color.decode("0x00AA00");
    }
}
