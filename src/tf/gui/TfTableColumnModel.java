/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tf.gui;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author lawry
 */
public class TfTableColumnModel extends DefaultTableColumnModel {
    private static final int MIN_WIDTH = 10;
    private static final int FRAME_COLUMN_WIDTH = 20;
    private static final int MAX_WIDTH = 100;

    public TfTableColumnModel() {
        setColumnSelectionAllowed(false);
        //getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }

    @Override
    public void addColumn(TableColumn aColumn) {
        int columnIndex = aColumn.getModelIndex();

        // Update width of column.
        aColumn.setMinWidth(MIN_WIDTH);
        aColumn.setMaxWidth(MAX_WIDTH);
        aColumn.setPreferredWidth(FRAME_COLUMN_WIDTH);

        super.addColumn(aColumn);
    }

}
