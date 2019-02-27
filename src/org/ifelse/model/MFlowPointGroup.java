package org.ifelse.model;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class MFlowPointGroup {

    public String id;
    public String name;

    public List<MFlowPoint> points;

    public void addPoint(MFlowPoint point){
        if( points == null )
            points = new ArrayList<>();
        points.add(point);
    }


    @JSONField(serialize = false)
    public int getPointsCount(){
        return points==null?0:points.size();
    }


    @Override
    public String toString() {
        return name;
    }
}
