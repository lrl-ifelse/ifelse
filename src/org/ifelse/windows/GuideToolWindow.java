package org.ifelse.windows;

import com.intellij.icons.AllIcons;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.impl.JsonObjectImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.SingleSelectionModel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.ifelse.IEAppLoader;
import org.ifelse.RP;
import org.ifelse.editors.EditorFactory;
import org.ifelse.model.MEditor;
import org.ifelse.model.MProject;
import org.ifelse.ui.EditorItem;
import org.ifelse.ui.OnClickListener;
import org.ifelse.ui.ToolbarButton;
import org.ifelse.utils.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GuideToolWindow implements ToolWindowFactory {


    JTable table;
    MProject mProject;
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {


        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(createPanel(project),"", false);
        toolWindow.getContentManager().addContent(content);

    }

    private JComponent createPanel(final Project project) {


        mProject = IEAppLoader.getMProject(project);

        JPanel panel = new JPanel();
        JToolBar toolBar = new JToolBar();
        table = new JTable();

        panel.setLayout(new BorderLayout());




        ToolbarButton btn_edit = new ToolbarButton(AllIcons.ToolbarDecorator.Edit);
        btn_edit.setToolTipText("project.json");

        ToolbarButton btn_types = new ToolbarButton(AllIcons.General.Recursive);
        btn_types.setToolTipText("Optional type of property");


        ToolbarButton btn_init = new ToolbarButton(Icons.icon_logo,100);
        btn_init.setText("create ifelse ");


        btn_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(JComponent component) {


                String path = RP.Path.getIeData(project);
                File file = new File(path);
                if( file.exists() )
                    GUI.goline(project,path,1,4);



            }
        });
          btn_types.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(JComponent component) {

                String path = RP.Path.getIeData(project);
                File file = new File(path);
                if( !file.exists() )
                    return;


                VirtualFile vf = VirtualFileManager.getInstance().findFileByUrl("file://" + path);

                final Document document = FileDocumentManager.getInstance().getDocument(vf);
                PsiFile psiFile =  PsiDocumentManager.getInstance(project).getPsiFile(document);
                PsiElement[] elements = psiFile.getChildren();
                int offset = 0;

                for(PsiElement element : elements){

                    if( element instanceof JsonObjectImpl){
                        JsonObjectImpl jobj = (JsonObjectImpl) element;
                        JsonProperty property = jobj.findProperty("fieldTypes");
                        if( property != null ){
                            offset = property.getTextOffset();
                            GUI.goline(project,vf,offset);
                            break;
                        }
                    }
                }

            }
        });









        btn_init.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(JComponent component) {

                try {
                    IEAppLoader.create(project);
                    IEAppLoader.opened(project);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                component.getParent().remove(component);

                mProject = IEAppLoader.getMProject(project);

                ((DefaultTableModel)table.getModel()).fireTableDataChanged();

            }
        });

        toolBar.setFloatable(false);
        toolBar.addSeparator();
        toolBar.add(btn_edit);
        toolBar.add(btn_types);

        //toolBar.add(btn_remove);
        if( !mProject.isIEProject )
        {
            toolBar.add(btn_init);
        }


        toolBar.addSeparator();

        panel.add(toolBar, BorderLayout.NORTH);







            table.setModel(new DefaultTableModel() {
                @Override
                public int getRowCount() {
                    return mProject.editors == null ? 0 : mProject.editors.size();
                }

                @Override
                public int getColumnCount() {
                    return 1;
                }

                @Override
                public Object getValueAt(int rowIndex, int columnIndex) {
                    return mProject.editors.get(rowIndex);
                }


                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return false;
                }


            });

            table.setDefaultRenderer(Object.class, new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    return new EditorItem((MEditor) value);
                }
            });

            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {


                    int rowIndex = table.getSelectedRow();

                    if (rowIndex > -1) {


                        MEditor editor = mProject.editors.get(rowIndex);

                        Log.i("click row:%s", editor);


                        EditorFactory.open(project, editor);

                    }

                }
            });


            table.setRowHeight(60);

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
