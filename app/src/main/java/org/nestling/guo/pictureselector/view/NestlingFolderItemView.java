package org.nestling.guo.pictureselector.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.nestling.guo.pictureselector.R;

import java.io.File;

/**
 * Created by guo on 2016/8/23.
 */
public class NestlingFolderItemView extends FrameLayout {

    private String name;
    private String icon;
    private int number;
    private TextView folderName;
    private TextView folderNum;
    private ImageView folderIcon, selectIcon;
    private Bitmap image;
    private boolean selected = false;
    public NestlingFolderItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater =  LayoutInflater.from(context);
        inflater.inflate(R.layout.folder_item,this);
        folderIcon = (ImageView)this.findViewById(R.id.folder_icon);
        folderName = (TextView)this.findViewById(R.id.folder_name);
        folderNum = (TextView)this.findViewById(R.id.image_num);
        selectIcon = (ImageView)this.findViewById(R.id.selected);
        if(!selected)
            selectIcon.setVisibility(INVISIBLE);
    }

    public NestlingFolderItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if(name != null)
            this.folderName.setText(name+"");
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
        if(icon != null && image == null){
            File file = new File(icon);
            if(file.exists()){
                image = BitmapFactory.decodeFile(icon);
                this.folderIcon.setImageBitmap(image);
            }
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        if(number > 0){
            this.folderNum.setText(number + " 张");
        }
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        if(selected)
            selectIcon.setVisibility(VISIBLE);
        else
            selectIcon.setVisibility(INVISIBLE);


    }
}
