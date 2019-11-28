package org.ifelse.model;

public class DartField {

    public String type;
    public String name;

    public DartField(String type, String name) {

        this.type = type;
        this.name = name;
    }
    public DartField(){


    }

    @Override
    public String toString() {
        return String.format("{ type:%s,name:%s }",type,name);
    }
}
