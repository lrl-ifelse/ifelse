package org.ifelse.ui;

import com.intellij.openapi.project.Project;
import org.ifelse.model.MVar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;

public class DialogVars extends JDialog {
    private JPanel contentPane;

    private JButton buttonCancel;
    private JTable table;

    public DialogVars() {
        setContentPane(contentPane);
        setModal(true);


        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);





        table.setRowHeight(30);


        table.setRowSelectionAllowed(true);


        table.setDefaultRenderer(Object.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JLabel field = new JLabel();
                MVar var = (MVar) value;

                switch (column){

                    case 0 : field.setText(var.key);break;
                    case 1 : field.setText(var.flow);break;
                    case 2 : field.setText(var.descript);break;
                    case 3 : field.setText(var.name);break;


                }


                return field;



            }
        });







    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        DialogVars dialog = new DialogVars();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public static void showDialog(java.util.List<MVar> list){

        int widht = 800;
        int height = 500;

        DialogVars dialog = new DialogVars();
        dialog.setSize(widht,height);
        dialog.setData( list );


        dialog.setTitle("ifelse 静态变量");


        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screensize.width;
        int h = screensize.height;


        dialog.setLocation( (w-widht)/2,(h-height)/2 );
        //dialog.pack();
        dialog.setVisible(true);




    }

    public  void setData(final java.util.List<MVar> vars) {


        DefaultTableModel propertyModel = new DefaultTableModel() {

            @Override
            public int getRowCount() {
                return vars ==null ? 0 : vars.size();
            }

            @Override
            public int getColumnCount() {
                return 4;
            }


            @Override
            public String getColumnName(int column) {

                switch (column){
                    case 0 : return "key";
                    case 1 : return "flow";
                    case 2 : return "point";
                    case 3 : return "title";
                }
                return "";

            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {

                return  vars.get(rowIndex);
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {

                return  Object.class;
            }


        };


        table.setModel(propertyModel);



    }
}
