package org.nestling.guo.pictureselector.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by guo on 2016/8/24.
 */
public class PicItem {

    private final String name;
    private final String path;
    private final long size;
    private boolean selected;
    private int width, height;

    private Bitmap img;

    public PicItem(String name, String path){
        this.name = name;
        this.path = path;
        this.size = calcSize();
        this.selected = false;
    }

    private long calcSize(){
        if(path != null){
            File file = new File(this.path);
            if(file.exists())
                return file.length();
        }
        return -1;
    }

    public long getSize(){
        return size;
    }

    /**
     * load bitmap without compress
     * @return
     */
    private Bitmap loadBitmap(){
        if(this.path != null && this.size > 0){
            Bitmap bitmap = BitmapFactory.decodeFile(this.path);
            return bitmap;
        }
        return null;
    }

    /**
     * compress the picture and return bitmap
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap compressPicture(String path, int width, int height){
        this.width = width;
        this.height = height;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
        this.img = bitmap;
        return bitmap;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    /**
     * get the img with the width and height
     * @param width
     * @param height
     * @return
     */
    public Bitmap getImg(int width, int height){
        if(this.img != null && this.width == width && this.height == height)
            return this.img;
        if(this.path != null && this.size > 0)
            return compressPicture(this.path, width, height);
        return null;
    }

    /**
     * get the img without compress
     * @return
     */
    public Bitmap getImg(){
        if(this.img != null && this.width == 0 && this.height == 0)
            return this.img;
        else {
            this.width = 0;
            this.height = 0;
            return loadBitmap();
        }
    }

    /**
     * if the memory is not enough,
     * we can release the img manually.
     * @return
     */
    public boolean release(){
        if(img != null)
            this.img = null;
        return true;
    }

    public Bitmap cache(){
        return this.img;
    }

    public void selecte(){
        this.selected = true;
    }

    public void cancel(){
        this.selected = false;
    }

    public boolean isSelected(){
        return this.selected;
    }

    public String getPath(){
        return this.path;
    }


}
