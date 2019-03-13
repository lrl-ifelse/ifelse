package org.ifelse.utils;

import com.intellij.debugger.memory.utils.AndroidUtil;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

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



}
