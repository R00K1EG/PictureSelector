package org.nestling.guo.pictureselector.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.nestling.guo.pictureselector.PictureSelectorActivity;
import org.nestling.guo.pictureselector.R;
import org.nestling.guo.pictureselector.bean.PicFolder;
import org.nestling.guo.pictureselector.bean.PicItem;
import org.nestling.guo.pictureselector.view.SquareImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guo on 2016/8/24.
 */
public class NestlingPictureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<PicItem> items;
    private LayoutInflater mLayoutInflater = null;
    private Context mContxt;
    private String mFolderPath = null;
    private boolean showCamera;

    private static enum TYPE {
        TYPE_CAMERA,
        TYPE_PICTURE
    }

    public NestlingPictureAdapter(Context context, boolean showCamera, ArrayList<PicItem> data){
        mContxt = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.items = data;
        this.showCamera = showCamera;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PictureViewHolder) {
            if(showCamera)
                --position;
            ((PictureViewHolder) holder).position = position;
            PicItem picItem = items.get(position);

            if (picItem.cache() == null) {
                ((PictureViewHolder) holder).picture.setImageResource(R.drawable.loading);
                new LoadImageAsyncTask(((PictureViewHolder) holder).picture).execute(picItem);
            } else {
                ((PictureViewHolder) holder).picture.setImageBitmap(picItem.cache());
            }
            if (picItem.isSelected()) {
                ((PictureViewHolder) holder).select.setImageResource(R.drawable.square_checked);
                ((PictureViewHolder) holder).picture.setColorFilter(Color.parseColor("#77000000"));
            } else {
                ((PictureViewHolder) holder).select.setImageResource(R.drawable.square_unchecked);
                ((PictureViewHolder) holder).picture.setColorFilter(null);
            }
        }
        if(holder instanceof CameraViewHolder){

        }
    }

    class LoadImageAsyncTask extends AsyncTask<PicItem, Void, Bitmap> {

        private ImageView view = null;

        public LoadImageAsyncTask(ImageView imageView){
            view = imageView;
        }

        @Override
        protected Bitmap doInBackground(PicItem... params) {
            return params[0].getImg(400, 400);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            this.view.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(showCamera)
            return position == 0 ? TYPE.TYPE_CAMERA.ordinal() : TYPE.TYPE_PICTURE.ordinal();
        return TYPE.TYPE_PICTURE.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE.TYPE_CAMERA.ordinal())
            return new CameraViewHolder(mLayoutInflater.inflate(R.layout.pic_camera_item, parent,false));
        return new PictureViewHolder(mLayoutInflater.inflate(R.layout.pic_item, parent,false));
    }

    @Override
    public int getItemCount() {
        if(showCamera)
            return items == null ? 1 : items.size() + 1;
        return items == null ? 0 : items.size();
    }

    /**
     * camera view holder;
     */
    class CameraViewHolder extends RecyclerView.ViewHolder{

        SquareImageView camera;

        public CameraViewHolder(View itemView) {
            super(itemView);
            camera = (SquareImageView)itemView.findViewById(R.id.camera);
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PictureSelectorActivity psa = (PictureSelectorActivity)mContxt;
                    psa.selectPictureFromCamera();
                }
            });
        }
    }

    /**
     * nomarl view
     */
    class PictureViewHolder extends RecyclerView.ViewHolder{
        SquareImageView picture = null;
        ImageView select = null;
        int position = 0;
        public PictureViewHolder(final View itemView) {
            super(itemView);
            this.picture = (SquareImageView)itemView.findViewById(R.id.image);
            this.select = (ImageView)itemView.findViewById(R.id.picture_select);
            picture.setColorFilter(null);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PicItem pi = items.get(position);
                    if(pi.isSelected()) {
                        select.setImageResource(R.drawable.square_unchecked);
                        picture.setColorFilter(null);
                        pi.cancel();
                        PictureSelectorActivity psa = (PictureSelectorActivity)mContxt;
                        psa.removeSelectedPic(pi);
                    }else{
                        PictureSelectorActivity psa = (PictureSelectorActivity)mContxt;
                        psa.removeSelectedPic(pi);
                        if(psa.addSelectedPic(pi)) {
                            select.setImageResource(R.drawable.square_checked);
                            picture.setColorFilter(Color.parseColor("#77000000"));
                            pi.selecte();
                        }
                    }
                }
            });
        }
    }
}
