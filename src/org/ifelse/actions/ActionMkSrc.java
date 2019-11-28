package org.ifelse.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import org.ifelse.utils.Log;
import org.jetbrains.annotations.NotNull;

public class ActionMkSrc extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {


        VirtualFile virtualFile = anActionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);

        if( virtualFile.isDirectory() )
        for(VirtualFile vf :  virtualFile.getChildren()){

            if( vf.getExtension().endsWith("cpp") || vf.getExtension().endsWith("c")){

                Log.console(anActionEvent.getProject(),"src/main/cpp/paperp/%s \\",vf.getName());

            }

        }

    }
}
