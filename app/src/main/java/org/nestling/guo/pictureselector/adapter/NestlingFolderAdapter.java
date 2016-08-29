package org.nestling.guo.pictureselector.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.nestling.guo.pictureselector.R;
import org.nestling.guo.pictureselector.bean.PicFolder;
import org.nestling.guo.pictureselector.view.NestlingFolderItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guo on 2016/8/23.
 */
public class NestlingFolderAdapter extends ArrayAdapter<PicFolder> {

    private List<PicFolder> data;
    private Context context;
    private int layoutId;
    public NestlingFolderAdapter(Context context, int resource) {
        super(context, resource);
        this.layoutId = resource;
        this.context = context;
    }

    public NestlingFolderAdapter(Context context, int resource, List<PicFolder> objects) {
        super(context, resource, objects);
        this.layoutId = resource;
        this.data = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(this.data != null)
            return this.data.size() + 1;
        return 0;
    }

    @Override
    public PicFolder getItem(int position) {
        if(this.data != null)
            return this.data.get(position  - 1);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0){
            PicFolder picFolder = getItem(1);
            if(picFolder != null)
                if(picFolder.getCover() != null){
                    return initView(parent,"所有文件", getAllCount(), picFolder.getCover(), getSelectedPosition() == 0);
                }
        }
        PicFolder pf = getItem(position);
        if(pf != null) {
            if(pf.getCover() != null){
                return initView(parent, pf.getName(), pf.getNumber(), pf.getCover(), pf.isSelected());
            }
        }
        return null;
    }

    private View initView(ViewGroup parent, String fname, int num, Bitmap cover, boolean selected){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(layoutId, parent, false);
        TextView name = (TextView)view.findViewById(R.id.folder_name);
        ImageView icon = (ImageView)view.findViewById(R.id.folder_icon);
        TextView number = (TextView)view.findViewById(R.id.image_num);
        ImageView select = (ImageView)view.findViewById(R.id.selected);
        name.setText(fname);
        number.setText(num + " 张");
        if (cover != null)
            icon.setImageBitmap(cover);
        if(selected)
            select.setVisibility(View.VISIBLE);
        else
            select.setVisibility(View.INVISIBLE);
        return view;
    }


    private int getAllCount(){
        int count = 0;
        for(PicFolder pf : this.data) {
            count += pf.getNumber();
        }
        return count;
    }

    private int getSelectedPosition(){
        for(int i = 0; i < this.data.size(); i++){
            if(this.data.get(i).isSelected())
                return i + 1;
        }
        return 0;
    }

}
