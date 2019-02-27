package org.ifelse.ui;


import org.ifelse.utils.Log;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;


public  class TextEditor extends AbstractCellEditor implements TableCellEditor {

    //javax.swing.text.FieldView

    JTextField field;

    int row,column;

    Object value;

    IValue iValue;

    public TextEditor(IValue iValue){


        this.iValue = iValue;

    }


    @Override
    public Object getCellEditorValue() {
        return field.getText();
    }

    @Override
    protected void fireEditingStopped() {

        Log.i("fireEditingStopped");

        if( iValue != null && field != null )
            iValue.onTableCellValueChanged(value,field.getText(),row,column);

        try {
            super.fireEditingStopped();
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public Component getTableCellEditorComponent(JTable table,final Object value, boolean isSelected,final int row,final int column) {

        Log.i("getTableCellEditorComponent");

        field = new JTextField();
        field.setText(iValue.getTableCellValueAt(value,row,column));
        this.row = row;
        this.column = column;
        this.value = value;


        field.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseExited(MouseEvent e) {


                Log.i("mouseExited");
                fireEditingStopped();
            }
        });
        return field;
    }






}
