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
package org.ifelse.utils;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.panels.NonOpaquePanel;
import org.ifelse.IEAppLoader;
import org.ifelse.model.MProject;

import javax.swing.*;
import java.awt.*;

public class Log {



    public static void i(String format,Object ... args){

        System.out.println(  String.format(format,args) );

    }


    public static void console(Project project, Exception ee) {

        ee.printStackTrace();

        if( project == null )
            return;

        ConsoleViewImpl console = IEAppLoader.getMProject(project).getConsoleView(project);

        String msg = ee.toString();

        if (msg.indexOf("NullPointerException") > -1) {

            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            int i = 0;
            for (StackTraceElement e : stackTrace) {

                if (i++ > 20)
                    break;
                console.print(String.format("%s %s(%d) ->%s\n", e.getClassName(), e.getMethodName(), e.getLineNumber(), e.toString()), ConsoleViewContentType.ERROR_OUTPUT);

            }

        } else
            console.print(msg, ConsoleViewContentType.ERROR_OUTPUT);

        console.print("\n", ConsoleViewContentType.ERROR_OUTPUT);
        console.requestScrollingToEnd();


    }
    public static void console(Project project,String format,Object ... args) {

        if( project == null )
            return;

        ConsoleViewImpl console = IEAppLoader.getMProject(project).getConsoleView(project);
        String msg = String.format(format,args);
        console.print(msg,ConsoleViewContentType.NORMAL_OUTPUT);
        console.print("\n", ConsoleViewContentType.NORMAL_OUTPUT);
        console.requestScrollingToEnd();

    }
}
