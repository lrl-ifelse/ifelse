package org.ifelse.message;

import com.intellij.openapi.project.Project;
import com.intellij.util.containers.WeakList;

import java.util.HashMap;
import java.util.List;

public class MessageCenter {

    public static interface IMessage{

        boolean onMessage(Project project,MsgEvent event,Object value);

    }

    static HashMap<String, WeakList<IMessage>> stacks = new HashMap<>();


    public static void unregister(Project project){

        String key = project.getName();
        WeakList list = stacks.remove( key );
        if( list != null )
            list.clear();

    }


    public static void register(Project project, IMessage msgListener){

        String key = project.getName();

        WeakList<IMessage> list = null;

        if( stacks.containsKey(key) ){
            list = stacks.get(key);
        }
        else{
            list = new WeakList<>();
            stacks.put(key,list);
        }

        if( !list.contains(msgListener) )
            list.add(msgListener);


    }

    public static void sendMsg(Project project,MsgEvent event, Object value){

        String key = project.getName();
        if( stacks.containsKey(key) ){
            WeakList<IMessage> list = stacks.get(key);
            for(IMessage im : list) {
                if( im.onMessage(project, event, value) )
                    break;
            }
        }

    }



}
