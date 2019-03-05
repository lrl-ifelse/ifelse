import com.intellij.openapi.project.Project
import org.ifelse.editors.EditorFactory;
import org.ifelse.model.MProperty;
import org.ifelse.vl.VLItem;

import org.ifelse.IEAppLoader;
import org.ifelse.utils.TemplateUtil;

class FormBase{

    Project project;
    Map binding;


    void run(){

        String path = project.getBasePath()+"/iedata/template/form.tpl";
        def tpl = new File(path);
        def engine = new groovy.text.GStringTemplateEngine();
        def template = engine.createTemplate(tpl).make(binding);
        println template.toString()



    }


}