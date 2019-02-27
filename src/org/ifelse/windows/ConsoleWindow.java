package org.ifelse.windows;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.ifelse.IEAppLoader;
import org.ifelse.message.MessageCenter;
import org.ifelse.ui.KVFactory;
import org.ifelse.utils.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class ConsoleWindow implements ToolWindowFactory {


    public ConsoleViewImpl consoleView;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(createPanel(project), "", false);
        toolWindow.getContentManager().addContent(content);

    }

    private JComponent createPanel(Project project) {



        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();


        //ToolbarButton btn_add = new ToolbarButton(AllIcons.ToolbarDecorator.Add);
        //ToolbarButton btn_remove = new ToolbarButton(AllIcons.ToolbarDecorator.Remove);

        toolBar.setMargin(new Insets(10,0,10,0));

        toolBar.setFloatable(false);
        toolBar.addSeparator();



        toolBar.addSeparator();

        panel.add(toolBar, BorderLayout.NORTH);


        consoleView = new ConsoleViewImpl(project,true);



        panel.add(consoleView,BorderLayout.CENTER);




        return panel;

    }

    @Override
    public void init(ToolWindow window) {
            window.setIcon(Icons.icon_logo);

    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }
}
