package com.eetrust.utils;

/**
 * Created by chenmeng on 2017/8/11.
 */

public class NetRequestErrorUtile {
    private static NetRequestErrorUtile netRequestErrorUtile;

    private NetRequestErrorUtile() {
    }

    public static NetRequestErrorUtile getInstance(){
        synchronized (NetRequestErrorUtile.class){
            if(netRequestErrorUtile==null){
                netRequestErrorUtile = new NetRequestErrorUtile();
            }

            return netRequestErrorUtile;
        }

    }

    public String getErrorinf(String error){
        if(error!=null&&(error.contains("网络连接失败，请重新配置")||error.contains("请求超时")||error.contains("请求失败"))){

            return error;

        }else {
            return "网络请求数据获取失败";
        }
    }
}
