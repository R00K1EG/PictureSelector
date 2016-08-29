package org.nestling.guo.pictureselector;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.nestling.guo.pictureselector.adapter.NestlingFolderAdapter;
import org.nestling.guo.pictureselector.bean.PicFolder;
import org.nestling.guo.pictureselector.bean.PicItem;
import org.nestling.guo.pictureselector.fragment.PictureItemFragment;
import org.nestling.guo.pictureselector.utils.DeviceUtils;
import org.nestling.guo.pictureselector.utils.TimeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PictureSelectorActivity extends AppCompatActivity {

    private static final String TAG = "PICTURE_SELECTOR_A";
    private PictureItemFragment pif;
    private ImageButton back;
    private Button send, selector;
    private List<PicFolder> lists;
    private List<PicItem> items;
    private FragmentManager fmanger;
    private String localImageName;
    private ListPopupWindow folderWindow;
    private List<PicItem> selectedItems;
    private int max_select;
    private boolean show_camera;
    private boolean single_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_selector);

        //parse the params from the intent.
        parseParams();

        //init the params
        initParams();

        //set the main listener
        this.selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(folderWindow == null){
                    initFolderListWindow();
                }
                folderWindow.show();
            }
        });

        this.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                PictureSelectorActivity.this.finish();
            }
        });

        this.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("result", generateResult());
                setResult(RESULT_OK, intent);
                PictureSelectorActivity.this.finish();
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

    /**
     * parse the params
     * single priority better than max_select
     * means:when single is true, max_select will be set 1.
     * @return
     */
    private boolean parseParams(){

        Intent i = getIntent();

        max_select = i.getIntExtra(PictureSelector.PARAM_COUNT, 9);
        show_camera = i.getBooleanExtra(PictureSelector.PARAM_CAMERA, true);
        single_picture = i.getBooleanExtra(PictureSelector.PARAM_SINGLE, true);

        if(single_picture)
            max_select = 1;

        return true;
    }


    /**
     * generate the path of selected images as the result
     * @return
     */
    private ArrayList<String> generateResult(){
        ArrayList<String> result = new ArrayList<>();
        for(PicItem pi : selectedItems){
            result.add(pi.getPath());
        }
        return result;
    }

    private void initParams(){
        lists = new ArrayList<PicFolder>();
        items = new ArrayList<PicItem>();
        selectedItems = new ArrayList<>();

        //get the widget
        this.back = (ImageButton)this.findViewById(R.id.back_up);
        this.send = (Button)this.findViewById(R.id.pic_send);
        this.selector = (Button)this.findViewById(R.id.select_folder_name);
    }

    /**
     * init the picture item for every folder
     */
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
                    updateItemAll();
                }else {
                    PicFolder pf = lists.get(position - 1);
                    selector.setText(pf.getName());
                    pf.selected();
                    updateItems(pf);
                }
                folderWindow.dismiss();
            }
        });
    }

    /**
     * update the items;
     * @param picFolder
     */
    private void updateItems(PicFolder picFolder){
        for(PicFolder pf : this.lists) {
            if(picFolder == pf)
                continue;
            pf.cancel();
        }

        /**
         * update the items
         */
        items.clear();
        items.addAll(picFolder.getPictures());
        pif.update(items);
    }

    private void updateItemAll(){
        items.clear();
        for(PicFolder pf: this.lists){
            pf.cancel();
            items.addAll(pf.getPictures());
        }
        pif.update(items);
    }

    /**
     * Use camera to take a photo.
     */
    public void selectPictureFromCamera(){
        String status = Environment.getExternalStorageState();
        if(status.equals(Environment.MEDIA_MOUNTED)){
            try{
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "SelectorTmp");
                if(!dir.exists()) dir.mkdir();
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                localImageName = TimeUtils.getCurrentTime()+".jpg";
                File f = new File(dir, localImageName);
                Uri u = Uri.fromFile(f);
                i.putExtra(MediaStore.EXTRA_OUTPUT, u);
                startActivityForResult(i, 1);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == -1){
            File file = new File(Environment.getExternalStorageDirectory() + "/SelectorTmp/" + localImageName);
            Uri uri = Uri.fromFile(file);
            Intent i = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            i.setData(uri);
            this.sendBroadcast(i);
            showCameraImage(file.getAbsolutePath());
        }
    }

    private void showCameraImage(String path){
        Intent i = new Intent(PictureSelectorActivity.this, ShowPictureActivity.class);
        i.putExtra("mmm", path);
        startActivity(i);
    }

    /**
     * add the selected picture
     */
    public boolean addSelectedPic(PicItem picItem){
        if(selectedItems.size() < 9){
            selectedItems.add(picItem);
            updateSelectedButton();
            return true;
        }
        Toast.makeText(PictureSelectorActivity.this, getString(R.string.error_select_tip), Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean removeSelectedPic(PicItem picItem){
        if(selectedItems.contains(picItem)){
            selectedItems.remove(picItem);
            updateSelectedButton();
            return true;
        }
        return false;
    }

    /**
     * refresh the button text;
     */
    private void updateSelectedButton(){
        int count = selectedItems.size();
        if(count > 0){
            this.send.setText("( " + count + "/9 )");
        }else
            this.send.setText(getString(R.string.send_btn));
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
