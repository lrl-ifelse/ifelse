package org.ifelse.utils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by dizhanbin on 18/7/13.
 */

public class ListMap<K,T> implements Serializable {

    Vector<K> keys = new Vector<>();
    Map<K,T> values = new Hashtable<>();

    public boolean contains(K k){

        return values.containsKey(k);

    }
    public void put(K k,T t){

        keys.remove(k);
        values.put(k,t);
        keys.add(k);

    }
    public void put(K k,T t,int pos){

        keys.remove(k);
        values.put(k,t);
        keys.add(pos,k);

    }


    public T get(K k){

        return values.get(k);

    }

    public int indexOf(K k){

        return keys.indexOf(k);

    }

    public T getByIndex(int index){

        if( keys.size() == 0 )
            return null;
        return values.get(keys.elementAt(index));
    }

    public int size(){

        return keys.size();

    }


    public void remove(K k) {

        keys.remove(k);
        values.remove(k);

    }

    public K top() {
        if( keys.size() == 0 )
            return null;
        return keys.elementAt(keys.size()-1);
    }

    public K getKey(int index) {
        return keys.get(index);
    }

    public Vector<K> getKeys() {
        return keys;
    }

    public T remove(int index) {

        K k =  keys.remove(index);
        if( k != null )
            return values.remove(k);
        return null;

    }

    public void clear() {

        keys.clear();
        values.clear();

    }
    public void sort(Comparator<K> comparator)
    {
        Collections.sort(keys,comparator);
    }

    public void addKey(K k) {
        keys.add(k);
    }
    public void addValue(K k,T t){

        values.put(k,t);

    }
}
