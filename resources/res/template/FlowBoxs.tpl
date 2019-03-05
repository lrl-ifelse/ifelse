package org.ifelse.vldata;

import org.ifelse.speakword.R;
import org.ifelse.vl.MFlow;

public class FlowBoxs{

    public static int getBox(Event event){
        switch(event){
<%

    for(VLItem item : flows)
    {
        def eid = item.getMProperty("event").value;
        def ename = events.get(eid).get("name");
        def name = item.getMProperty("name").value;
        def descript = item.getMProperty("descript").value;

        out.print("         case "); out.print( ename ) ;out.print(" : return R.raw.");out.print(name); out.print(";//"); out.println( descript );
    }

%>
      }
        return 0;
    }


    public static MFlow getBoxInfo(Event event) {
        switch(event)
        {
<%

     for(VLItem item : flows)
     {
         def eid = item.getMProperty("event").value;
         def ename = events.get(eid).get("name");
         def name = item.getMProperty("name").value;
         def descript = item.getMProperty("descript").value;

         out.println('           case '+ename+' : return new MFlow('+eid+',"'+descript+'","'+name+'");' );
     }
%>

        }
        return null;
    }

}

