package org.ifelse.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import org.codehaus.groovy.util.ListHashMap;
import org.ifelse.model.MProject;
import org.ifelse.model.MProperty;
import org.ifelse.utils.ListMap;
import org.ifelse.utils.Log;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class PropertyCellEditor extends AbstractCellEditor implements TableCellEditor {


    Component component;
    KVFactory factory;
    MProperty property;

    ListMap<String,KV> kvList;

    OnValueChangedListener listener;
    int row,column;

    public PropertyCellEditor(MProject project,@NotNull OnValueChangedListener listener){

        factory = new KVFactory(project);
        this.listener = listener;

    }

    public abstract MProperty getProperty(int row);

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
        property.value = (String)getCellEditorValue();

        Log.i("PropertyCellEditor fireEditingStopped");

        listener.onValueChanged(property,row,column);

    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {


        this.property = getProperty(row);
        this.kvList = factory.getItems( property );
        this.row = row;
        this.column = column;


        if( isNULL(property.args) ){

            JTextField field = new JTextField();
            field.setText( property.value );

            field.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {

                    if(  e.getKeyCode() == KeyEvent.VK_ENTER  ){
                        fireEditingStopped();
                    }


                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });

            component = field;

        }
        else{

            JComboBox field = new JComboBox();


            int selectindex = -1;
            for(int i=0;i<kvList.size();i++) {


                field.addItem(kvList.getByIndex(i).value);
                if(kvList.getByIndex(i).key.equals(property.value)){

                    selectindex = i;

                }

            }

            field.setSelectedIndex(selectindex);


            field.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {


                    if (e.getStateChange() == ItemEvent.SELECTED)
                    {

                        fireEditingStopped();

                    }

                }
            });

            component = field;



        }
        return component;
    }

    @Override
    public Object getCellEditorValue() {

        if( component instanceof JTextField )
        {
            JTextField field = (JTextField) component;
            return field.getText();

        }
        else if( component instanceof JComboBox)
        {
            JComboBox comboBox = (JComboBox) component;

            if( comboBox.getSelectedIndex() == -1 )
                return "";
            return kvList.getByIndex(comboBox.getSelectedIndex()).key;


        }
        return null;

    }

    private boolean isNULL(String v){

        return v==null||v.length()==0;

    }

    public static interface OnValueChangedListener{

        void onValueChanged(MProperty property,int row,int index );

    }

}
