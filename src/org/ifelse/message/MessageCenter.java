package org.ifelse.message;

import com.intellij.ide.IdeEventQueue;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.impl.ApplicationImpl;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.WeakList;
import org.ifelse.utils.Log;

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

    public static void unregister(Project project, IMessage msgListener){

        String key = project.getName();

        WeakList<IMessage> list = null;

        if( stacks.containsKey(key) ){
            list = stacks.get(key);
        }
        else{
            list = new WeakList<>();
            stacks.put(key,list);
        }

        if( list.contains(msgListener) )
            list.remove(msgListener);

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


    public static void sendMsg(Project project,MsgEvent event, Object value,long delay){

        new Thread(){

            @Override
            public void run() {

                try {
                    sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendMsg(project,event,value);

            }
        }.start();

    }

    public static void sendMsg(Project project,MsgEvent event, Object value){

        String key = project.getName();
        if( stacks.containsKey(key) ){
            WeakList<IMessage> list = stacks.get(key);
            for(IMessage im : list) {

                Log.i("Editor Imessage:%s",im);

                if( im.onMessage(project, event, value) )
                    break;
            }
        }



//        IdeEventQueue.getInstance().doWhenReady(new Runnable() {
//
//            @Override
//            public void run() {
//
//
//
//            }
//        });



    }



}
