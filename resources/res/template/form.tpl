package org.ifelse.speakword.forms;

import android.view.View;
import org.ifelse.speakword.R;
import org.ifelse.vl.Form;
import org.ifelse.vldata.Event;

/*
$descript
*/
public class $classname extends Form{


    @Override
    public void onClick(View view){

        switch (view.getId()){


        }
    }


    @Override
    public boolean onMessage(Event event, Object value) {

        switch (event){


        }
        return false;

    }

    @Override
    public void onStateChanged(FormState fs, Object value) {

        //log("%-12s taskid:%d taskroot:%b", fs, getTaskId(), isTaskRoot());

        switch (fs) {

            case FS_CREATE:

                setContentView(R.layout.);

                break;
        }
    }


}