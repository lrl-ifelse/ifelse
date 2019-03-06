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
package org.ifelse.actions;

import com.intellij.icons.AllIcons;
import com.intellij.ide.SaveAndSyncHandler;
import com.intellij.ide.actions.RefreshAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.impl.file.impl.FileManager;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.ifelse.IEAppLoader;
import org.ifelse.RP;
import org.ifelse.model.MProject;
import org.ifelse.ui.MsgDialog;
import org.ifelse.utils.GroovyUtil;
import org.ifelse.utils.Log;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class ActionRun extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {


        MProject mProject = IEAppLoader.getMProject( anActionEvent.getProject() );

        if( !mProject.isIEProject ){

            MsgDialog.showMessage("提示","非IFElse 项目,点击向导进行初始化。" );
            return;

        }

        FileDocumentManager.getInstance().saveAllDocuments();
        ClassLoader parent = GroovyUtil.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        String path = anActionEvent.getProject().getBasePath()+ RP.Path.script+ "/Run.groovy";

        Log.console(anActionEvent.getProject(),path);

        try {


            Class  groovyClass = loader.parseClass(new File(path));
            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
            groovyObject.setProperty("project",anActionEvent.getProject());
            groovyObject.invokeMethod("run",null);


        } catch (Exception e) {

            Log.console(anActionEvent.getProject(),e);
            e.printStackTrace();
        }

        VirtualFileManager.getInstance().syncRefresh();




    }


    public ActionRun(){


        getTemplatePresentation().setIcon(AllIcons.General.Run);

    }




}
