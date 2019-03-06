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
package org.ifelse.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.compiler.ant.taskdefs.Unzip;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.ifelse.IEAppLoader;
import org.ifelse.RP;
import org.ifelse.utils.FileUtil;
import org.ifelse.utils.Icons;
import org.ifelse.utils.UnZip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MProject {

    @JSONField(serialize = false)
    public String key;

    @JSONField(serialize = false)
    public String path;

    public List<MEditor> editors;
    public List<MFieldType> fieldTypes;

    @JSONField(serialize = false)
    public List<MFlowPointGroup> flowpoint_groups;

    @JSONField(serialize = false)
    public HashMap<String,MFlowPoint> flowpoints;


    @JSONField(serialize = false)
    public boolean isIEProject;



    public volatile int sequence;

    @JSONField(serialize = false)
    public int getSequence(Project project){

        sequence++;

        IEAppLoader.save(project);

        return sequence;
    }

    @JSONField(serialize = false)
    public String getSequenceStr(Project project){

        return String.valueOf(getSequence(project));
    }

    public void create(Project project){


        editors = new ArrayList<>();
        newDefEvents();
        newDefFormsEditor();
        newDefFieldTypes();

        newDefFlowPoints(project);

        try {
            copyRes(project);
        } catch (IOException e) {
            e.printStackTrace();
        }

        save(project);

        init(project);

    }

    private void copyRes(Project project) throws IOException {

        //getClass().getClassLoader().getResourceAsStream()

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/res.zip");
        String dir = RP.Path.getIEPath(project);
        UnZip.unzip(inputStream,dir);
        inputStream.close();
        FileUtil.refresh(dir);


    }

    public void init(Project project){

        key = project.getName();

        String path = project.getBasePath();

        this.path = path;

        isIEProject =  new File(path + RP.Path.ie).exists();

        if( !isIEProject )
            return;


        if( editors == null ){

           editors = new ArrayList<>();
           newDefEvents();
           newDefFormsEditor();
           newDefFieldTypes();
           newDefFlowPoints(project);

        }
        else {
            loadFlowPoints(project);

        }
        initFlowPoints();


    }

    private void initFlowPoints(){

        if( flowpoints != null)
            flowpoints.clear();
        flowpoints = new HashMap<>();

        for(MFlowPointGroup group:flowpoint_groups){

            for(MFlowPoint point : group.points){


                flowpoints.put(point.id,point);

            }

        }




    }

    public  List<MFlowPointGroup> loadFlowPoints(Project project) {

        String path = RP.Path.getFlowPointsPath(project);
        String txt = FileUtil.read(path);
        flowpoint_groups = JSON.parseArray(txt,MFlowPointGroup.class);
        initFlowPoints();

        return flowpoint_groups;

    }

    private void newDefFlowPoints(Project project) {

        String path = RP.Path.getFlowPointsPath(project);

        if (!new File(path).exists()) {

            flowpoint_groups = RP.Data.newDefaultFlowPoints(project);

        } else {

            String txt = FileUtil.read(path);
            flowpoint_groups = JSON.parseArray(txt,MFlowPointGroup.class);
        }

    }

    private void newDefFormsEditor() {

        MEditor editor = new MEditor();

        editor.name = "Forms";
        editor.type = MEditor.FLOW;
        editor.descript = "forms manager";



        MFlowPoint defPoint = new MFlowPoint();
        editor.defPoint = "900101";

        defPoint.name =  "form";

        defPoint.icon = Icons.path_form;
        defPoint.mproperties = new ArrayList<>();


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


        defPoint.mproperties.add(p_event);
        defPoint.mproperties.add(p_class);
        defPoint.mproperties.add(p_name);
        defPoint.mproperties.add(p_plate);



        editors.add(editor);



    }



    private void newDefFieldTypes() {

        fieldTypes = new ArrayList<>();

        MFieldType type0 = new MFieldType();
        type0.name = "auto";
        type0.value = "<%R.getSequence()%>";

        MFieldType type1 = new MFieldType();
        type1.name = "boolean";
        type1.value= "true,false";

        MFieldType type2 = new MFieldType();
        type2.name = "Event";
        type2.value= "id|name";
        type2.path="/iedata/Event.ie";


        MFieldType editortype = new MFieldType();
        editortype.name = "Editor";
        editortype.value= "TABLE,FLOW";

        fieldTypes.add(type0);
        fieldTypes.add(type1);
        fieldTypes.add(type2);
        fieldTypes.add(editortype);


    }


    private void newDefEvents(){



        MEditor editor = new MEditor();

        editor.name = "Event";
        editor.type = MEditor.TABLE;
        editor.descript = "event editor";


        editor.fields = new ArrayList<>();
        MProperty field_0 = new MProperty();
        field_0.name = "id";
        field_0.descript = "key";
        field_0.args = "auto";
        field_0.visible = false;


        MProperty field_1 = new MProperty();
        field_1.name = "name";
        field_1.descript = "event name";


        MProperty field_2 = new MProperty();
        field_2.name = "descript";
        field_2.descript = "descript event";


        editor.fields.add(field_0);
        editor.fields.add(field_1);
        editor.fields.add(field_2);


        editors.add(editor);



    }

    public void uninit() {




    }


    @JSONField(serialize = false)
    public MEditor getEditor(String filename){

        for(MEditor mEditor:editors){

            if( (mEditor.name+".ie").equals(filename) ){

                return mEditor;
            }

        }
        return null;

    }

    public MEditor getEditorByName(String name){


        for(MEditor mEditor : editors){

            if( mEditor.name.equals(name) )
                return mEditor;

        }
        return null;

    }



    @JSONField(serialize = false)
    ConsoleViewImpl consoleview;

    @JSONField(serialize = false)
    static final String  WIN_TAG = "IFLog";

    @JSONField(serialize = false)
    public ConsoleViewImpl getConsoleView(Project project) {


        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow window = manager.getToolWindow(WIN_TAG);

        if( consoleview == null ) {

            consoleview = new ConsoleViewImpl(project, true);



                try {
                    manager.unregisterToolWindow(WIN_TAG);
                    window = manager.registerToolWindow(WIN_TAG, consoleview.getComponent(), ToolWindowAnchor.BOTTOM);
                    window.setIcon(Icons.icon_logo);

                } catch (Exception ee) {
                    ee.printStackTrace();
                }

        }

       // window.setTitle(WIN_TAG);
        window.show(null);

        return consoleview;


    }

    public void save(Project project) {

        String txt = JSON.toJSONString(this, SerializerFeature.PrettyFormat);

        String path = RP.Path.getIeData(project);

        try {
            FileUtil.save(txt,path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://" + path);


    }
}
