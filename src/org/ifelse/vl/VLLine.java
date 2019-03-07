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
import org.ifelse.model.MProperty;
import org.ifelse.model.Rect;

import java.awt.*;

public class VLLine extends VLItem {

    public String id_from;
    public String id_to;
    public int direct;//0 default

    @JSONField(serialize = false)
    public VLItem point_from;

    @JSONField(serialize = false)
    public VLItem point_to;

    @JSONField(serialize = false)
    public Rect[] lines = new Rect[]{

            new Rect(),
            new Rect()
    };

    @JSONField(serialize = false)
    public Point move_cursor;




    public VLLine(){

        MProperty p_descript = new MProperty();
        p_descript.key = "descript";
        p_descript.name = "descript";

        MProperty p_isint = new MProperty();
        p_isint.key = "isint";
        p_isint.name = "isint";
        p_isint.args = "boolean";

        MProperty p_isdirect = new MProperty();
        p_isdirect.key = "direction";
        p_isdirect.name = "direction";
        p_isdirect.args = "Direction";


        mproperties.add(p_descript);
        mproperties.add(p_isint);
        mproperties.add(p_isdirect);


    }

    @Override
    public void paint(Graphics2D g) {

        g.setColor(Color.blue);

        if (point_from != null) {

            if (point_to == null && move_cursor != null)
                drawLine(g, point_from.x, point_from.y, move_cursor.x, move_cursor.y);
            else if (point_to != null)
                drawLine(g, point_from.x, point_from.y, point_to.x, point_to.y);
        }



    }

    @Override
    public void setXY(int x, int y) {

    }

    public int getTxtPos(){

        MProperty mProperty = getMProperty("direction");

        if( mProperty == null || "horizontal".equals(mProperty.value) )
            return 1;
        else
            return 2;

    }


