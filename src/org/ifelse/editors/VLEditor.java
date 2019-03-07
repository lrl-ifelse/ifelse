package org.ifelse.editors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.AsyncExecutionService;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.tabs.JBTabs;
import org.ifelse.IEAppLoader;
import org.ifelse.RP;
import org.ifelse.message.MessageCenter;
import org.ifelse.message.MsgEvent;
import org.ifelse.model.MDoc;
import org.ifelse.model.MEditor;
import org.ifelse.model.MFlowPoint;
import org.ifelse.ui.OnClickListener;
import org.ifelse.ui.ToolbarButton;
import org.ifelse.utils.*;
import org.ifelse.vl.VLDoc;
import org.ifelse.vl.VLItem;
import org.ifelse.vl.VLLine;
import org.ifelse.vl.VLPoint;
import org.jetbrains.annotations.NotNull;


import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class VLEditor extends IEEditor implements MessageCenter.IMessage {

    JPanel panel;
    VLDoc doc;

    JLabel label;

    public VLEditor(Project project, @NotNull VirtualFile virtualFile, MEditor editor) {
        super(project, virtualFile, editor);
    }

    public void focusEditor(){

                if( panel != null )
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {

                        Container container = getComponent().getParent();
                        while( (container = container.getParent()) != null ){

                            if( container instanceof  JBTabs ){

                                Log.i("Focus editor.");
                                JBTabs tabs = (JBTabs) container;
                                tabs.select(tabs.getTabAt(1),true);
                                break;

                            }


                        }

                    }
                });


    }


    @NotNull
    @Override
    public JComponent getComponent() {



        if( panel == null ){

            panel = new JPanel();

            panel.setLayout(new BorderLayout());


            JToolBar toolBar = new JToolBar();

            ToolbarButton btn_add = new ToolbarButton(AllIcons.ToolbarDecorator.Add);

            btn_add.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(JComponent component) {

                    if(mEditor != null && mEditor.defPoint != null )
                    {

                        MFlowPoint mFlowPoint =  IEAppLoader.getMProject(project).flowpoints.get(mEditor.defPoint);

                        VLPoint point = new VLPoint();

                        point.id =  IEAppLoader.getMProject(project).getSequenceStr(project);

                        point.setImage(IconFactory.createImage(RP.Path.getIconPath(project, mFlowPoint.icon)));

                        point.x = 50;
                        point.y = 50;

                        point.flow_point_id = mFlowPoint.id;

                        point.mproperties = mFlowPoint.copyProperties();

                        doc.getElements().add(point);

                        doc.onFocusChanged(point);



                        doc.repaint();


                    }


                }
            });



            ToolbarButton btn_refresh = new ToolbarButton(AllIcons.Actions.Refresh);
            btn_refresh.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(JComponent component) {

                    virtualFile.refresh(false,false);

                }
            });

            ToolbarButton btn_zoomin = new ToolbarButton(AllIcons.Graph.ZoomIn);

            btn_zoomin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(JComponent component) {
                    doc.zoomin();
                }
            });

            ToolbarButton btn_zoomout = new ToolbarButton(AllIcons.Graph.ZoomOut);

            btn_zoomout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(JComponent component) {
                    doc.zoomout();
                }
            });


            ToolbarButton btn_flows = new ToolbarButton(Icons.icon_logo);

            btn_flows.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(JComponent component) {

                    MEditor editor = IEAppLoader.getMProject(project).getEditorByName("Flows");
                    EditorFactory.open(project, editor);
                    MessageCenter.sendMsg(project,MsgEvent.B_FLOW_FOCUS, doc.flowid,500 );



                }
            });



            toolBar.setFloatable(false);
            toolBar.addSeparator();

            if( mEditor != null && mEditor.defPoint != null )
                toolBar.add(btn_add);

            toolBar.add(btn_refresh);

            toolBar.addSeparator();

            toolBar.add(btn_zoomin);
            toolBar.add(btn_zoomout);


            toolBar.addSeparator();

            if( mEditor == null || mEditor.defPoint == null )
            toolBar.add(btn_flows);


            label = new JLabel();
            toolBar.add(label);



            panel.add(toolBar, BorderLayout.NORTH);


            doc = new VLDoc(new VLDoc.VListener() {
                @Override
                public Project project() {
                    return project;
                }

                @Override
                public void onFocus(final VLItem item_focus) {


                    if( !ToolWindowManager.getInstance(project).getToolWindow("Property").isVisible() ) {

                        if( item_focus != null ) {

                            IdeEventQueue.getInstance().doWhenReady(new Runnable() {
                                @Override
                                public void run() {



                                    ToolWindowManager.getInstance(project).getToolWindow("Property").show(new Runnable() {
                                        @Override
                                        public void run() {
                                            MessageCenter.sendMsg(project, MsgEvent.B_FLOWITEM_FOCUS, item_focus);
                                        }
                                    });



                                }
                            });


                        }
                    }
                    else{

                        MessageCenter.sendMsg(project, MsgEvent.B_FLOWITEM_FOCUS, item_focus);
                    }



                }

                @Override
                public void onDoubleClick(VLItem item_focus) {

                    if( item_focus instanceof VLPoint)
                    {

                        VLPoint point = (VLPoint) item_focus;

                        if( !isNULL(point.flow_point_id) ){


                            MFlowPoint mFlowPoint = IEAppLoader.getMProject(project).flowpoints.get(point.flow_point_id);


                            if( !isNULL(mFlowPoint.doubleclick) ){
                                String[] script = mFlowPoint.doubleclick.split("\\.");

                                String filepath = String.format("%s%s/%s.groovy",project.getBasePath(),RP.Path.script,script[0]);

                                Log.i("run script:%s",filepath);

                                try {
                                    GroovyUtil.run(project,filepath,script[1],point,mFlowPoint);

                                } catch (Exception e) {

                                    Log.console(project,e);
                                }


                            }


                        }

                    }


                }

                @Override
                public void onRemoved(VLItem item_focus) {



                }

                @Override
                public void onDataChanged() {

                    save();

                }

            });
            JScrollPane pane = new JScrollPane(doc);

            pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            doc.setPreferredSize(new Dimension(2000,2000));


            //doc.setSize(2000,2000);

            panel.add(pane,BorderLayout.CENTER);



        }


        try {
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return panel;


    }



    private void load() throws Exception {




        String path = virtualFile.getPath();



        Log.i("VLEditor load:%s",path);


        MDoc mDoc = RP.Data.loadDoc(path);


        if( mDoc.title != null )
            label.setText(mDoc.title);

        //JSONArray array = JSON.parseArray(txt);


        doc.title = mDoc.title;
        doc.flowid = mDoc.flowid;


        doc.getElements().clear();

        HashMap<String,VLItem> i_points = mDoc.getPoints();

        HashMap<String,VLItem> i_lines = mDoc.getLines();


        for(VLItem item : i_points.values()){

             doc.getElements().add(item);
             item.initUI(project);

         }
        for(VLItem item : i_lines.values()){

            doc.getElements().add(item);
            item.initUI(project);

        }


        for(VLItem vline:i_lines.values()){

             VLLine line = (VLLine) vline;
             line.point_from = i_points.get(line.id_from);
             line.point_to = i_points.get(line.id_to);

         }
         i_points.clear();
         i_lines.clear();


    }





    public void save(){


        MDoc mDoc =  doc.getMDoc();
        final String txt = JSON.toJSONString(mDoc, SerializerFeature.PrettyFormat);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                document.setText(txt);
                FileDocumentManager.getInstance().saveDocument(document);
            }

        });



    }

    @Override
    public void deselectNotify() {

        //save();
        ToolWindowManager.getInstance(project).getToolWindow("Property").hide(null);
    }

    @Override
    public void selectNotify() {

        ToolWindowManager.getInstance(project).getToolWindow("FlowPoint").show(null);

    }

    @Override
    public boolean onMessage(Project project, MsgEvent event, Object value) {

        Log.i("onMessage event:%s vf:%s",event,virtualFile.getPath());
        switch (event) {

            case B_PROPERTY_CHANGED: {
                save();

            }
            return true;
            case B_FLOW_FOCUS:{


                    //focusEditor();

                    if( doc != null )
                    for(VLItem item : doc.getElements()){


                        if( item.id.equals(value) ){

                            Log.i("B_FLOW_FOCUS id:%s vf:%s",item.id,virtualFile.getPath());
                            doc.onFocusChanged(item);



                            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                                @Override
                                public void run() {
                                    doc.repaint();
                                }
                            });

                            break;

                        }
                    }

            }
            return true;
        }
        return false;
    }

}
