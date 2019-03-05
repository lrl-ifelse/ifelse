package org.ifelse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.ide.ApplicationLoadListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.impl.file.impl.FileManager;
import org.ifelse.editors.VLEditor;
import org.ifelse.message.MessageCenter;
import org.ifelse.model.MProject;
import org.ifelse.utils.FileUtil;
import org.ifelse.utils.Log;
import org.ifelse.vl.VLItem;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class IEAppLoader implements ApplicationLoadListener, FileEditorManagerListener {


    public static VLItem copy_item;

    static HashMap<String, MProject> projects = new HashMap<>();



    @Override
    public void beforeApplicationLoaded(@NotNull Application application, @NotNull String configPath) {



        ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerListener() {
            @Override
            public void projectOpened(Project project) {

               opened(project);

                FileEditorManager.getInstance(project).addFileEditorManagerListener(IEAppLoader.this);




            }
            @Override
            public void projectClosed(Project project) {


                MessageCenter.unregister(project);

                closed(project);


            }

            @Override
            public void projectClosing(Project project) {

            }

            @Override
            public void projectClosingBeforeSave(@NotNull Project project) {

                Log.i("project : projectClosingBeforeSave");

            }
        });

        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {


            @Override
            public void contentsChanged(@NotNull VirtualFileEvent event) {

                String filename = event.getFileName();
                String filepath = event.getFile().getPath();

                if( "workspace.xml,R.java".indexOf(event.getFileName()) >-1 )
                {
                    return;
                }

                Project project = getProjectByPath(filepath);

                if( project != null )
                    onFileChanged(project,filename,filepath);

            }

            @Override
            public void fileCreated(@NotNull VirtualFileEvent event) {

            }




        });





    }

    private void onFileChanged(Project project, String filename, String filepath) {

        switch (filename){

            case "project.json":

                opened(project);
                break;

        }

        if( filepath.indexOf("iedata") > -1 ){

            Log.console(project,"*%s*",filepath);
        }


    }


    public static Project getProjectByPath(String path){

        Project[] projects = ProjectManager.getInstance().getOpenProjects();


        for(Project project : projects){


            if( path.indexOf(project.getBasePath()) > -1 ){

                return project;
            }

        }
        return null;


    }





    public static void opened(Project project){

        String key = project.getName();

        projects.remove(key);

        Log.i("project :%s opened",key);
        if( !projects.containsKey(key) ){

            MProject mProject = null;
            String path = RP.Path.getIeData(project);
            if( new File(path).exists() )
            {
                mProject = load(project);
            }
            else {
                mProject = new MProject();
            }

            mProject.init(project);

            projects.put(key,mProject);

        }

    }


    public static void closed(Project project){

        String key = project.getName();

        Log.i("project :%s closed",key);
        save(project);

        if( projects.containsKey(key) ){

            MProject mProject = projects.remove(key);
            if( mProject != null ){
                mProject.uninit();
            }
            projects.remove(key);

        }


    }

    public static MProject load(Project project){

        String path = RP.Path.getIeData(project);

        File file = new File(path);

        if (file.exists()) {

            JSONReader reader = null;
            try {
                reader = new JSONReader(new FileReader( file ));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            MProject mProject = reader.readObject(MProject.class);

            reader.close();

            return mProject;

        }
        return null;


    }

    public static void save(Project project){

        MProject mProject = getMProject(project);
        mProject.save(project);

    }



    public static MProject getMProject(Project project){

        return projects.get(project.getName());

    }

    public static boolean isMProject(Project project){
        MProject mProject = projects.get(project.getName());

        return mProject!=null&&mProject.isIEProject;
    }


    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {


        Log.i("File opened:%s",file.getPath());
        if( source.getSelectedEditor() instanceof VLEditor){

            //((VLEditor) source.getSelectedEditor()).focusEditor();

        }

    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {


        if( source.getSelectedEditor() instanceof MessageCenter.IMessage) {
            MessageCenter.unregister( source.getProject(),  ( MessageCenter.IMessage )source.getSelectedEditor() );
        }

    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {



        if( event.getOldEditor()  instanceof MessageCenter.IMessage ){

            MessageCenter.unregister( event.getManager().getProject(),  ( MessageCenter.IMessage )event.getOldEditor() );
        }
        if( event.getNewEditor() instanceof MessageCenter.IMessage ){

            MessageCenter.register(event.getManager().getProject(),( MessageCenter.IMessage )event.getNewEditor());
        }



    }
}

