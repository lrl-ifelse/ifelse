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
import org.ifelse.utils.Log;
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



    public void init(Project project){

        key = project.getName();

        String path = project.getBasePath();

        this.path = path;

        isIEProject =  new File(path + RP.Path.iedata).exists();

        if( !isIEProject )
            return;
         {
           if(  loadFlowPoints(project) == null )
           {
               Log.consoleError(project,"load points.json error.");
               return;
           }

        }
        initFlowPoints();


    }

    private String initFlowPoints(){

        if( flowpoints != null)
            flowpoints.clear();
        flowpoints = new HashMap<>();

        String result = null;

        for(MFlowPointGroup group:flowpoint_groups){

            for(MFlowPoint point : group.points){


                if( flowpoints.containsKey(point.id) ){
                    result = "发现重复id:"+point.id;
                }
                else
                    flowpoints.put(point.id,point);

            }

        }
        return null;




    }

    public  List<MFlowPointGroup> loadFlowPoints(Project project) {

        String path = RP.Path.getFlowPointsPath(project);
        String txt = FileUtil.read(path);
        try {

            flowpoint_groups = JSON.parseArray(txt, MFlowPointGroup.class);

            String msg = initFlowPoints();
            if( msg != null )
            {
                Log.consoleError(project,msg);
            }
            return flowpoint_groups;

        }catch (Exception e){

            Log.console(project,e);
        }
            return null;

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
        if( window != null ) {
            window.show(null);
        }

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
