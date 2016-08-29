package org.nestling.guo.pictureselector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

public class ShowPictureActivity extends Activity {

    private ImageView imageView;
    private ImageButton back;
    private Button send;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);

        initComponent();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPictureActivity.this.finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("result", generateResult());
                ShowPictureActivity.this.finish();
            }
        });

        new MyLoadImageAsyncTask(imageView).execute(path);
    }

    /**
     * generate the result;
     * @return
     */
    private ArrayList<String> generateResult(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(path);
        return arrayList;
    }

    /**
     * init the component in the activity
     */
    private void initComponent(){
        imageView = (ImageView) this.findViewById(R.id.picture);
        back = (ImageButton)this.findViewById(R.id.back_to_activity);
        send = (Button)this.findViewById(R.id.pic_send_ok);
        Intent i = getIntent();
        path = i.getStringExtra("mmm");

    }


    class MyLoadImageAsyncTask extends AsyncTask<String, Void, Bitmap>{

        ImageView imageView;
        public MyLoadImageAsyncTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                Thread.sleep(4 * 1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            return getBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(this.imageView != null){
                this.imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * get the bitmap with the path.
     * @param path
     * @return
     */
    private Bitmap getBitmap(String path){
        try {
            return BitmapFactory.decodeFile(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
