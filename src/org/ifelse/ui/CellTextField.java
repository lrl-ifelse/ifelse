package org.ifelse.ui;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class CellTextField extends JTextField {


    @Override
    protected void processMouseEvent(MouseEvent e) {

        int id = e.getID();
        switch(id) {

            case MouseEvent.MOUSE_EXITED:

                break;
        }


    }
}
