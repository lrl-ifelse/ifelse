package org.ifelse.ui;

public interface IValue {

    String getTableCellValueAt(Object value, int row, int column);

    void onTableCellValueChanged(Object value, String cvalue, int row, int column);

}
