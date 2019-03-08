package org.ifelse.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {


    public static void unzip(InputStream inputStream, String unzipdir) {
        try {

            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            //BufferedInputStream bin = new BufferedInputStream(zipInputStream);

            File file = null;
            ZipEntry entry;
            int len;


            byte[] bytes = new byte[1024*100];
            try {
                while ( ( entry = zipInputStream.getNextEntry()) != null ) {

                    String name = entry.getName();
                    if( name.charAt(0) == '.' || name.charAt(0) == '_' )
                        continue;

                    if(  entry.isDirectory() )
                    {

                        String path = unzipdir +"/"+ entry.getName();

                        new File(path).mkdirs();


                        Log.i("unzip dir:%s",path);
                        continue;

                    }


                    file = new File(unzipdir, entry.getName());

                    if( !file.exists() ) {
                        file.getParentFile().mkdirs();
                    }

                    Log.i("unzip dir:%s",file.getPath());


                    FileOutputStream fout = new FileOutputStream(file);
                    while ((len = zipInputStream.read(bytes)) != -1) {
                        fout.write(bytes,0,len);
                    }
                    fout.close();


                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {
                    if (zipInputStream != null) {
                        zipInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
