package org.ifelse.windows;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.ifelse.IEAppLoader;
import org.ifelse.message.MessageCenter;
import org.ifelse.message.MsgEvent;
import org.ifelse.model.MFlowPoint;
import org.ifelse.model.MProject;
import org.ifelse.model.MProperty;
import org.ifelse.ui.KV;
import org.ifelse.ui.KVFactory;
import org.ifelse.ui.PropertyCellEditor;
import org.ifelse.utils.Icons;
import org.ifelse.utils.ListMap;
import org.ifelse.vl.VLItem;
import org.ifelse.vl.VLPoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PropertyWindow implements ToolWindowFactory, MessageCenter.IMessage, PropertyCellEditor.OnValueChangedListener {

    JTable table;
    KVFactory factory;
    MProject mProject;
    Project project;
    JLabel label;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(createPanel(project), "", false);
        toolWindow.getContentManager().addContent(content);
        MessageCenter.register(project, this);


    }

    private JComponent createPanel(Project project) {


        this.project = project;
        mProject = IEAppLoader.getMProject(project);

        factory = new KVFactory(mProject);


        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();


        //ToolbarButton btn_add = new ToolbarButton(AllIcons.ToolbarDecorator.Add);
        //ToolbarButton btn_remove = new ToolbarButton(AllIcons.ToolbarDecorator.Remove);

        label = new JLabel();
        toolBar.setMargin(new Insets(10,0,10,0));

        toolBar.setFloatable(false);
        toolBar.addSeparator();


        toolBar.add(label);

        toolBar.addSeparator();

        panel.add(toolBar, BorderLayout.NORTH);


        table = new JTable();


        table.setRowHeight(40);

        //table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        table.setTableHeader(null);

        //table.setSelectionBackground(new Color(0,0,0,0));


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

        return IEAppLoader.isMProject(project);
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return true;
    }

    @Override
    public boolean onMessage(Project project, MsgEvent event, Object value) {


        //Log.i("Property windows receive %s %s", event, value);
        switch (event) {

            case B_FLOWITEM_FOCUS:

                setProperties(value);

                return false;


        }

        return false;
    }

    private void setProperties(Object value) {

        if (value instanceof VLItem) {

            final VLItem item = (VLItem) value;

            if(item instanceof VLPoint){

                VLPoint point = (VLPoint) item;

                MFlowPoint mFlowPoint = IEAppLoader.getMProject(project).flowpoints.get( point.flow_point_id );

                if( mFlowPoint != null )
                {
                    label.setText(mFlowPoint.name +" "+mFlowPoint.classz);
                }

            }
            else if( item.isLine() ) {
                label.setText("Line");
            }



            table.setModel(new DefaultTableModel() {

                @Override
                public int getRowCount() {
                    return item.mproperties.size();
                }

                @Override
                public int getColumnCount() {
                    return 2;
                }

                @Override
                public Object getValueAt(int row, int column) {

                    MProperty mp = item.mproperties.get(row);
                    if (column == 0)
                        return mp.name;


                    if (isNULL(mp.args))
                        return mp.value;
                    else {
                        if (isNULL(mp.value))
                            return "";
                        ListMap<String, KV> kvList = factory.getItems(mp);
                        return kvList.get(mp.value).value;
                    }

                }


                @Override
                public boolean isCellEditable(int row, int column) {

                    if (column == 0)
                        return false;
                    MProperty mp = item.mproperties.get(row);
                    return mp.visible;

                }
            });


            table.setDefaultEditor(Object.class, new PropertyCellEditor(mProject, this) {

                @Override
                public MProperty getProperty(int row) {
                    return item.mproperties.get(row);
                }
            });


        } else {

            label.setText("");
            table.setModel(new DefaultTableModel() {
                @Override
                public int getRowCount() {
                    return 0;
                }
            });


        }

    }


    private boolean isNULL(String args) {
        return args == null || args.length() == 0;
    }

    @Override
    public void onValueChanged(MProperty property, int row, int index) {

        MessageCenter.sendMsg(project, MsgEvent.B_PROPERTY_CHANGED, property);

    }
}
