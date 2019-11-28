package org.ifelse.ui;

import org.ifelse.model.MLineLabel;
import org.ifelse.utils.Icons;

import javax.swing.*;
import java.awt.*;

public class LineEditorItem extends JPanel {


    public LineEditorItem(MLineLabel label,int row){


        setLayout(new BorderLayout(5,5));


        JLabel label_name = new JLabel( label.descript );
        label_name.setForeground(Color.BLACK);
        label_name.setEnabled(false);


        JLabel icon = new JLabel();
        icon.setIcon((Icons.icon_logo));

        add(icon,BorderLayout.WEST);
        add(label_name,BorderLayout.CENTER);

        if( row %2 == 1)
        setBackground(new Color(0, 0, 255,20));




    }




}


