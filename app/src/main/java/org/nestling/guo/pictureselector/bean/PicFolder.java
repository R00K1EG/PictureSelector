package org.nestling.guo.pictureselector.bean;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guo on 2016/8/22.
 */
public class PicFolder {

    private final String name;
    private int number;
    private final String path;
    private PicItem first;

    private List<PicItem> pictures;
    private String[] picDirs;
    private boolean selected;
    public PicFolder(String name, String path){
        this.path = path;
        this.name = name;
        this.pictures = new ArrayList<PicItem>();
        selected = false;
    }

    /**
     * load the pictures in the @PicFolder;
     */
    public boolean loadPictures(){
        File file = new File(this.getPath());
        if(file == null){
            return false;
        }
        if(picDirs != null)
            return true;
        this.picDirs = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if(filename.endsWith("jpg") || filename.endsWith("jpeg") || filename.endsWith("png"))
                    return true;
                return false;
            }
        });
        if(this.picDirs.length == 0)
            return false;
        for(String fname : picDirs){
            this.pictures.add(new PicItem(fname, this.path + "/" + fname));
        }
        if(this.first == null){
            this.first = this.pictures.get(0);
        }
        this.number = this.picDirs.length;
        return true;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<PicItem> getPictures(){
        if(pictures.size() == 0)
            loadPictures();
        return this.pictures;
    }

    public Bitmap getCover(){
        if(this.first != null)
            return this.first.getImg(400, 400);
        return null;
    }

    public boolean isSelected() {
        return selected;
    }

    public void selected() {
        this.selected = true;
    }

    public void cancel(){
        this.selected = false;
    }
}
