package org.ifelse.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MsgDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea text_msg;
    private JLabel label_title;

    public MsgDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
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
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public void setText(String msg){

        text_msg.setText(msg);

    }
    public void setTitle(String title){

        label_title.setText(title);

    }

    public static void showMessage(String title,String msg){

        int widht = 800;
        int height = 600;

        MsgDialog dialog = new MsgDialog();
        dialog.setSize(widht,height);
        dialog.setText(msg);

        dialog.setTitle(title);


        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screensize.width;
        int h = screensize.height;


        dialog.setLocation( (w-widht)/2,(h-height)/2 );
        //dialog.pack();
        dialog.setVisible(true);

    }

    public static void main(String[] args) {
        MsgDialog dialog = new MsgDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
