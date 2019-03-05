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
package org.ifelse.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.ifelse.vl.VLItem;
import org.ifelse.vl.VLLine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateUtil {



    public static List<VLItem> parseFlow(String path) throws ClassNotFoundException {


        String txt = FileUtil.read(path);



        Object obj_ = JSON.parse(txt);

        JSONArray jsonArray = null;

        if( obj_ instanceof JSONObject )
        {
            JSONObject jobj = (JSONObject) obj_;

            if( jobj.containsKey("items") )
            {
                jsonArray = jobj.getJSONArray("items");
            }

        }else if( obj_ instanceof  JSONArray ) {

            jsonArray = (JSONArray) obj_;
        }




        ArrayList<VLItem> list = new ArrayList();

        for(Object obj : jsonArray){

            JSONObject jobject = (JSONObject) obj;
            String classname = jobject.getString("classname");
            VLItem item = (VLItem) jobject.toJavaObject( Class.forName(classname) );
            list.add(item);

        }


        return list;
    }

    public static List<Map> parseTable(String path){


        String txt = FileUtil.read(path);

        JSONArray array = JSON.parseArray(txt);

        ArrayList<Map> list = new ArrayList();

        for(Object obj : array){

            JSONObject jobject = (JSONObject) obj;

            Map map = jobject.getInnerMap();

            list.add(map);

        }


        return list;


    }

    public static <T> List parse(Class<T> classt, String path){

        String txt = FileUtil.read(path);
        return JSON.parseArray(txt,classt);

    }



}
