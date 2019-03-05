import com.intellij.openapi.project.Project
import org.ifelse.editors.EditorFactory;
import org.ifelse.model.MProperty;
import org.ifelse.vl.VLItem;

import org.ifelse.IEAppLoader;
import org.ifelse.utils.*;

class FormBase{

    Project project;
    
    void run(Map binding){

        String path = project.getBasePath()+"/iedata/template/form.tpl";
        def tpl = new File(path);
        def engine = new groovy.text.GStringTemplateEngine();
        def template = engine.createTemplate(tpl).make(binding);

        def classname = binding.get("classname");

        String dir = project.getBasePath()+"/app/src/main/java/org/ifelse/speakword/forms/";
        String javapath = dir +classname+".java";

        def filedir = new File(dir);
        if( !filedir.exists() )
            filedir.mkdirs();

        def file = new File(javapath);

        if (!file.exists()) {

            def printWriter = file.newPrintWriter() //
            printWriter.write(template.toString())
            printWriter.flush()
            printWriter.close()

            log("create " + javapath);


            log("");

            log( '<activity')
            log( 'android:name="org.ifelse.speakword.forms.'+classname+'"')
            log( 'android:configChanges="keyboard|keyboardHidden|orientation|locale|screenSize"')
            log( 'android:launchMode="singleTask"')
            log( 'android:screenOrientation="portrait"')
            log( '/>')

            log("");
            
        }
        else
            log( "exist "+ javapath);



    }

    void log(String msg){

        GUI.println(project,msg);

    }

}