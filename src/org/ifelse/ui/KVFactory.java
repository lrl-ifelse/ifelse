package org.ifelse.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.codehaus.groovy.util.ListHashMap;
import org.ifelse.RP;
import org.ifelse.model.MFieldType;
import org.ifelse.model.MProject;
import org.ifelse.model.MProperty;
import org.ifelse.utils.FileUtil;
import org.ifelse.utils.ListMap;

import java.util.ArrayList;
import java.util.List;

public class KVFactory {


    private final MProject project;

    public KVFactory(MProject project){

        this.project = project;
    }

    public ListMap<String,KV> getItems(MProperty property) {


        for(MFieldType mFieldType : project.fieldTypes)
        {

            if(  mFieldType.name.equals(property.args) ){

                if( mFieldType.value.indexOf(',') > -1 ){

                    ListMap<String,KV> kvlist = new ListMap<>();
                    String[] kvs = mFieldType.value.split(",");

                    for(String kv:kvs){
                        kvlist.put(kv,new KV(kv));
                    }
                    return kvlist;

                }
                else if( mFieldType.value.indexOf('|') > -1 ){


                    String[] keys = mFieldType.value.split("\\|");
                    ListMap<String,KV> kvlist = new ListMap<>();
                    String path = project.path+mFieldType.path;

                    String txt = FileUtil.read(path);
                    JSONArray array = JSON.parseArray(txt);

                    for(Object obj : array){

                        JSONObject jobj = (JSONObject) obj;
                        String k = jobj.getString(keys[0]);
                        kvlist.put(k,new KV(k,jobj.getString(keys[1])));
                    }

                    return kvlist;

                }
            }


        }

        return null;
    }


}
