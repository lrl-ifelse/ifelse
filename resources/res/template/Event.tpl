package org.ifelse.vldata;

/* cretae by template/Event.tpl */

public enum Event{


<%
        for(Map map : list)
        {
            out.println '    '+map.get("name") + ',//' + map.get("descript");
        }
%>
    S_NONE;

    public static Event getEvent(int eid){

        switch(eid){

<%
        for(Map map : list)
        {
            out.println '            case ' + map.get("id") +' : return Event.' + map.get("name")+';';

        }
%>
        }
        return Event.S_NONE;


    }

}