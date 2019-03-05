package org.ifelse.editors;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.ifelse.RP;
import org.ifelse.model.MEditor;
import org.ifelse.utils.Log;

import java.io.File;
import java.io.IOException;

public class EditorFactory {

    public static void open(Project project,MEditor editor){


        String path = RP.Path.getPath(project,editor);

        Log.i("open file:%s",path);
        File file = new File(path);

        boolean exist = file.exists();
        if( !exist )
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        VirtualFile vf = VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://" + path);

        FileEditorManager.getInstance(project).openFile(vf, true,false);




    }

    public static void open(Project project,String path){


        File file = new File(path);

        if( file.exists() ) {

            VirtualFile vf = VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://" + path);
            if (vf != null)
                FileEditorManager.getInstance(project).openFile(vf, true);

        }
        else {

            try {
                file.createNewFile();
                VirtualFile vf = VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://" + path);
                if (vf != null)
                    FileEditorManager.getInstance(project).openFile(vf, true);

            } catch (IOException e) {
                Log.console(project,e);
            }



        }

    }

}
