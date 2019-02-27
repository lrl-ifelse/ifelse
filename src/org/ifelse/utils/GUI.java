package org.ifelse.utils;

import com.intellij.openapi.project.Project;

public class GUI {


    public static void println(Project project, String format, Object ... args){

        Log.console(project,format,args);
    }
}
