package org.ifelse.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MFlowPoint implements Serializable {


    public String id;
    public String name;
    public String icon;
    public String classz;
    public String doubleclick;

    public List<MProperty> mproperties;


    @Override
    public String toString() {
        return name;
    }


    public List<MProperty> copyProperties(){

        List<MProperty> result = new ArrayList<>();

        for(MProperty mp : mproperties){
            result.add(mp.clone());
        }
        return result;

    }
}
