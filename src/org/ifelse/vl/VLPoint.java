/*
 * Copyright 1999-2019 fclassroom Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ifelse.vl;

import com.alibaba.fastjson.annotation.JSONField;
import com.intellij.openapi.project.Project;
import org.ifelse.IEAppLoader;
import org.ifelse.RP;
import org.ifelse.model.MFlowPoint;
import org.ifelse.model.MProperty;
import org.ifelse.utils.IconFactory;

import java.awt.*;

public class VLPoint extends VLItem {



    public String flow_point_id;

    @JSONField(serialize = false)
    public int image_width,image_height;


    @JSONField(serialize = false)
    private Image image;

    public void setImage(Image image){
        this.image = image;
        if( image != null ){

            image_width = image.getWidth(null);
            if( image_width > this.width )
                this.width = image_width;

            image_height = image.getHeight(null);
            if( image_height > this.height )
                this.height = image_height;

        }

    }


    @Override
    public void paint(Graphics2D g) {

        g.setColor(Color.blue);
        g.setStroke(new BasicStroke(3));
        if( image != null ) {
            g.drawImage(image, x+(width-image_width)/2, y+(height-image_height)/2, null);
        }
        else
        {
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.GRAY);
            g.drawRect(x,y,width,height);
        }
        if( isFocus() ){

            g.setColor(Color.red);
            g.drawRoundRect(x,y,width,height,4,4);

        }

        String text = getDescript();
        if( text != null ) {

            int s_w = g.getFontMetrics().stringWidth(text);
            g.drawString(text, x + (width - s_w) / 2, y - 2);

        }

    }

    public void initUI(Project project){

        if( flow_point_id != null ){

            MFlowPoint point  = IEAppLoader.getMProject(project).flowpoints.get(flow_point_id);
            if( point != null ){
                setImage( IconFactory.createImage(RP.Path.getIconPath(project,point.icon)) );
            }

        }

    }

    @Override
    public VLItem clone() {
        VLPoint point = new VLPoint();
        point.flow_point_id = flow_point_id;

        for(MProperty mp : mproperties){
            point.mproperties.add(mp.clone());
        }

        point.x = x;
        point.y = y;

        return point;
    }

}
