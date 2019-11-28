package org.ifelse.model;

import java.io.File;
import java.util.LinkedHashMap;

public class DartClass {


    public LinkedHashMap<String,DartField> fields = new LinkedHashMap<>();
    public LinkedHashMap<String,DartFunction> functions = new LinkedHashMap<>();

    public String class_annotation;

    public String name;
    public int line_end;

    public DartClass(String classname) {

        name = classname;
    }
    public DartClass(){



    }
    @Override
    public String toString() {
        return name+" field size:"+fields.size();
    }


}
