/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.netbeans.modules.php.nette.wizards.newpresenter;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ondřej Brejla <ondrej@brejla.cz>
 */
public class NetteTableModel extends DefaultTableModel {

    private Class[] types = new Class [] {
        String.class, Boolean.class, Boolean.class, Boolean.class
    };

    public NetteTableModel() {
        super(
            new Object [][] {
                {"default", true, true, true}
            },
            new String [] {"Action name:", "action<action>()", "render<action>()", "gen. template"}
        );
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 0 ? false : true;
    }

}
