package com.eetrust;

import android.content.Context;
import android.util.Log;

import com.eetrust.securedocsdk.CryptoUtil;


/**
 * Created by chenmeng on 2016/10/27.
 */

public class MyTest {
    private Context context;

    public MyTest(Context context) {
        this.context = context;
    }

    public void testCryptoUtil(){
        CryptoUtil util = CryptoUtil.getInstance();

//        int result = util.EncryptFile("380fd54fbd5a4072e0d0cbeec6eb02b4","/storage/emulated/0/cn.wps.moffice.demo.doc",40l,505l,"/storage/emulated/0/demoCode.doc");
//        Log.i("testCryptoUtil", "result1 = " + result);

        int result = util.DecryptFile("380fd54fbd5a4072e0d0cbeec6eb02b4","/storage/emulated/0/jiamiwenjian.docx","/storage/emulated/0/jiamiwenjianEncode.docx");
        Log.i("testCryptoUtil", "result2 = " + result);

//        result = util.getSecureDocInfo("GetInfoSecureDocName.docx");
//        Log.i("testCryptoUtil", "result3 = " + result);
//
//        boolean isSecDoc = util.isSecureDoc("isSecureDocName.docx");
//        Log.i("testCryptoUtil", "isSecDoc = " + result);

    }

}
