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
package org.ifelse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.ifelse.model.*;
import org.ifelse.model.MFlowPointGroup;
import org.ifelse.utils.FileUtil;
import org.ifelse.utils.Icons;
import org.ifelse.utils.Log;
import org.ifelse.vl.VLItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RP {


    public static class Path{

        public static final String ie = "/iedata";
        public static final String iedata = ie +"/project.json";
        public static final String flowpoints = ie+"/points.json";

        public static final String script = ie+"/script";

        public static final String menus = script+"/menu.json";


        public static String getPath(Project project,MEditor editor) {
            return  String.format("%s%s/%s.ie",project.getBasePath(),ie,editor.name);
        }
        public static String getFlowPointsPath(Project project){

            return  project.getBasePath()+flowpoints;

        }

        public static String getIconPath(Project project,String icon){

            return project.getBasePath()+icon;

        }

        public static String getIEPath(Project project) {
            return project.getBasePath()+ie;
        }
        public static String getIeData(Project project) {
            return project.getBasePath()+iedata;
        }


        public static String getIEMenu(Project project) {
            return project.getBasePath() + menus;
        }
    }



    public static class Data{


        public static HashMap<String,Object> datas = new HashMap<>();



        public List<MFlowPointGroup> flowpoint_groups;


        public static List<MFlowPointGroup> newDefaultFlowPoints(Project project){

            String path = Path.getFlowPointsPath(project);

            List<MFlowPointGroup> groups = new ArrayList<>();
            MFlowPointGroup group = new MFlowPointGroup();

            group.id = "1001";
            group.name = "Base";


            MFlowPoint point_start = new MFlowPoint();
            point_start.id = "100101";
            point_start.name = "Start";
            point_start.icon = "/iedata/icons/start.png";
            point_start.classz = "org.ifelse.points.Start";

            point_start.mproperties = new ArrayList<>();

            MProperty property = new MProperty();
            property.key = "event";
            property.name ="Event";
            property.args = "Event";

            MProperty property_value = new MProperty();
            property_value.key = "value";
            property_value.name ="Value";


            point_start.mproperties.add(property);
            point_start.mproperties.add(property_value);

            group.addPoint(point_start);



            //form point

            MFlowPointGroup group_manager = new MFlowPointGroup();

            group_manager.id = "9001";
            group_manager.name = "Manager Item";



            MFlowPoint point_form = new MFlowPoint();
            point_form.id = "900101";
            point_form.name = "Form";
            point_form.icon = Icons.path_form;

            point_form.mproperties = new ArrayList<>();



            MProperty p_event = new MProperty();
            p_event.key = "event";
            p_event.name = "event";
            p_event.args = "Event";


            MProperty p_class = new MProperty();
            p_class.key = "class";
            p_class.name = "class";
            p_class.value = "Form";

            MProperty p_name = new MProperty();
            p_name.key = "name";
            p_name.name = "name";

            MProperty p_plate = new MProperty();
            p_plate.key = "template";
            p_plate.name = "template";

            MProperty p_descript = new MProperty();
            p_descript.key = "descript";
            p_descript.name = "descript";


            point_form.mproperties.add(p_event);
            point_form.mproperties.add(p_class);
            point_form.mproperties.add(p_name);
            point_form.mproperties.add(p_plate);
            point_form.mproperties.add(p_descript);

            group_manager.addPoint(point_form);

            groups.add(group);
            groups.add(group_manager);


            String txt = JSON.toJSONString(groups,SerializerFeature.PrettyFormat);

            try {
                FileUtil.save(txt,path);
            } catch (Exception e) {
                e.printStackTrace();
            }

            VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://" + path);

            return groups;
        }



        public static MDoc loadDoc( String path) throws ClassNotFoundException {




                Log.i("RP.loadDoc :%s",path);

                String txt = FileUtil.read(path);

                MDoc mDoc = new MDoc();

                JSONObject jsonobj = (JSONObject) JSON.parse(txt);

                if( jsonobj.containsKey("title") )
                     mDoc.title = jsonobj.getString("title");
                if( jsonobj.containsKey("flowid") )
                    mDoc.flowid = jsonobj.getString("flowid");

                JSONArray array = jsonobj.getJSONArray("items");

                mDoc.items = new ArrayList<>();

                for (int i = 0; i < array.size(); i++) {

                    JSONObject jsonObject = (JSONObject) array.get(i);
                    String classname = jsonObject.getString("classname");
                    mDoc.items.add( (VLItem) jsonObject.toJavaObject( Class.forName(classname) ) );

                }

            return mDoc;
        }
    }
}
