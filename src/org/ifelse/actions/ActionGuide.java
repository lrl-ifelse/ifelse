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

import com.intellij.diagnostic.IdeErrorsDialog;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.ToolWindowManager;
import org.ifelse.IEAppLoader;
import org.ifelse.RP;
import org.ifelse.editors.EditorFactory;
import org.ifelse.model.MEditor;
import org.ifelse.model.MProject;
import org.ifelse.ui.MsgDialog;
import org.ifelse.utils.Icons;
import org.ifelse.utils.Log;

import org.ifelse.utils.Util;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ActionGuide extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {


        MProject mProject = IEAppLoader.getMProject(anActionEvent.getProject());
        if( mProject.isIEProject ) {
            MEditor editor = IEAppLoader.getMProject(anActionEvent.getProject()).getEditorByName("Flows");
            EditorFactory.open(anActionEvent.getProject(), editor);
        }
        ToolWindowManager.getInstance(anActionEvent.getProject()).getToolWindow("Guide").show(null);


    }


    public ActionGuide(){

        getTemplatePresentation().setIcon(Icons.icon_logo);

    }




}
