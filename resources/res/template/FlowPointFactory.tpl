package org.ifelse.vldata;

import org.ifelse.vl.FlowPoint;

public class FlowPointFactory{

    public static FlowPoint createFlowPoint(int pid){

        switch(pid){
<%

    for(MFlowPointGroup group : groups)
    {

       for(MFlowPoint point : group.points)
       {

if( point.classz != null )
out.println '           case '+point.id +' : return new '+point.classz+'();//'+point.name;


       }

    }

%>
      }
        return null;
    }




}

