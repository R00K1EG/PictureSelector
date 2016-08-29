package org.nestling.guo.pictureselector;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import org.nestling.guo.pictureselector.adapter.NestlingFolderAdapter;
import org.nestling.guo.pictureselector.bean.PicFolder;
import org.nestling.guo.pictureselector.bean.PicItem;
import org.nestling.guo.pictureselector.fragment.PictureFolderFragment;
import org.nestling.guo.pictureselector.fragment.PictureItemFragment;
import org.nestling.guo.pictureselector.utils.DeviceUtils;
import org.nestling.guo.pictureselector.view.NestlingFolderItemView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PictureSelectorActivity extends AppCompatActivity {

    private static final String TAG = "PICTURE_SELECTOR_A";
    private PictureItemFragment pif;
    private PictureFolderFragment pff;
    private ImageButton back;
    private Button send, selector;
    private List<PicFolder> lists;
    private List<PicItem> items;
    private FragmentManager fmanger;
    private ListPopupWindow folderWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_selector);
        lists = new ArrayList<PicFolder>();
        items = new ArrayList<PicItem>();
        //get the widget
        this.back = (ImageButton)this.findViewById(R.id.back_up);
        this.send = (Button)this.findViewById(R.id.pic_send);

        this.selector = (Button)this.findViewById(R.id.select_folder_name);
        this.selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(folderWindow == null){
                    initFolderListWindow();
                }
                folderWindow.show();
            }
        });

        //
        pif = new PictureItemFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.pic_frag, pif).commit();
        this.lists = search();
        loadPictures();
        if(folderWindow == null){
            initFolderListWindow();
        }
    }

    private void loadPictures(){
        if(this.lists.size() > 0){
            for(PicFolder pf : this.lists) {
                LoadImageAsyncTask loadImageAsyncTask = new LoadImageAsyncTask();
                loadImageAsyncTask.execute(pf);
            }
        }
    }

    class LoadImageAsyncTask extends AsyncTask<PicFolder, Void, List<PicItem>>{

        @Override
        protected void onPostExecute(List<PicItem> picItems) {
            super.onPostExecute(picItems);
            items.addAll(picItems);
            pif.update(items);
        }

        @Override
        protected List<PicItem> doInBackground(PicFolder... params) {
            List<PicItem> lists = new ArrayList<PicItem>();
            if(params[0] == null)
                return null;
            lists.addAll(params[0].getPictures());
            return lists;
        }
    }

    /**
     * search all of the folder and find the folders which contains the pictures.
     * @return
     */
    private List<PicFolder> search() {
        ArrayList<PicFolder> pics = new ArrayList<PicFolder>();
        Set<String> paths = new HashSet<String>();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = PictureSelectorActivity.this
                .getContentResolver();
        // only jpeg ,jpg and png
        Cursor mCursor = mContentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);
        Log.e(TAG, mCursor.getCount() + ": count");
        while (mCursor.moveToNext()) {
            PicFolder pf = null;
            // get the picture path
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.e(TAG, "pics:" + path);
            // get the picture parent path
            File parentFile = new File(path).getParentFile();
            if (parentFile == null)
                continue;
            String dirName = parentFile.getName();
            String dirPath = parentFile.getAbsolutePath();
            if (paths.contains(dirPath)) {
                continue;
            } else {
                paths.add(dirPath);
                pf = new PicFolder(dirName, dirPath);
                pics.add(pf);
            }
        }
        mCursor.close();
        return pics;
    }

    /**
     * init the folder list window(ListPopupWindow)
     */
    private void initFolderListWindow(){
        if(lists.size() == 0){
           lists = search();
        }
        Point p = DeviceUtils.getScreenSize(this);
        int width = p.x;
        int height = (int)(p.y * (0.5f / 0.8f));
        this.folderWindow = new ListPopupWindow(this);
        this.folderWindow.setAdapter(new NestlingFolderAdapter(this, R.layout.folder_item, lists));
        this.folderWindow.setWidth(width);
        this.folderWindow.setHeight(height);
        this.folderWindow.setAnchorView(this.selector);
        this.folderWindow.setModal(true);
        this.folderWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    selector.setText("所有文件");
                    updateItems(0);
                }else {
                    PicFolder pf = lists.get(position - 1);
                    selector.setText(pf.getName());
                    pf.selected();
                    updateItems(position);
                }
                folderWindow.dismiss();
            }
        });
    }

    /**
     * update the items;
     * @param position
     */
    private void updateItems(int position){
        if(position == 0)
            for(PicFolder pf : this.lists)
                pf.cancel();
        else
            for(int i = 0; i < lists.size(); i++){
                if(position == i)
                    continue;
                else
                    lists.get(i).cancel();
            }
    }

    class SearchImageFolderAsyncTask extends AsyncTask<Void, Void, List<PicFolder>>{
        @Override
        protected List<PicFolder> doInBackground(Void... params) {
            return search();
        }
        @Override
        protected void onPostExecute(List<PicFolder> picFolders) {
            super.onPostExecute(picFolders);
            lists.clear();
            lists.addAll(picFolders);
        }
        private List<PicFolder> search() {
            ArrayList<PicFolder> pics = new ArrayList<PicFolder>();
            Set<String> paths = new HashSet<String>();
            Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = PictureSelectorActivity.this
                    .getContentResolver();
            // only jpeg ,jpg and png
            Cursor mCursor = mContentResolver.query(mImageUri, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png"},
                    MediaStore.Images.Media.DATE_MODIFIED);
            Log.e(TAG, mCursor.getCount() + ": count");
            while (mCursor.moveToNext()) {
                PicFolder pf = null;
                // get the picture path
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Log.e(TAG, "pics:" + path);
                // get the picture parent path
                File parentFile = new File(path).getParentFile();
                if (parentFile == null)
                    continue;
                String dirName = parentFile.getName();
                String dirPath = parentFile.getAbsolutePath();
                if (paths.contains(dirPath)) {
                    continue;
                } else {
                    paths.add(dirPath);
                    pf = new PicFolder(dirName, dirPath);
                    pics.add(pf);
                }
            }
            mCursor.close();
            return pics;
        }
    }
}
