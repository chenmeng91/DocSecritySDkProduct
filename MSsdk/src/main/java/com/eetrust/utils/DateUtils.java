package com.eetrust.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenmeng on 2017/8/3.
 */

public class DateUtils {
    private static DateUtils dateUtils;

    private DateUtils() {
    }
    public static DateUtils getInstance(){
        synchronized (DateUtils.class){
            if(dateUtils==null){
                dateUtils = new DateUtils();
            }
        }
        return dateUtils;
    }



    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//yyyy-MM-dd HH:mm:ss
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
