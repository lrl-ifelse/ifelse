import com.intellij.openapi.project.Project
import org.ifelse.editors.EditorFactory;
import org.ifelse.model.*;
import org.ifelse.vl.*;

import org.ifelse.IEAppLoader;
import org.ifelse.utils.TemplateUtil;
import org.ifelse.utils.GroovyUtil;
import org.ifelse.utils.*;
import groovy.text.VLTemplateEngine;


import java.time.temporal.TemporalUnit;

class Run {


    Project project;


    void run(){

        log("---------------------------------------------------------")

        String path = project.getBasePath()+"/iedata/Event.ie";
        List<Map> events = TemplateUtil.parseTable(path);
        Map<String,Map> eventmap = new HashMap();
        for(Map map : events){

            eventmap.put(map.get("id"),map);

        }





        runEvents(events);

        log("输出页面：");

        runForms(eventmap);

        log("输出节点：");
        runFlowPoints();

        log("输出流程：");

        runFlows(eventmap);

        log("---------------------------------------------------------")

    }

    void runFlowPoints(){

        String path = project.getBasePath()+"/iedata/points.json";
        List<MFlowPointGroup>  groups = TemplateUtil.parse(MFlowPointGroup.class,path);
        Map binding = new HashMap();
        binding.put("groups",groups);

        String tplpath = project.getBasePath()+"/iedata/template/FlowPointFactory.tpl";

        def tpl = new File(tplpath);
        def engine = new VLTemplateEngine();
        def template = engine.createTemplate(tpl).make(binding);

        String dir = project.getBasePath()+"/app/src/main/java/org/ifelse/vldata/";

        save(dir,"FlowPointFactory.java",template.toString(),true);


    }

    void runFlows(Map<String,Map> events){

        String path = project.getBasePath()+"/iedata/Flows.ie";
        List<VLItem> list = TemplateUtil.parseFlow(path);

        Map binding = new HashMap();
        binding.put("flows",list);
        binding.put("events",events);

        String tplpath = project.getBasePath()+"/iedata/template/FlowBoxs.tpl";

        def tpl = new File(tplpath);
        def engine = new groovy.text.VLTemplateEngine();

        def template = engine.createTemplate(tpl).make(binding);


        String dir = project.getBasePath()+"/app/src/main/java/org/ifelse/vldata/";

        save(dir,"FlowBoxs.java",template.toString(),true);


        copyFlows(list);

    }

    void copyFlows(List<VLItem> list){


        for(VLItem item : list){

            if( item instanceof  VLPoint){

                VLPoint vp = (VLPoint)item;

                if( vp.flow_point_id.equals("900102") )
                {

                    String from = project.getBasePath()+"/iedata/flows/"+vp.getMProperty("name").value+'.ie';
                    String to = project.getBasePath()+"/app/src/main/res/raw/"+vp.getMProperty("name").value+'.ie';
                    FileUtil.copy(from,to);
                    log('flow->'+from);

                }
            }


        }

    }

    void runEvents(List<Map> list){


        Map binding = new HashMap();
        binding.put("list",list);

        String eventpath = project.getBasePath()+"/iedata/template/Event.tpl";



        def tpl = new File(eventpath);
        def engine = new VLTemplateEngine();
        def template = engine.createTemplate(tpl).make(binding);


        String dir = project.getBasePath()+"/app/src/main/java/org/ifelse/vldata/";
        save(dir,"Event.java",template.toString(),true);

        log( eventpath );
    }

    void runForms(Map<String,Map> events){

        String path = project.getBasePath()+"/iedata/Forms.ie";

        List<VLItem> list = TemplateUtil.parseFlow(path);

        for(VLItem item : list){

            if( item.classname.equals("org.ifelse.vl.VLPoint") )
            {
                createForm(item);

            }

        }



        Map binding = new HashMap();
        binding.put("forms",list);
        binding.put("events",events);

        String tplpath = project.getBasePath()+"/iedata/template/FormFactory.tpl";



        def tpl = new File(tplpath);
        def engine = new VLTemplateEngine();
        def template = engine.createTemplate(tpl).make(binding);


        String dir = project.getBasePath()+"/app/src/main/java/org/ifelse/vldata/";
        save(dir,"FormFactory.java",template.toString(),true);

        log( dir +"FormFactory.java");



    }

    void createForm(VLItem item){

        Map binding = getProperties(item);
        String formpath = project.getBasePath()+"/iedata/template/"+ binding.get("template")+".groovy";
        log( formpath );
        GroovyUtil.run(project,formpath,"run",binding);

    }

    Map getProperties(VLItem item){

        Map map = new HashMap();
        for(MProperty mp:item.mproperties){
            map.put(mp.key,mp.value);
        }
        return map;

    }




    void save(String dir,String filename,String txt,boolean replace){


        def filedir = new File(dir);
        if( !filedir.exists() )
            filedir.mkdirs();

        String srcpath = dir+"/"+filename;

        def file = new File(srcpath);

        if (file.exists() )
        {
            if( replace )
                file.delete()
            else
                return;
        }
        def printWriter = file.newPrintWriter() //
        printWriter.write(txt)
        printWriter.flush()
        printWriter.close()

        log("save ->"+srcpath);

    }


    void log(String msg){

        GUI.println(project,msg);

    }


}