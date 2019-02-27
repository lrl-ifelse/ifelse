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
package org.ifelse.utils;

import com.intellij.openapi.util.IconLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Icons {


    public static String path_form = "/iedata/icons/form.png";

    public static Icon load(String path) {
        return IconLoader.getIcon(path, Icons.class);
    }

    public static final Icon icon_logo = load("/icons/logo.png");
    public static final Icon icon_vars = load("/icons/vars.png");


    public static Image loadResImage(String path){

        try {
            return ImageIO.read( Icons.class.getResourceAsStream(path) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }

    public static Image loadImage(String path){

        try {
            return ImageIO.read(new File( path ) );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }


    public static final Image image_doc = loadResImage("/icons/doc.png");
    public static final Image image_collect = loadResImage("/icons/collect.png");
    public static final Image image_logo36 = loadResImage("/icons/logo_36.png");


}
