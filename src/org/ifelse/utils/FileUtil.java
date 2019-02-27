package org.ifelse.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.ifelse.IEAppLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtil {
    public static void save(String context, String filepath) throws Exception {


        File file = new File(filepath);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(filepath);
        fos.write(context.getBytes("utf-8"));
        fos.close();


    }

    public static void open(Project project, String path) {

        VirtualFile vf = VirtualFileManager.getInstance().findFileByUrl("file://" + path);
        FileEditorManager.getInstance(project).openFile(vf, true);

    }

    public static void empty(String s) {

        File file = new File(s);
        if (file.isDirectory()) {
            if (!file.exists()) {

                file.mkdirs();
            }

            File[] files = file.listFiles();
            for (File f : files)
                f.delete();

        }


    }

    public static void copy(String from, String to) {

        File file_to = new File(to);
        if (!file_to.getParentFile().exists())
            file_to.getParentFile().mkdirs();

        try {
            FileInputStream fis = new FileInputStream(from);
            FileOutputStream fout = new FileOutputStream(to);

            byte[] bytes = new byte[1024];
            int len = 0;

            while ((len = fis.read(bytes)) > 0) {

                fout.write(bytes, 0, len);

            }

            fis.close();
            fout.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public static String read(String from) {


        try {
            VirtualFile vf = VirtualFileManager.getInstance().findFileByUrl("file://" + from);

            final Document document = FileDocumentManager.getInstance().getDocument(vf);

            return document.getText();

        }catch (Exception e){

            //e.printStackTrace();

            Project project = IEAppLoader.getProjectByPath(from);

            Log.console(project,e);

        }

        /*

        File file_to = new File(from);

        try {
            FileInputStream fis = new FileInputStream(from);
            ByteOutputStream bout = new ByteOutputStream();

            byte[] bytes = new byte[1024];
            int len = 0;

            while ((len = fis.read(bytes)) > 0) {

                bout.write(bytes, 0, len);

            }

            fis.close();


            return new String(bout.getBytes(), "utf-8");

        } catch (Exception e) {

            e.printStackTrace();
        }

        */

        return null;

    }


}
