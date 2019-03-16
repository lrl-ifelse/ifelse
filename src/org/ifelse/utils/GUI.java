package org.ifelse.utils;

import com.intellij.debugger.memory.utils.AndroidUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.impl.JsonObjectImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPlainTextFile;
import org.apache.batik.dom.util.DocumentFactory;

import java.util.Properties;

public class GUI {


    public static void println(Project project, String format, Object ... args){

        Log.console(project,format,args);
    }

    public static void error(Project project, String format, Object ... args){

        Log.consoleError(project,format,args);

    }

    public static void open(Project project, String path) {

        Util.open(project,path);

    }
    public static void openUrl(String url){

        Util.openUrl(url);

    }


    public static void goline(Project project,String path,int offset){

        VirtualFile vf = VirtualFileManager.getInstance().findFileByUrl("file://" + path);
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, vf,offset);
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        fileEditorManager.openEditor(descriptor, true);

    }
    public static void goline(Project project,String path,int line,int col){

        VirtualFile vf = VirtualFileManager.getInstance().findFileByUrl("file://" + path);
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, vf,line,col);
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        fileEditorManager.openEditor(descriptor, true);

    }
    public static void goline(Project project,VirtualFile vf,int offset){

        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, vf,offset);
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        fileEditorManager.openEditor(descriptor, true);

    }
    public static void goline(Project project,String path,String str){

        int index = 0;
        VirtualFile vf = VirtualFileManager.getInstance().findFileByUrl("file://" + path);
        if( str != null ) {
            final Document document = FileDocumentManager.getInstance().getDocument(vf);
            index =  document.getText().indexOf(str);
        }
        if( index < 0 )
            index = 0;
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, vf,index);
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        fileEditorManager.openEditor(descriptor, true);


    }



}
