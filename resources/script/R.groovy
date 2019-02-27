package script

import com.intellij.openapi.project.Project
import org.ifelse.editors.EditorFactory;
import org.ifelse.model.MProperty;
import org.ifelse.IEAppLoader

class R {


    Project project;

    String getSequence(Project project){

        return IEAppLoader.getMProject(project).getSequenceStr();

    }

    void open_form(List<MProperty> properies){

        def path = project.getBasePath()+formname+".ie";

        EditorFactory.open(project,path);

    }

    void open_flow(List<MProperty> properies){

        def path = project.getBasePath()+"/iedata/flows/"+getProperty(properies,"name")+".ie";

        EditorFactory.open(project,path);


    }

    String getProperty(List<MProperty> properies,String key){



        for(int i=0;i<properies.size();i++)
        {
            if( properies.get(i).key.equals(key) )
            {

                return properies.get(i).value;

            }


        }
        return "";


    }




    void showMsg(){

        print("is me from java");
    }


}
