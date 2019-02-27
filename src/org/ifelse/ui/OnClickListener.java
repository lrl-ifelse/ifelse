package org.ifelse.ui;

import javax.swing.*;
import java.awt.*;

public interface OnClickListener {

    public void onClick(JComponent component);

    default public void onClick(JComponent component, Object value){

    }
}