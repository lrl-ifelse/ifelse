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
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.ifelse.utils.Icons;
import org.ifelse.utils.Log;
import org.jetbrains.annotations.NotNull;

public class ActionVars extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {


       // Log.console(anActionEvent.getProject(),"ABC :%s","AAA");

    }


    public ActionVars(){

        getTemplatePresentation().setIcon(Icons.icon_vars);

    }




}
