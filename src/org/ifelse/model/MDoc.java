package org.ifelse.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.ifelse.vl.VLItem;
import org.ifelse.vl.VLLine;
import org.ifelse.vl.VLPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MDoc {

    public String title;
    public String flowid;

    public List<VLItem> items;


    @JSONField(serialize = false)
    public HashMap<String,VLItem> getLines(){

        HashMap<String,VLItem> maps = new HashMap();
        if( items != null )
        for(VLItem item : items){
            if( item.isLine() )
                maps.put(item.id,item);

        }
        return maps;

    }

    @JSONField(serialize = false)
    public HashMap<String,VLItem> getPoints(){

        HashMap<String,VLItem> maps = new HashMap();
        if( items != null )
            for(VLItem item : items){

                if( !item.isLine() )
                    maps.put(item.id,item);

            }
        return maps;

    }

    public void addItem(VLItem item){


        if( items == null )
            items = new ArrayList<>();
        items.add(item);

    }

    public void clear() {

        if( items != null )
            items.clear();
        items =  null;

    }
}
