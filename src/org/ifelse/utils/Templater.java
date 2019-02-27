package org.ifelse.utils;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.io.File;

public class Templater {






    public void run(StringBuffer jsp) throws IllegalAccessException, InstantiationException {

        StringBuffer script = new StringBuffer();

        script.append("class TemplaterRun{\n");

        script.append("StringBuffer out = new StringBuffer();\n");

        script.append(" void rprint(String str){\n");

        script.append(" out.append(str);\n");

        script.append(" }\n");


        script.append(" void run(){\n");

        script.append(jsp);

        script.append(" }");

        script.append("}");



        ClassLoader parent = GroovyUtil.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);
        Class groovyClass = loader.parseClass(script.toString());

        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        groovyObject.invokeMethod("run",null);


        Object out =  groovyObject.getProperty("out");


        Log.i(out.toString());

    }




}
