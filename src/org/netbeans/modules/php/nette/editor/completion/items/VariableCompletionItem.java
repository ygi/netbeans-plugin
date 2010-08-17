/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.editor.completion.items;

import java.awt.Color;

/**
 * Representers completion item for variable (is super class for others) //FIXME: ?
 * @author Radek Ježdík
 */
public class VariableCompletionItem extends BaseCompletionItem {

    public VariableCompletionItem(String text, int dotOffset, int caretOffset) {
        super(text, dotOffset, caretOffset);
        fieldColor = Color.decode("0xEE7700");
    }

	@Override
    public int getSortPriority() {
        return 0;
    }

}
