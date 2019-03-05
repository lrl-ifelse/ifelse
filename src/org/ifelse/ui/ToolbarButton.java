package org.ifelse.ui;

import com.intellij.openapi.ui.popup.IconButton;
import org.apache.batik.util.gui.resource.JToolbarButton;

import javax.swing.*;
import javax.swing.text.IconView;
import javax.swing.text.html.ImageView;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToolbarButton extends JButton {

    public ToolbarButton(Icon icon) {
        super(icon);
        this.initialize();
        setWidth(40);
    }
    public ToolbarButton(Icon icon,int width) {
        super(icon);
        this.initialize();
        setWidth(width);
    }

    protected void initialize() {


        this.setOpaque(false);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setBorderPainted(false);

    }


    public void setWidth(int width){

        setMaximumSize(new Dimension(width,36));
        setMinimumSize(new Dimension(width,36));

    }


    @Override
    protected void processMouseEvent(MouseEvent e) {

        int id = e.getID();
        switch(id) {
            case MouseEvent.MOUSE_PRESSED:
                requestFocus();
                this.setBorderPainted(true);
                break;
            case MouseEvent.MOUSE_RELEASED:
                this.setBorderPainted(false);
                if( listener != null )
                    listener.onClick(this);
                break;
        }


    }


    OnClickListener listener;

    public void setOnClickListener(OnClickListener listener) {
       this.listener = listener;


    }
}
