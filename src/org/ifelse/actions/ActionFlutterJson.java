package org.ifelse.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import org.ifelse.model.DartClass;
import org.ifelse.model.DartField;
import org.ifelse.model.DartFunction;
import org.ifelse.utils.DartParser;
import org.ifelse.utils.Log;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ActionFlutterJson extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {


        FileEditor editor = FileEditorManager.getInstance(anActionEvent.getProject()).getSelectedEditor();
        if (editor != null) {


            String filepath = editor.getFile().getPath();
            if (filepath.endsWith(".dart")) {

                Log.console(anActionEvent.getProject(), "struce:%s", editor.getStructureViewBuilder());

                final Document document = FileDocumentManager.getInstance().getDocument(editor.getFile());


                DartParser parser = new DartParser();


                final List<DartClass> darts = new ArrayList<>();

                parser.parse(document, new DartParser.ParseListener() {


                    DartClass dartClass;

                    @Override
                    public void onType(int type, Object value) {


                        switch (type) {




                            case DartParser.T_CLASS_START: {
                                dartClass = new DartClass();
                                dartClass.name = value.toString();
                                darts.add(dartClass);
                                Log.console(anActionEvent.getProject(),"T_CLASS_START");
                            }
                            break;
                            case DartParser.T_FIELD: {
                                DartField field = (DartField) value;
                                dartClass.fields.put(field.name, field);
                                Log.console(anActionEvent.getProject(),"T_FIELD %s",field);
                            }
                            break;
                            case DartParser.T_FINISH: {
                                Log.console(anActionEvent.getProject(),"T_FINISH");
                            }
                            break;
                            case DartParser.T_CLASS_END: {
                                dartClass.line_end = (int) value;
                                Log.console(anActionEvent.getProject(), "T_CLASS_END %d", dartClass.line_end);
                            }
                                break;
                            case DartParser.T_FUNCTION: {


                                DartFunction fun = (DartFunction) value;
                                Log.console(anActionEvent.getProject(), "T_FUNCTION %s", value);
                                dartClass.functions.put(fun.name,fun);


                            }
                                break;


                        }

                    }
                });


                WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), new Runnable() {
                    @Override
                    public void run() {

                        for (int i = darts.size() - 1; i > -1; i--) {

                            DartClass dartClass = darts.get(i);

                            int offset = document.getLineStartOffset(dartClass.line_end);

                            document.insertString( offset, fromJson(dartClass));

                            document.insertString( offset, toJson(dartClass.fields));


                            DartFunction f_from=null,f_to=null;

                            if( dartClass.functions.containsKey("fromJson") ){
                                f_from = dartClass.functions.get("fromJson");
                            }
                            if( dartClass.functions.containsKey("toJson") ){
                                f_to = dartClass.functions.get("toJson");
                            }


                            if( f_from != null && f_to != null ){
                                if( f_from.line_start > f_to.line_start ){
                                    delete(document,f_from.line_start,f_from.line_end);
                                    delete(document,f_to.line_start,f_to.line_end);
                                }
                                else{
                                    delete(document,f_to.line_start,f_to.line_end);
                                    delete(document,f_from.line_start,f_from.line_end);
                                }
                            }else if( f_from != null ){
                                delete(document,f_from.line_start,f_from.line_end);
                            }else if( f_to != null ){
                                delete(document,f_to.line_start,f_to.line_end);
                            }





                        }


                    }
                });


            }



        }


    }

    void delete(Document doc,int line_s,int line_e){

            int from = doc.getLineStartOffset(line_s);
            int end = doc.getLineEndOffset(line_e);
            doc.deleteString(from,end);

    }


    private String fromJson(DartClass dartClass) {

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append('\t').append(dartClass.name).append(".fromJson(Map<String, dynamic> json)\n");
        stringBuffer.append("\t:");

        for (DartField field : dartClass.fields.values()) {
            stringBuffer.append("\t\t").append(field.name).append(" = json['").append(field.name).append("'],\n");
        }

        stringBuffer.delete(stringBuffer.length() - 2, stringBuffer.length());
        stringBuffer.append(";\n");

        return stringBuffer.toString();

    }


    private String toJson(LinkedHashMap<String, DartField> fields) {

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("\tMap<String, dynamic> toJson() =>\n");
        stringBuffer.append("\t{\n");
        for (DartField field : fields.values()) {

            stringBuffer.append("\t\t'").append(field.name).append("':").append(field.name).append(",\n");

        }
        stringBuffer.append("\t};\n\n");


        return stringBuffer.toString();

    }
}
