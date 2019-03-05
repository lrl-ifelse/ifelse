package org.ifelse.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IconFactory {


    static Map<String,BufferedImage> images = new HashMap<>();
    public static final Map<RenderingHints.Key, Object> RENDERING_HINTS = new HashMap<RenderingHints.Key, Object>();

    public static void init() {
        RENDERING_HINTS.put(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        RENDERING_HINTS.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        RENDERING_HINTS.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        RENDERING_HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        RENDERING_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }


    public static BufferedImage createImage(String path){

        //Log.i("createImage :%s",path);

        return createImage(path,0,0);

    }
    public static BufferedImage createImage(String path,int width,int height){

        return createImage(path,width,height,0);

    }
    public static BufferedImage createImage(String path,int width,int height,int color){


        int code = path.hashCode();

        String key = code+"_"+width;


        if( images.containsKey(key) ){
            //Log.console("new  image from cache:%s ",path);
            BufferedImage bufferedImage = images.get(key);
            if( bufferedImage != null )
                return bufferedImage;

        }

        {

            try {

                //Log.console("new  image from io:%s ",path);
                BufferedImage image;
                if( width > 0 ) {


                    BufferedImage bi =  javax.imageio.ImageIO.read(new File(path));


                    if( color == 0 )
                        image = scaledImage(bi,width, height, false, RENDERING_HINTS);
                    else
                        image = scaledImage(bi,width, height, false, RENDERING_HINTS,color);
                    images.put(key, image);

                }
                else{
                    image = javax.imageio.ImageIO.read(new File(path));
                    images.put(key,image );
                }
                return image;
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        Log.i("not load image:%s",path);
        return null;

    }


    public static BufferedImage scaledImage(BufferedImage img, int targetWidth, int targetHeight, boolean higherQuality, Map renderingHints,int color) {
        int width = img.getWidth();
        int height = img.getHeight();

        int draw_w = targetWidth;
        int draw_h = targetHeight;

        //int v_w,v_h;

        if (width != height) {
            if (width > height) {
                targetHeight = (int) (((double) targetWidth * (double) height) / (double) width);
            } else {
                targetWidth = (int) (((double) targetHeight * (double) width) / (double) height);
            }
        }
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledImg = img;
        int w, h;
        if (higherQuality) {
            w = img.getWidth();
            h = img.getHeight();
        } else {
            w = targetWidth;
            h = targetHeight;
        }
        do {
            if (w < targetWidth && h < targetHeight) {
                break;
            }
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }
            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }
            BufferedImage tmp = new BufferedImage(draw_w, draw_h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHints(renderingHints);
            g2.setColor(Color.BLUE);
            g2.drawRoundRect(1,1,draw_w-2,draw_h-2,4,4);

            g2.drawImage(scaledImg, (draw_w-targetWidth)/2, (draw_h-targetHeight)/2, w, h, null);
            g2.dispose();
            scaledImg = tmp;
        } while (w != targetWidth || h != targetHeight);
        return scaledImg;
    }


    public static BufferedImage scaledImage(BufferedImage img, int targetWidth, int targetHeight, boolean higherQuality, Map renderingHints) {
        int width = img.getWidth();
        int height = img.getHeight();
        if (width != height) {
            if (width > height) {
                targetHeight = (int) (((double) targetWidth * (double) height) / (double) width);
            } else {
                targetWidth = (int) (((double) targetHeight * (double) width) / (double) height);
            }
        }
        int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledImg = img;
        int w, h;
        if (higherQuality) {
            w = img.getWidth();
            h = img.getHeight();
        } else {
            w = targetWidth;
            h = targetHeight;
        }
        do {
            if (w < targetWidth && h < targetHeight) {
                break;
            }
            if (higherQuality && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }
            if (higherQuality && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }
            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHints(renderingHints);
            g2.drawImage(scaledImg, 0, 0, w, h, null);
            g2.dispose();
            scaledImg = tmp;
        } while (w != targetWidth || h != targetHeight);
        return scaledImg;
    }
}