    void drawLine(Graphics2D  painter,int x0,int y0,int x1,int y1)
    {

        int pos = getTxtPos();

        float line_wh = 3;
        painter.setStroke(new BasicStroke(line_wh));

        //int text_w =  (int)fontMetrics.width(title);
        int text_h = 12;

        if( isFocus() ){
            painter.setColor( Color.RED );
        }
        else{
            painter.setColor( Color.BLUE );
            if( point_from != null && point_to != null ){
               if( point_from.is_run_point && point_to.is_run_point &&  point_to.id.equals(point_from.next_run_point_id)  )
                    painter.setColor(Color.RED);

            }
        }


        String text = getDescript();
        if( text == null ) text = "";
        int s_w = painter.getFontMetrics().stringWidth(text);


        int ele_w = point_from.width;
        int ele_h = point_from.height;


        if( x0+ele_w/2 > x1 && x0+ele_w/2<x1+ele_w )//竖直方向
        {

            if( y1>y0)
            {
                int px0 = x0+ele_w/2;
                int py0 = y0+ele_h;

                int px1 = px0;
                int py1 = y1;
                painter.drawLine(px0,py0,px1,py1);
                drawArrow(painter,px0,py0,px1,py1);


                if( point_to != null )
                    lines[0].setPoint(px0,py0,px1,py1);


                painter.drawString(text,px0-s_w/2,(py1+py0)/2);


            }
            else
            {


                int px0 = x0+ele_w/2;
                int py0 = y0;

                int px1 = px0;
                int py1 = y1+ele_h;
                painter.drawLine(px0,py0,px1,py1);
                drawArrow(painter,px0,py0,px1,py1);


                if( point_to != null )
                    lines[0].setPoint(px0,py0,px1,py1);


                painter.drawString(text,px0-s_w/2,(py1+py0)/2);

            }



        }
        else if( x0+ele_w/2 > x1+ele_w  )//left 从上到左下
        {
            if( y0+ele_h/2>y1 && y0+ele_h/2<y1+ele_h)//
            {
                int px0 = x0;
                int py0 = y0+ele_h/2;

                int px1 = x1+ele_w;
                int py1 = py0;
                painter.drawLine(px0,py0,px1,py1);
                drawArrow(painter,px0,py0,px1,py1);


                if( point_to != null )
                    lines[0].setPoint(px0,py0,px1,py1);


                painter.drawString(text,(px0+px1-s_w)/2,py1-5);


            }

            else if( y0+ele_h/2>y1+ele_h)// 从上到左上
            {
                int px0 = x0+ele_w/2;
                int py0 = y0;

                int px1 = px0;
                int py1 = y1+ele_h/2;

                int px2 = x1+ele_w;
                int py2 = py1;

                painter.drawLine(px0,py0,px1,py1);
                painter.drawLine(px1,py1,px2,py2);

                drawArrow(painter,px1,py1,px2,py2);

                if( pos == 2 )
                    painter.drawString(text,px0-s_w/2,(py1+py0)/2);
                else
                    painter.drawString(text,(px1+px2-s_w)/2,py1 - 5);


                if( point_to != null ) {
                    lines[0].setPoint(px0, py0, px1, py1);
                    lines[1].setPoint(px1, py1, px2, py2);
                }


            }


            else{//从下 到左下

                int px0 = x0 + ele_w/2;
                int py0 = y0+ele_h;

                int px1 = px0;
                int py1 = y1+ele_h/2;

                int px2 = x1+ele_w;
                int py2 = py1;

                painter.drawLine(px0,py0,px1,py1);
                painter.drawLine(px1,py1,px2,py2);

                drawArrow(painter,px1,py1,px2,py2);


                if( point_to != null ) {
                    lines[0].setPoint(px0, py0, px1, py1);
                    lines[1].setPoint(px1, py1, px2, py2);
                }


                if( pos == 2 )
                    painter.drawString(text,px0-s_w/2,(py1+py0)/2);
                else
                    painter.drawString(text,(px0+px2-s_w)/2,py1 - 5);


            }

        }
        else{//right


            if( y0+ele_h/2>y1 && y0+ele_h/2<y1+ele_h)//从左到右 直线
            {
                int px0 = x0+ele_w;
                int py0 = y0+ele_h/2;

                int px1 = x1;
                int py1 = py0;
                painter.drawLine(px0,py0,px1,py1);
                drawArrow(painter,px0,py0,px1,py1);


                if( point_to != null )
                    lines[0].setPoint(px0,py0,px1,py1);


                painter.drawString(text,(px0+px1-s_w)/2,py0-5);

            }

            else if( y0+ele_h/2>y1+ele_h)  //right north   从下到上 到右
            {
                int px0 = x0+ele_w/2;
                int py0 = y0;

                int px1 = px0;//x1+ele_w/2;
                int py1 = y1+ele_h/2;

                int px2 =  x1;
                int py2 =  py1;

                painter.drawLine(px0,py0,px1,py1);
                painter.drawLine(px1,py1,px2,py2);

                drawArrow(painter,px1,py1,px2,py2);

                if( pos == 2 )
                    painter.drawString(text,px0-s_w/2,(py1+py0)/2);
                else
                    painter.drawString(text,(px0+x1-s_w)/2,py1 - 5);


                if( point_to != null ) {
                    lines[0].setPoint(px0, py0, px1, py1);
                    lines[1].setPoint(px1, py1, px2, py2);
                }

            } else{//从 上到下到右

                int px0 = x0+ele_w/2;
                int py0 = y0+ele_h;

                int px1 = px0;
                int py1 = y1+ele_h/2;

                int px2 = x1;
                int py2 = py1;

                painter.drawLine(px0,py0,px1,py1);
                painter.drawLine(px1,py1,px2,py2);

                drawArrow(painter,px1,py1,px2,py2);



                if( pos == 2 )
                    painter.drawString(text,px0-s_w/2,(py1+py0)/2);
                else
                    painter.drawString(text,(px1+px2-s_w)/2,py1 - 5);



                if( point_to != null ) {
                    lines[0].setPoint(px0, py0, px1, py1);
                    lines[1].setPoint(px1, py1, px2, py2);
                }


            }




        }










    }

    private String getText() {
        return "";
    }

    private void drawArrow(Graphics painter, int x1, int y1, int x2, int y2) {


        int a_wh = 12;

        if( x1 == x2 ){

            int off = y1 < y2 ? a_wh : -a_wh;
            Polygon polygon = new Polygon();
            polygon.addPoint(x2,y2+(y1<y2?3:-3));
            polygon.addPoint(x2-a_wh/2,y2-off);
            polygon.addPoint(x2+a_wh/2,y2-off);
            painter.fillPolygon(polygon);


        }
        else {

            int off = x1 < x2 ? a_wh : -a_wh;
            Polygon polygon = new Polygon();
            polygon.addPoint(x2+(x1<x2?3:-3),y2);
            polygon.addPoint(x2-off,y2+a_wh/2);
            polygon.addPoint(x2-off,y2-a_wh/2);
            painter.fillPolygon(polygon);

        }




    }

    @Override
    public boolean isLine() {
        return true;
    }

    @Override
    public VLItem clone() {
        return null;
    }

    @Override
    public boolean isPointIn(Point p) {
        return lines[0].isIn(p.x,p.y)||lines[1].isIn(p.x,p.y);
    }

    public void newDefProperty() {




    }
}
