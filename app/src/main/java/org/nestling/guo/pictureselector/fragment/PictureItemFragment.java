package org.nestling.guo.pictureselector.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.nestling.guo.pictureselector.R;
import org.nestling.guo.pictureselector.adapter.NestlingPictureAdapter;
import org.nestling.guo.pictureselector.bean.PicFolder;
import org.nestling.guo.pictureselector.bean.PicItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guo on 2016/8/24.
 */
public class PictureItemFragment extends Fragment{

    private String path = null;
    private RecyclerView picItemRV = null;
    private NestlingPictureAdapter myPicItemAdapter;
    private ArrayList<PicItem> items;

    public PictureItemFragment(){
        super();
        this.items = new ArrayList<>();
    }

    public void Refresh(){
        this.myPicItemAdapter.notifyDataSetChanged();
    }

    public void update(List<PicItem> lists){
        this.items.clear();
        this.items.addAll(lists);
        Refresh();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //new LoadPicturesAsyncTask().execute(this.folders);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_picture_item, container, false);
        this.picItemRV = (RecyclerView)view.findViewById(R.id.pic_items);
        this.picItemRV.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        this.myPicItemAdapter = new NestlingPictureAdapter(getActivity(), true, items);
        this.picItemRV.setAdapter(this.myPicItemAdapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    class LoadPicturesAsyncTask extends AsyncTask<List<PicFolder>, Void, List<PicItem>>{
        @Override
        protected List<PicItem> doInBackground(List<PicFolder>... params) {
            List<PicItem> lists = new ArrayList<PicItem>();
            if(params[0] == null && params[0].size() < 0)
                return null;
            for(PicFolder pf : params[0]){
                lists.addAll(pf.getPictures());
            }
            return lists;
        }

        @Override
        protected void onPostExecute(List<PicItem> picItems) {
            super.onPostExecute(picItems);
            items.clear();
            items.addAll(picItems);
        }
    }


}
