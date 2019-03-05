package org.ifelse.model;

public class MVar {

    public String key;
    public String name;
    public String flow;
    public String descript;
    public int count;

    public MVar(String k){
        this.key = k;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj instanceof MVar ) {
            return key.equals(((MVar)obj).key);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return String.format("{key:%s,name:%s,flow:%s}",key,name,flow);
    }
}
