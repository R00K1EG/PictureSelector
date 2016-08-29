package org.nestling.guo.pictureselector.utils;

import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.view.WindowManager;

/**
 * Created by guo on 2016/8/29.
 */
public class DeviceUtils {

    public static Point getScreenSize(Context context){
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        return new Point(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
    }

}
