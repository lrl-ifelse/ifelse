package org.ifelse.editors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.sun.java.swing.action.SaveAction;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.ifelse.IEAppLoader;
import org.ifelse.model.MEditor;
import org.ifelse.model.MProperty;
import org.ifelse.ui.OnClickListener;
import org.ifelse.ui.ToolbarButton;
import org.ifelse.utils.FileUtil;
import org.ifelse.utils.Log;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class TableEditor extends IEEditor {


    public TableEditor(Project project, VirtualFile virtualFile, MEditor editor) {
        super(project, virtualFile,editor);

    }

    List<HashMap<String,String>> datas;

    JPanel panel;
    JTable table;

    @NotNull
    @Override
    public JComponent getComponent() {

        loadData();
        if (panel == null) {

            initPanel();
        }
        return panel;

    }


    private void saveData(){


        String txt = JSON.toJSONString(datas, SerializerFeature.PrettyFormat);

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

    private void loadData() {

        final Document document = FileDocumentManager.getInstance().getDocument(virtualFile);

        String txt = document.getText();//FileUtil.read(virtualFile.getPath());

        datas = new ArrayList<>();

        if( txt != null && txt.length() > 0 ){


            //JSON.parseArray(txt);
            JSONArray array = JSON.parseArray(txt);


            if( array != null)
            for(int i=0;i<array.size();i++){

               HashMap line = new HashMap();
               JSONObject obj = (JSONObject) array.get(i);
               for(MProperty field: mEditor.fields){

                   if( obj.containsKey(field.name) )
                   {
                      line.put(field.name,obj.getString(field.name));

                   }

               }
               datas.add(line);


            }


        }





    }

    HashMap newLine(){

        HashMap map = new HashMap();


        for(MProperty field : mEditor.fields)
        {

            if( "auto".equals(field.args) )
                map.put(field.name, IEAppLoader.getMProject(project).getSequenceStr(project));
            else
                map.put(field.name,"");

        }

        return map;

    }

    private void notifyDataChanged(){

        ((DefaultTableModel)table.getModel()).fireTableDataChanged();

    }

    private void initPanel() {

        panel = new JPanel();
        table = new JTable();

        panel.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();


        ToolbarButton btn_add = new ToolbarButton(AllIcons.ToolbarDecorator.Add);

        btn_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(JComponent component) {

                datas.add(newLine());
                notifyDataChanged();


            }
        });

        ToolbarButton btn_remove = new ToolbarButton(AllIcons.ToolbarDecorator.Remove);

        btn_remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(JComponent component) {

                int row = table.getSelectedRow();
                if( row > -1 ){

                    datas.remove(row);
                    notifyDataChanged();
                    saveData();

                }


                GroovyClassLoader loader = new GroovyClassLoader(getClass().getClassLoader());
                try {
                    Class groovyClass = loader.parseClass(new File("demo/GroovyDemo.groovy"));
                    GroovyObject groovyObj = (GroovyObject) groovyClass.newInstance();
                    groovyObj.invokeMethod("aaa",null);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        toolBar.setFloatable(false);
        toolBar.addSeparator();
        toolBar.add(btn_add);
        toolBar.add(btn_remove);
        toolBar.addSeparator();

        if( mEditor!= null ) {
            JLabel label = new JLabel();
            label.setText(mEditor.descript);
            toolBar.add(label);
        }

        panel.add(toolBar, BorderLayout.NORTH);


        table.setModel(new DefaultTableModel() {
            @Override
            public int getRowCount() {
                int size = datas==null?0:datas.size();

                return size;
            }

            @Override
            public int getColumnCount() {
                return mEditor.getVisibleFieldCount();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {

                return datas.get(rowIndex).get( mEditor.getVisibleField(columnIndex).name );

            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {

                //super.setValueAt(aValue, row, column);
                Log.i("setValueAt %d,%d :%s",row,column,aValue);
                HashMap map = datas.get(row);
                map.put(mEditor.getVisibleField(column).name,aValue);

                saveData();

            }



            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }

            @Override
            public String getColumnName(int column) {
                return mEditor.getVisibleField(column).name;
            }



        });

        table.setRowHeight(40);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {


                int select_col = table.getTableHeader().columnAtPoint(e.getPoint());
                {

                    String name = ((DefaultTableModel)table.getModel()).getColumnName(select_col);
                    datas.sort(new Comparator<HashMap<String,String>>() {
                        @Override
                        public int compare(HashMap<String,String> o1, HashMap<String,String> o2) {


                            return o1.get(name).compareTo(o2.get(name));

                        }
                    });
                    notifyDataChanged();
                    saveData();
                }


            }
        });


    }



    @Override
    public void deselectNotify() {

        virtualFile.refresh(false,false);



    }


}
