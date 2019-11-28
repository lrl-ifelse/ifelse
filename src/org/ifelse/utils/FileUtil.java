package org.ifelse.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.ifelse.IEAppLoader;
import org.ifelse.model.DartClass;
import org.ifelse.model.DartField;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
    public static void save(String context, String filepath) throws Exception {


        File file = new File(filepath);
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(filepath);
        fos.write(context.getBytes("utf-8"));
        fos.close();


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

    public static Document getDocument(String from){

        try {
            VirtualFile vf = VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://" + from);
            final Document document = FileDocumentManager.getInstance().getDocument(vf);
            return document;

        }catch (Exception e){

            Project project = IEAppLoader.getProjectByPath(from);
            Log.console(project,e);

        }
        return null;
    }


    public static String read(String from) {


        Log.i("read :%s exist:%b",from,new File(from).exists());
        try {
            VirtualFile vf = VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://" + from);
            final Document document = FileDocumentManager.getInstance().getDocument(vf);
            return document.getText();

        }catch (Exception e){

            Project project = IEAppLoader.getProjectByPath(from);
            Log.console(project,e);

        }

       /*
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


    public static void refresh(String dir) {

        VirtualFile vf = VirtualFileManager.getInstance().findFileByUrl("file://" + dir);
        if( vf != null )
         vf.refresh(false,true);
    }



    public static void addActivity(String path,StringBuffer activitystr){


        StringBuffer manifest_strings = new StringBuffer();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));

            String line;

            boolean inserted = false;

            while( (line = reader.readLine()) != null )
            {
                manifest_strings.append(line).append('\n');

                if( !inserted && line.indexOf("</activity") > -1 ){

                    manifest_strings.append(activitystr).append('\n');
                    inserted = true;

                }

            }
            reader.close();

            save(manifest_strings.toString(),path);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public static String getClass(String line){


        Matcher matcher = DartParser.patter_class.matcher(line);
        if( matcher.find() ){

            String classstr = matcher.group(0);
            String[] classes = classstr.split("\\s|\\{");
            return classes[1];

        }
        return null;


    }

    public static DartField getField(String line){


        Matcher matcher = DartParser.pattern_field.matcher(line);

        if( matcher.find() ){

            String[] fields = matcher.group(0).split("\\s|;");

            DartField field = new DartField( fields[0],fields[1] );

            return field;

        }
        return null;

    }


    public static String getAnnotation(String line){

        String txt = line.trim();
        if( txt.length() > 0 && txt.charAt(0) == '@' ){

            return line.trim();
        }
        return null;

    }

    public static List<DartClass> getDartClassList(Project project,String filepath) {


        List<DartClass> result = new ArrayList<>();

        VirtualFile vf = VirtualFileManager.getInstance().refreshAndFindFileByUrl("file://" + filepath);
        final Document document = FileDocumentManager.getInstance().getDocument(vf);

        Log.consoleError(project,"-----------------------------");

        String class_annotation = null;

        DartClass dartClass = null;

        for(int i=0;i<document.getLineCount();i++){

            int start = document.getLineStartOffset(i);
            int end = document.getLineEndOffset(i);

            String line = document.getText(new TextRange(start,end));

            if( line.length() > 0  ){

                if( class_annotation == null ){

                    class_annotation = getAnnotation(line);
                    if( class_annotation != null )
                        continue;

                }


                String classname = getClass(line);

                if( classname != null ){

                    dartClass = new DartClass(classname);

                    result.add(dartClass);

                    dartClass.class_annotation = class_annotation;
                    class_annotation = null;

                    continue;

                }

                if( dartClass != null  ){

                    DartField field = getField(line);
                   // Log.consoleError(project,"field: %s",field);

                    if(field != null && dartClass != null ){

                        dartClass.fields.put(field.name,field);
                        continue;

                    }

                }
            }

        }
        return result;


    }
}
