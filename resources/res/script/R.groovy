package script

import com.intellij.openapi.project.Project
import org.ifelse.editors.EditorFactory;
import org.ifelse.model.MProperty;
import org.ifelse.IEAppLoader
import org.ifelse.vl.*;
import org.ifelse.utils.*;
import org.ifelse.model.*;
import com.alibaba.fastjson.JSON;

class R {


    Project project;
    VLPoint vlPoint;
    MFlowPoint mFlowPoint;

    String getSequence(Project project){

        return IEAppLoader.getMProject(project).getSequenceStr();

    }

    void open_form(){
        

        List<MProperty> properies = vlPoint.mproperties;

        def formname = getProperty(properies,"classname");
        def path = project.getBasePath()+"/app/src/main/java/org/ifelse/speakword/forms/"+formname+".java";

        GUI.println(project,"/script/R.groovy.open_form");
        GUI.println(project,"open:"+path);

        File file = new File(path);

        if( !file.exists() )
        {
            GUI.println(project,"设置模版后点击运行生成页面代码");
        }else
        {
            EditorFactory.open(project,path);
        }

    }
    void open_point(){


        String classname = mFlowPoint.classz;

        def path = project.getBasePath()+"/app/src/main/java/org/ifelse/points/"+classname.substring( classname.lastIndexOf('.') + 1)+".java";

        GUI.println(project,"/script/R.groovy.open_point");
        GUI.println(project,"open:"+path);

        EditorFactory.open(project,path);


    }

    void open_flow(){

        List<MProperty> properies = vlPoint.mproperties;


        def path = project.getBasePath()+"/iedata/flows/"+getProperty(properies,"name")+".ie";

        GUI.println(project,"/script/R.groovy.open_flow");
        GUI.println(project,"open:"+path);

        File file = new File(path);

        if( !file.exists() )
        {

            StringBuffer lines = new StringBuffer();

            lines.append("[");

            VLPoint point = new VLPoint();
            point.id = 1;
            point.flow_point_id = "100101";
            point.mproperties = new ArrayList();

            MProperty mp_args = vlPoint.getMProperty("event");
            point.mproperties.add(mp_args.clone());
            point.mproperties.add(new MProperty("value"));
            point.mproperties.add(new MProperty("descript","descript","起始点"));

            point.x = 50;
            point.y = 50;


            lines.append( JSON.toJSONString(point) );


            lines.append("]");

            FileUtil.save(lines.toString(),path);

        }


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



}
