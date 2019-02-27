package org.ifelse.ui;

import org.ifelse.model.MEditor;
import org.ifelse.utils.Icons;
import org.ifelse.utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditorItem extends JPanel {


    public EditorItem(MEditor editor){


        setLayout(new ItemLayout());

        Image icon = null;

        switch (editor.type){

            case MEditor.TABLE:
                icon = Icons.image_doc;
                break;
            case MEditor.FLOW:
                icon = Icons.image_logo36;
                break;
            default:
                throw new RuntimeException("not the editor type:"+editor.type);

        }

        ImageView imageView = new ImageView(icon);
        imageView.setEnabled(false);
        JLabel label_name = new JLabel( editor.name );
        label_name.setForeground(Color.BLACK);
        JLabel label_descript = new JLabel( editor.descript );
        label_descript.setForeground(Color.DARK_GRAY);

        label_name.setEnabled(false);
        label_descript.setEnabled(false);

        add(imageView);
        add(label_name);
        add(label_descript);


    }


    public static class ItemLayout implements   LayoutManager{


        @Override
        public void addLayoutComponent(String name, Component comp) {

        }

        @Override
        public void removeLayoutComponent(Component comp) {

        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {

            return new Dimension(100,100);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(100,100);
        }

        @Override
        public void layoutContainer(Container parent) {

            int off = 12;
            int left = off;

            int icon_w = parent.getHeight() -  off*2;


            parent.getComponent(0).setBounds(off,off,icon_w,icon_w);

            left += icon_w + off;

            parent.getComponent(1).setBounds(left,10,parent.getWidth()-left,(parent.getHeight()-20)/2);
            parent.getComponent(2).setBounds(left,parent.getHeight()/2,parent.getWidth()-left,(parent.getHeight()-20)/2);


        }
    }

}


