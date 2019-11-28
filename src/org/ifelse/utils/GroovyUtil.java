/*
 * Copyright 1999-2019 fclassroom Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ifelse.utils;

import com.intellij.openapi.project.Project;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.Script;
import org.ifelse.model.DartClass;
import org.ifelse.model.DartField;
import org.ifelse.model.MFlowPoint;
import org.ifelse.model.MProperty;
import org.ifelse.vl.VLItem;
import org.ifelse.vl.VLPoint;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GroovyUtil {





    public static void run(Project project,String method) throws IOException, IllegalAccessException, InstantiationException {


        ClassLoader parent = GroovyUtil.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        String path = project.getBasePath()+"/iedata/script/R.groovy";
        Class groovyClass = loader.parseClass(new File(path));

        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        groovyObject.invokeMethod(method, null);


    }


    //回调 R.groovy
    public static void open(Project project, List<MProperty> properies) throws Exception{


        ClassLoader parent = GroovyUtil.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        String path = project.getBasePath()+"/iedata/script/R.groovy";


        Class groovyClass = loader.parseClass(new File(path));

        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        groovyObject.setProperty("project",project);

        groovyObject.invokeMethod("open", properies);




    }

    public static void run(Project project, String filepath, String method, MFlowPoint point) throws Exception {


        ClassLoader parent = GroovyUtil.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        Class groovyClass = loader.parseClass(new File(filepath));


        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        groovyObject.setProperty("project",project);

        groovyObject.invokeMethod(method, point);

    }

    public static void run(Project project, String filepath, String method, List<MProperty> properties) throws Exception {


        ClassLoader parent = GroovyUtil.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        Class groovyClass = loader.parseClass(new File(filepath));


        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        groovyObject.setProperty("project",project);

        groovyObject.invokeMethod(method, properties);

    }

    public static void run(Project project, String filepath, String method, Map properties) throws Exception {


        ClassLoader parent = GroovyUtil.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        Class groovyClass = loader.parseClass(new File(filepath));


        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        groovyObject.setProperty("project",project);

        groovyObject.invokeMethod(method, properties);

    }

    public static void run(Project project, String filepath, String method, DartClass dartClass) throws Exception {


        ClassLoader parent = GroovyUtil.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        Class groovyClass = loader.parseClass(new File(filepath));


        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        groovyObject.setProperty("project",project);

        groovyObject.invokeMethod(method, dartClass);

    }

    public static void run(Project project, String filepath, String method, VLPoint vlPoint, MFlowPoint mFlowPoint) throws Exception {


        ClassLoader parent = GroovyUtil.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        Class groovyClass = loader.parseClass(new File(filepath));


        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        groovyObject.setProperty("project",project);
        groovyObject.setProperty("vlPoint",vlPoint);
        groovyObject.setProperty("mFlowPoint",mFlowPoint);
        //groovyObject.invokeMethod(method, point);
        groovyObject.invokeMethod(method,null);

    }

    public static Object get(Project project, String filepath, String method) throws Exception {


        ClassLoader parent = GroovyUtil.class.getClassLoader();
        GroovyClassLoader loader = new GroovyClassLoader(parent);

        Class groovyClass = loader.parseClass(new File(filepath));

        GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();

        groovyObject.setProperty("project",project);

        //groovyObject.invokeMethod(method, point);
        return groovyObject.invokeMethod(method,null);

    }



}
