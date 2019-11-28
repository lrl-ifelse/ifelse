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
package org.ifelse;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.ifelse.IEAppLoader;
import org.ifelse.editors.TableEditor;
import org.ifelse.editors.VLEditor;
import org.ifelse.model.MEditor;
import org.ifelse.model.MProject;
import org.ifelse.utils.GroovyUtil;
import org.ifelse.utils.Log;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class IEProvider implements FileEditorProvider {
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {

        MProject mProject = IEAppLoader.getMProject(project);

        return mProject != null && mProject.isIEProject && virtualFile.getName().endsWith(".ie");
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {

        FileEditor fileEditor  = null;

        MProject mProject = IEAppLoader.getMProject(project);
        if( mProject == null )
            return null;
        MEditor editor =  mProject.getEditor(virtualFile.getName());

        if(editor !=null) {
            switch (editor.type) {
                case "TABLE":
                    fileEditor =  new TableEditor(project, virtualFile, editor);
                    break;
                case "FLOW":
                    fileEditor =  new VLEditor(project, virtualFile, editor);
                    break;
            }
        }
        else
            fileEditor =  new VLEditor(project, virtualFile, editor);
        return fileEditor;



    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "ie";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
         return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
