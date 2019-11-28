package org.ifelse.utils;


import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import org.ifelse.model.DartField;
import org.ifelse.model.DartFunction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DartParser {

    public static final int T_CLASS_START = 1;
    public static final int T_CLASS_END = 2;
    public static final int T_FIELD = 3;
    public static final int T_FUNCTION = 4;
    public static final int T_FINISH = 5;

    public static Pattern patter_class = Pattern.compile("class\\s+\\w+(\\s|\\{)");
    public static Pattern pattern_reflect = Pattern.compile("@reflect");
    public static Pattern pattern_field = Pattern.compile("(num|bool|String|int)\\s+\\w+;");
    public static Pattern pattern_fun = Pattern.compile("(\\.|\\s).*\\(.*\\)");



    public static interface ParseListener{


        public void onType(int type,Object value);

    }
    public static DartField getField(String line){


        Matcher matcher = pattern_field.matcher(line);

        if( matcher.find() ){

            String[] fields = matcher.group(0).split("\\s|;");

            DartField field = new DartField( fields[0],fields[1] );

            return field;

        }
        return null;

    }


    public static String getFunName(String line){

        Matcher matcher = pattern_fun.matcher(line);
        if( matcher.find() ){
            String str = matcher.group();
            String[] funs = str.substring(0,str.indexOf('(')).split("<|>|\\.|\\s|\\(|\\)");
            if( funs.length > 0 )
                return funs[funs.length-1];
        }
        return null;

    }


    public static String getAnnotation(String line){

        String txt = line.trim();
        if( txt.length() > 0 && txt.charAt(0) == '@' ){

            return line.trim();
        }
        return null;

    }


    public static String getClass(String line){


        Matcher matcher = patter_class.matcher(line);
        if( matcher.find() ){

            String classstr = matcher.group(0);
            String[] classes = classstr.split("\\s|\\{");
            return classes[1];

        }
        return null;


    }

    public static int count_char(String str,char c){

        int count = 0;
        for(int i=0;i<str.length();i++){

            if( str.charAt(i) == c ){
                count++;
            }

        }
        return count;
    }

    public void parse(Document document,ParseListener listener){


        int left_r=-1;


        String classname = null;
        DartFunction function = null;

        for(int i=0;i<document.getLineCount();i++){

            int l_start = document.getLineStartOffset(i);

            int l_end = document.getLineEndOffset(i);

            String line = document.getText(new TextRange(l_start,l_end));

            if(line.trim().length()==0)
                continue;


            int lcount = count_char(line, '{');

            if( left_r == -1 && lcount > 0  )
            {
                left_r = lcount;
            }
            else
                left_r += lcount;


            left_r -= count_char(line, '}');


            if( function != null ){

                int cleft = count_char(line,'{');
                if( cleft > 0 ){
                    if( function.start_end==-1 )
                        function.start_end = 0;

                }
                function.start_end+= cleft;
                function.start_end -= count_char(line,'}');

                if( function.start_end == 0 ){

                    function.line_end = i;

                    listener.onType(T_FUNCTION,function);
                    function = null;

                }else if( function.start_end == -1 ){

                    if( count_char(line,';') >= 1 ){

                        function.line_end = i;

                        listener.onType(T_FUNCTION,function);
                        function = null;

                    }


                }
                continue;

            }


            if( classname == null) {
                classname = getClass(line);
                if( classname != null ){
                    listener.onType(T_CLASS_START,classname);
                }
            }

            if( left_r == 0 && classname != null ){

                listener.onType(T_CLASS_END,i);
                classname = null;
                left_r=-1;
                continue;

            }

            if( classname != null ){

                DartField field = getField(line);

                if( field != null ){

                    listener.onType(T_FIELD,field);
                    continue;

                }

            }

            if( function == null ){

                String fname = getFunName(line);

                if( fname != null ){

                    function = new DartFunction();
                    function.name = fname;
                    function.line_start = i;

                    int cleft   = count_char(line,'{');
                    if( cleft > 0 ){
                        function.start_end = cleft;
                    }



                }

            }



        }

        listener.onType(T_FINISH,null);



    }

    private void isFunEnd(String line) {




    }


}
