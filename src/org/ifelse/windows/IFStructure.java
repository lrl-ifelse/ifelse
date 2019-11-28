package org.ifelse.windows;

import com.intellij.icons.AllIcons;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.impl.JsonObjectImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.ifelse.IEAppLoader;
import org.ifelse.RP;
import org.ifelse.editors.EditorFactory;
import org.ifelse.model.DartClass;
import org.ifelse.model.MEditor;
import org.ifelse.model.MLineLabel;
import org.ifelse.model.MProject;
import org.ifelse.ui.EditorItem;
import org.ifelse.ui.LineEditorItem;
import org.ifelse.ui.OnClickListener;
import org.ifelse.ui.ToolbarButton;
import org.ifelse.utils.GUI;
import org.ifelse.utils.Icons;
import org.ifelse.utils.Log;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IFStructure implements ToolWindowFactory {


    JTable table;
    MProject mProject;
    String path;
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {


        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(createPanel(project),"", false);
        toolWindow.getContentManager().addContent(content);




        FileEditorManager.getInstance(project).addFileEditorManagerListener(new FileEditorManagerListener() {




            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {

              onFileOpened(file);

            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {

                if( event != null && event.getNewEditor() != null )
                    onFileOpened( event.getNewEditor().getFile() );

            }
        });



        FileEditor  editor = FileEditorManager.getInstance(project).getSelectedEditor();
        if( editor != null ){


            onFileOpened(editor.getFile());

        }



    }

    private void onFileOpened(VirtualFile file) {

        if( file == null )
            return;
        path = file.getPath();

        final Document document = FileDocumentManager.getInstance().getDocument(file);

        if( document == null )
            return;
        List<MLineLabel> lineLabels = new ArrayList<>();

        for(int i=0;i<document.getLineCount();i++) {

            int start = document.getLineStartOffset(i);
            int end = document.getLineEndOffset(i);

            String line = document.getText(new TextRange(start, end));

            int index = line.indexOf("@descript");
            if( index > -1 ){

                MLineLabel lineLabel = new MLineLabel();

                lineLabel.line = i;
                lineLabel.descript = line.substring(index+"@descript".length());
                lineLabels.add(lineLabel);

            }

        }



        table.setModel(new DefaultTableModel() {
            @Override
            public int getRowCount() {
                return lineLabels.size();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return lineLabels.get(rowIndex);
            }


            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }


        });


    }


    private JComponent createPanel(final Project project) {


        mProject = IEAppLoader.getMProject(project);

        JPanel panel = new JPanel();
        JToolBar toolBar = new JToolBar();
        table = new JTable();

        panel.setLayout(new BorderLayout());



        ToolbarButton btn_init = new ToolbarButton(AllIcons.Actions.Refresh,100);
        btn_init.setText("@descript");

        btn_init.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(JComponent component) {

                onFileOpened(VirtualFileManager.getInstance().findFileByUrl("file://"+path));

            }
        });

        toolBar.setFloatable(false);
        toolBar.addSeparator();

        toolBar.add(btn_init);


        panel.add(toolBar, BorderLayout.NORTH);

            table.setDefaultRenderer(Object.class, new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    return new LineEditorItem((MLineLabel) value,row);
                }
            });

            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {


                    int rowIndex = table.getSelectedRow();

                    if (rowIndex > -1) {


                       MLineLabel lineLabel = (MLineLabel) table.getModel().getValueAt(rowIndex,0);



                       GUI.goline(project,path,lineLabel.line+1,0);


                    }

                }
            });


            table.setRowHeight(40);

            table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

            table.setTableHeader(null);

            table.setSelectionBackground(new Color(0, 0, 0, 0));


            JScrollPane scrollPane = new JScrollPane(table);

            panel.add(scrollPane, BorderLayout.CENTER);


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
