package org.ifelse.vldata;

import org.ifelse.speakword.forms.*;
import org.ifelse.vldata.*;

public class FormFactory{

    public static Class getFormClass(Event event){

        switch(event){

<%
        for(VLItem item : forms)
        {

            if( !item.isLine() )
            {
                def eid = item.getMProperty("event").value;
                def ename = events.get(eid).get("name");
                def descript = item.getMProperty("descript").value;
                def classname = item.getMProperty("classname").value;
                out.println('           case ' + ename + ' : return ' + classname +'.class;//' + descript );
            }
        }
%>
        }
        return null;
    }




}

