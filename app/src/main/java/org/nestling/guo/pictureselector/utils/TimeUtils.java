package org.nestling.guo.pictureselector.utils;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guo on 2016/8/29.
 */
public class TimeUtils {

    public static String getCurrentTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
        return dateFormat.format(new Date());
    }


}
