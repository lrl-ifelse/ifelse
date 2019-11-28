package org.ifelse.model;

import java.awt.*;

public class Rect {


    public int y;
    public int x1;
    public int y1;
    public int x;
    public boolean is_focus;
    public Point move_point;


    public Rect() {


    }

    public Rect(int x, int y, int x1, int y1) {

        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;

    }

    public boolean isIn(int x0, int y0) {

        boolean is = (x <= x0 && x0 <= x1 && y <= y0 && y0 <= y1);
        return is;

    }

    public void setPoint(int x, int y, int x1, int y1) {

        if (x > x1) {
            int t = x;
            x = x1;
            x1 = t;
        }
        if (y > y1) {
            int t = y;
            y = y1;
            y1 = t;
        }


        if (x == x1) {

            x -= 3;
            x1 += 3;
        } else if (y == y1) {

            y -= 3;
            y1 += 3;
        }

        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;

    }

    public int width(){

        return x1-x;
    }
    public int height(){

        return y1-y;
    }

    public void move(Point point) {




        x -= move_point.x - point.x;
        y -= move_point.y - point.y;

        x1 -= move_point.x - point.x;
        y1 -= move_point.y - point.y;

        move_point = point;

    }

    public void setMovePoint(Point point) {

        move_point = point;
    }
}
