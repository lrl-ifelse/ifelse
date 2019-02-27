package org.ifelse.editors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.ifelse.IEAppLoader;
import org.ifelse.RP;
import org.ifelse.message.MessageCenter;
import org.ifelse.message.MsgEvent;
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
import java.util.ArrayList;
import java.util.HashMap;

public class VLEditor extends IEEditor implements MessageCenter.IMessage {

    JPanel panel;
    VLDoc doc;

    public VLEditor(Project project, @NotNull VirtualFile virtualFile, MEditor editor) {
        super(project, virtualFile, editor);
        MessageCenter.register(project,this);

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

            toolBar.setFloatable(false);
            toolBar.addSeparator();
            toolBar.add(btn_add);
            toolBar.add(btn_refresh);
            toolBar.addSeparator();



            if( mEditor!= null ) {
                JLabel label = new JLabel();
                label.setText(mEditor.descript);
                toolBar.add(label);
            }


            panel.add(toolBar, BorderLayout.NORTH);


            doc = new VLDoc(new VLDoc.VListener() {
                @Override
                public Project project() {
                    return project;
                }

                @Override
                public void onFocus(final VLItem item_focus) {


                    if( !ToolWindowManager.getInstance(project).getToolWindow("Property").isVisible() ) {

                        if( item_focus != null )
                        ToolWindowManager.getInstance(project).getToolWindow("Property").show(new Runnable() {
                            @Override
                            public void run() {
                                MessageCenter.sendMsg(project, MsgEvent.B_FLOWITEM_FOCUS, item_focus);
                            }
                        });
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

        String txt = FileUtil.read(path);

        java.util.List<VLItem> items =   new ArrayList<>();

        JSONArray array = JSON.parseArray(txt);

        doc.getElements().clear();
        HashMap<String,VLItem> i_points = new HashMap<>();
        HashMap<String,VLLine> i_lines = new HashMap<>();


        if( array == null )
            return;

        for(Object obj : array){

             JSONObject jobject = (JSONObject) obj;
             String classname = jobject.getString("classname");

             VLItem item = (VLItem) jobject.toJavaObject( Class.forName(classname) );

             if( item.isLine() ) {
                 i_lines.put(item.id, (VLLine) item);
             }
             else
                 i_points.put(item.id,item);


             doc.getElements().add(item);
             item.initUI(project);

         }
         for(VLLine line:i_lines.values()){

             line.point_from = i_points.get(line.id_from);
             line.point_to = i_points.get(line.id_to);

         }
         i_points.clear();
         i_points.clear();


    }





    public void save(){

         String txt = JSON.toJSONString(doc.getElements(), SerializerFeature.PrettyFormat);
//         String path = virtualFile.getPath();
//        try {
//            FileUtil.save(txt,path);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        virtualFile.refresh(false,false);

        final Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);


        new WriteCommandAction.Simple(project, psiFile) {
            @Override
            protected void run() {
                document.setText( txt );
                FileDocumentManager.getInstance().saveDocument(document);
            }
        }.execute();


    }

    @Override
    public void deselectNotify() {

        save();
        ToolWindowManager.getInstance(project).getToolWindow("Property").hide(null);
    }

    @Override
    public void selectNotify() {

        ToolWindowManager.getInstance(project).getToolWindow("FlowPoint").show(null);

    }

    @Override
    public boolean onMessage(Project project, MsgEvent event, Object value) {


        switch (event) {

            case B_PROPERTY_CHANGED: {

                if (FileEditorManager.getInstance(project).getSelectedEditor() == this) {

                    doc.repaint();
                }
                return true;
            }
        }
        return false;
    }
}
