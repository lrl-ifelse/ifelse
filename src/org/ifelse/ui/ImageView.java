package org.ifelse.ui;

import javax.swing.*;
import java.awt.*;

public class ImageView extends JComponent {


    Image image;
    public void setIcon(Image icon){

        this.image = icon;
    }

    public ImageView(Image icon){
        this.image = icon;
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(image != null)
         g.drawImage(image,(getWidth()-image.getWidth(null))/2,(getHeight()-image.getHeight(null))/2,null);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
