package org.ifelse.actions;


import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.apache.xerces.parsers.DOMParser;
import org.ifelse.ui.MsgDialog;
import org.ifelse.utils.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

public class ActionFind extends AnAction {


    private static final Logger LOG = Logger.getInstance(ActionFind.class);
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {






        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);



        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        PsiFile pfile = anActionEvent.getData(PlatformDataKeys.PSI_FILE);



        String pfilepath = pfile.getVirtualFile().getPath();


        Log.console(project, "select file :%s" , pfilepath);

        String path_xml;

        if( pfilepath.indexOf("layout")>-1 && pfilepath.endsWith(".xml") )
        {

            path_xml = pfilepath;
        }
        else {


            int ps = editor.getSelectionModel().getBlockSelectionStarts()[0];
            int pe = editor.getSelectionModel().getBlockSelectionEnds()[0];

            String sel = editor.getSelectionModel().getSelectedText();

            if (sel == null || sel.length() == 0) {

                String text = editor.getDocument().getText();

                int pos = ps;
                StringBuffer strs = new StringBuffer();
                while (pos > 0) {

                    char ch = text.charAt(pos--);

                    if (!Character.isWhitespace(ch) && ch != '.')
                        strs.insert(0, ch);
                    else
                        break;
                }

                pos = pe + 1;

                while (pos > 0 && pos < text.length()) {

                    char ch = text.charAt(pos++);

                    if (!Character.isWhitespace(ch) && ch != '.' && ch != ':' && ch != ';' && ch != ',' && ch != '(' && ch != ')')
                        strs.append(ch);
                    else
                        break;
                }
                sel = strs.toString().trim();

            }


            path_xml = project.getBasePath() + "/app/src/main/res/layout/" + sel + ".xml";




        }


        try {
            //String codes = parse(path_xml);

            StringBuffer sb = new StringBuffer();
            StringBuffer fieldsb = new StringBuffer();

            domParse(path_xml,sb,fieldsb);
            String codes = fieldsb.append("\n\n\n").append(sb).toString();


            MsgDialog.showMessage("Find View",codes);


        } catch (Exception e) {


            MsgDialog.showMessage( "Error",e.toString() );



        }


    }

    private void domParse(String path,StringBuffer sb,StringBuffer fieldsb) throws IOException, SAXException {


        DOMParser parser = new DOMParser();
        parser.parse(path);
        Document document = parser.getDocument();
        Element rootElement = document.getDocumentElement();

        //traverse child elements
        NodeList nodes = rootElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            parseNode(node,sb,fieldsb);

        }

    }
    private void parseNode(Node node,StringBuffer sb,StringBuffer fieldsb){


        if( node.getNodeType() == Node.ELEMENT_NODE ){

            Element element = (Element) node;


            String tagname = element.getTagName();
            String classname;
            int cindex =  tagname.lastIndexOf('.');
            if( cindex > -1 )
                classname = tagname.substring(cindex+1);
            else
                classname = tagname;


            String id = element.getAttribute("android:id");
            String rid = id.substring(id.lastIndexOf('/')+1);
            if(rid != null && rid.length() > 0 ){

                fieldsb.append(classname).append(" ").append(rid).append(";\n");
                sb.append(rid).append(" = (").append(classname).append(")itemView.findViewById(R.id.").append(rid).append(");\n");

                //sb.append("@Bind(R.id.").append(rid).append(")\n");
                //sb.append(classname).append(" ").append(rid).append(";\n\n");

            }

            NodeList nodes =  element.getChildNodes();
            if( nodes != null && nodes.getLength() > 0 )
            {

                for(int i=0;i<nodes.getLength();i++)
                    parseNode(nodes.item(i),sb,fieldsb);


            }


        }


    }






}
