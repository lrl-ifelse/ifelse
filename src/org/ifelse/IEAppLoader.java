package org.ifelse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.ide.ApplicationLoadListener;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.file.impl.FileManager;
import com.intellij.util.EditorPopupHandler;
import org.ifelse.editors.VLEditor;
import org.ifelse.message.MessageCenter;
import org.ifelse.model.*;
import org.ifelse.utils.FileUtil;
import org.ifelse.utils.GroovyUtil;
import org.ifelse.utils.Log;
import org.ifelse.utils.UnZip;
import org.ifelse.vl.VLItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IEAppLoader implements ApplicationLoadListener, FileEditorManagerListener {


    public static Object copy_item;

    static HashMap<String, MProject> projects = new HashMap<>();



    @Override
    public void beforeApplicationLoaded(@NotNull Application application, @NotNull String configPath) {



        ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerListener() {
            @Override
            public void projectOpened(Project project) {

                opened(project);

                FileEditorManager.getInstance(project).addFileEditorManagerListener(IEAppLoader.this);


                /*
                ActionManager am = ActionManager.getInstance();



                Log.i("ifelse_menu :%s",am.getAction("ifelse_menu").getClass());


                com.intellij.openapi.actionSystem.impl.ChameleonAction s;

                DefaultActionGroup ifelse_menu = (DefaultActionGroup) am.getAction("ifelse_menu");
                String path = RP.Path.getIEMenu(project);


                if( new File(path).exists() ){

                    String txt = FileUtil.read(path);

                    MMenu mMenu = JSON.parseObject(txt, MMenu.class);

                    if( mMenu != null && mMenu.menus != null ) {

                        ifelse_menu.addSeparator();

                        for (MMenuItem item : mMenu.menus) {


                            AnAction anAction = new AnAction(item.title) {
                                @Override
                                public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

                                    try {

                                        String[] script = item.action.split("\\.");

                                        String filepath = String.format("%s%s/%s.groovy",project.getBasePath(),RP.Path.script,script[0]);

                                        Log.console(project,"run script:%s %s",filepath,script[1]);
                                        GroovyUtil.run(project,filepath,script[1],null,null);
                                    } catch (Exception e) {

                                        Log.console(project,e);
                                    }



                                }
                            };

                            am.registerAction("menu_" + item.hashCode(), anAction);
                            ifelse_menu.addAction(anAction);

                        }



                    }


                }
                */



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
        if( filename.endsWith(".dart") ){


            List<DartClass> classes = FileUtil.getDartClassList(project,filepath);


            for(DartClass dartClass : classes){


                Log.consoleError(project,dartClass.toString() );


                try {


                    String jscript = String.format("%s%s/R.groovy",project.getBasePath(),RP.Path.script);

                    Log.console(project,"run script:%s onDartChanged",filepath);

                    GroovyUtil.run(project,jscript,"onDartChanged",dartClass);

                } catch (Exception e) {

                    Log.console(project,e);
                }


            }




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

            projects.put(key,mProject);

            mProject.init(project);



        }

    }

    public static void create(Project project) throws IOException {

        //getClass().getClassLoader().getResourceAsStream()

        {

            InputStream inputStream = IEAppLoader.class.getClassLoader().getResourceAsStream("/res.zip");
            String dir = RP.Path.getIEPath(project);
            UnZip.unzip(inputStream, dir);
            inputStream.close();

        }


        {
            String src = project.getBasePath() + "/app/src/main/java";
            //if (new File(src).exists()) {

                InputStream srcStream =IEAppLoader.class.getClassLoader().getResourceAsStream("/src.zip");

                UnZip.unzip(srcStream, src);
                srcStream.close();


            //}

        }
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
        VirtualFileManager.getInstance().syncRefresh();


        Log.consoleError(project,"*********************************");

        Log.consoleError(project,"init ifelse ");
        Log.consoleError(project,"implementation 'com.alibaba:fastjson:1.2.55'");
        Log.consoleError(project,"Application implements VL._adapter");


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
            } catch (Exception e) {
                //e.printStackTrace();

                Log.console(project,"load project.json error.");
                Log.console(project,e);
            }

            MProject mProject = reader.readObject(MProject.class);

            reader.close();

            return mProject;

        }
        return null;


    }

    public static void save(Project project){

        MProject mProject = getMProject(project);
        if(mProject != null && mProject.isIEProject )
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

        PsiAwareTextEditorImpl com;

        Log.i("file opened:%s",source.getSelectedEditor(file).getComponent());

    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {

//        if( source.getSelectedEditor() instanceof MessageCenter.IMessage) {
//            MessageCenter.unregister( source.getProject(),  ( MessageCenter.IMessage )source.getSelectedEditor() );
//        }

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

