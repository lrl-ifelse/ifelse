package org.ifelse.actions;


import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class ActionBind extends AnAction {


    private static final Logger LOG = Logger.getInstance(ActionBind.class);
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {






        Project project = anActionEvent.getProject();


        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        PsiFile pfile = anActionEvent.getData(PlatformDataKeys.PSI_FILE);



        String pfilepath = pfile.getVirtualFile().getPath();



        String path_xml;

        Log.console(project, "select file :%s" , pfilepath);

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
            domParse(path_xml,sb);
            String codes = sb.toString();


            //Messages.showMessageDialog(project, codes, "提示", Messages.getInformationIcon());

            // log(project,codes);

            MsgDialog.showMessage("Bind View",codes);


        } catch (Exception e) {


            MsgDialog.showMessage("Error",e.toString() );


        }


    }

    private void domParse(String path,StringBuffer sb) throws IOException, SAXException {


        DOMParser parser = new DOMParser();
        parser.parse(path);
        Document document = parser.getDocument();
        Element rootElement = document.getDocumentElement();

        //traverse child elements
        NodeList nodes = rootElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            parseNode(node,sb);

        }

    }
    private void parseNode(Node node,StringBuffer sb){


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

                sb.append("@Bind(R.id.").append(rid).append(")\n");
                sb.append(classname).append(" ").append(rid).append(";\n\n");

            }

            NodeList nodes =  element.getChildNodes();
            if( nodes != null && nodes.getLength() > 0 )
            {

                for(int i=0;i<nodes.getLength();i++)
                    parseNode(nodes.item(i),sb);


            }


        }


    }


    private String parse(String path) throws IOException, XmlPullParserException {

        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();



        FileInputStream fis = new FileInputStream(path);

        parser.setInput( fis ,"utf-8");


        StringBuffer sb = new StringBuffer();

        int eventType ;
        String tagname;

        while( (eventType = parser.getEventType()) != XmlPullParser.END_DOCUMENT ) {

            tagname = parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG: {

                    int index = getKey(parser,"android:id");


                    if( index > -1 ) {


                        String classname;
                        int cindex =  tagname.lastIndexOf('.');
                        if( cindex > -1 )
                            classname = tagname.substring(cindex+1);
                        else
                            classname = tagname;

                        String v = parser.getAttributeValue(index);

                        String rid = v.substring(v.lastIndexOf('/')+1);

                        sb.append(String.format( "@Bind(R.id.%s)\n%s %s;\n",rid,classname,rid) );

                    }


                }
            }

            parser.next();

        }




        fis.close();

        return sb.toString();

    }

    private int getKey(XmlPullParser parser,String key){


        for(int i=0;i<parser.getAttributeCount();i++){
            if( parser.getAttributeName(i) .equals(key) )
                return i;
        }
        return -1;

    }

    private void log(Project project,String msg){


        ConsoleView vConsole= TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        vConsole.print("---",ConsoleViewContentType.ERROR_OUTPUT);
        vConsole.print(msg, ConsoleViewContentType.NORMAL_OUTPUT);

    }

    public void update(AnActionEvent e) {


    }



}

