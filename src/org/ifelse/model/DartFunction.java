package org.ifelse.model;

public class DartFunction {

    public String type;
    public String name;
    public int line_start;
    public int line_end;
    public int start_end = -1;

    public DartFunction(String type, String name) {

        this.type = type;
        this.name = name;
    }
    public DartFunction(){


    }

    @Override
    public String toString() {
        return String.format("{ name:%s line(%d,%d) }",name,line_start,line_end);
    }



}
