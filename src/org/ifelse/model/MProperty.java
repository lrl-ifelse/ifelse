package org.ifelse.model;

import java.io.Serializable;

public class MProperty implements Serializable {

    public String key;
    public String name;
    public String args;
    public String value;
    public boolean visible;
    public String descript;


    public MProperty(){

        visible = true;

    }
    public MProperty(String key){

        visible = true;
        this.key = key;
        this.name = key;


    }
    public MProperty(String key,String name,String value){

        visible = true;
        this.key = key;
        this.name = name;
        this.value = value;


    }

    public MProperty clone(){


        MProperty mp = new MProperty();
        mp.name = name;
        mp.key = key;
        mp.args = args;
        mp.value = value;
        mp.visible = visible;
        mp.descript = descript;

        return mp;


    }
}
