
/*
 * Copyright 1999-2019 fclassroom Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ifelse.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.jetbrains.jsonProtocol.JsonField;

import java.util.List;

public class MEditor {

    @JSONField(serialize = false)
    public static final String TABLE = "TABLE";
    @JSONField(serialize = false)
    public static final String FLOW = "FLOW";


    public String icon;

    public String name;

    public String descript;

    public String type;// FINAL TABLE .. FLOW.. MANAGER

    public List<MProperty> fields;

    public String defPoint;





    @JSONField(serialize = false)
    @Override
    public String toString() {
        return name;
    }

    @JSONField(serialize = false)
    public int getVisibleFieldCount() {

        int count = 0;
        for (MProperty field : fields)
            if (field.visible)
                count++;
        return count;

    }


    public MProperty getVisibleField(int index) {

        int i = 0;
        for (MProperty field : fields){
            if( field.visible ){

                if( i == index )
                    return field;
                else
                    i++;

            }

        }
        return null;

    }
}
