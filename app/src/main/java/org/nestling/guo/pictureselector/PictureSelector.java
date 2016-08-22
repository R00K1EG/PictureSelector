package org.nestling.guo.pictureselector;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by guo on 2016/8/22.
 */
public class PictureSelector {

    /**
    *  The max number of picture can be selected.
    *  default:9
    */
    private final int count;

    /**
     * The list can contain the camera or not.
     */
    private final boolean showCamera;

    /**
     * single or multi model
     */
    private final boolean single;

    /**
     * The result in multi_model.
     */
    private ArrayList<String> result;

    /**
     * The result in single_model.
     */
    private String singleResult;

    /**
     * context
     */
    private final Context context;

    /**
     * startActivityForResult : requestCode;
     * default:REQUEST_CODE
     */
    private final int requestCode;

    /**
     * params
     */
    public static final String PARAM_COUNT = "param_count";
    public static final String PARAM_SINGLE = "param_single";
    public static final String PARAM_CAMERA = "param_camera";
    public static final int REQUEST_CODE = 0x10;

    private PictureSelector(Creator creator){
        this.context = creator.context;
        this.count = creator.count;
        this.single = creator.single;
        this.requestCode = creator.requestCode;
        this.result = new ArrayList<>();
        this.singleResult = null;
        this.showCamera = creator.showCamera;
    }

    public static class Creator{

        private int count = 9;
        private boolean showCamera = true;
        private boolean single = true;
        private Context context = null;
        private int requestCode = 0x10;
        public Creator(Context context){
            this.context = context;
        }

        public Creator count(int number){
            this.count = number;
            return this;
        }

        public Creator showCamera(boolean show){
            this.showCamera = show;
            return this;
        }

        public Creator single(){
            this.single = true;
            return this;
        }

        public Creator multi(){
            this.single = false;
            return this;
        }

        public Creator requestCode(int requestCode){
            this.requestCode = requestCode;
            return this;
        }

        public boolean start(Activity activity){
            Intent i = createIntent();
            if(i != null) {
                activity.startActivityForResult(i, REQUEST_CODE);
                return true;
            }
            return false;
        }

        public boolean start(Fragment fragment){
            Intent i = createIntent();
            if(i != null) {
                fragment.startActivityForResult(i, REQUEST_CODE);
                return true;
            }
            return false;
        }

        /**
         * create the activity;
         * @return
         */
        private Intent createIntent(){
            if(context == null){
                return null;
            }
            Intent i = new Intent(context, PictureSelectorActivity.class);
            i.putExtra(PARAM_COUNT, this.count);
            i.putExtra(PARAM_CAMERA, this.showCamera);
            i.putExtra(PARAM_SINGLE, this.single);
            return i;
        }
    }



}
