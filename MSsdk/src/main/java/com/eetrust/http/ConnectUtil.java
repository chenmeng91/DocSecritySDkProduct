package com.eetrust.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by android on 2016/6/16.
 */
public class ConnectUtil {

    public static  boolean isConnenct(Context context){
         ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
          NetworkInfo[] networks= connectivityManager.getAllNetworkInfo();
        for (NetworkInfo info:networks){
            if (info.getState()== NetworkInfo.State.CONNECTED)
                return true;
        }
        return  false;
    }
}
