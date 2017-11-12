package com.eetrust.utils.fileOpen;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;


import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import docsecuritysdk.eetrust.com.mysdk.R;

/**
 * Created by chenmeng on 2017/9/5.
 */

public class TXTActivity extends Activity {
    TextView txt_text;
    private Handler mHandler;//全局变量
    File filename;
    private StringBuffer sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setContentView(R.layout.txt_activity);
        txt_text = (TextView) findViewById(R.id.txt_text);
        filename = new File(getIntent().getStringExtra("FILE"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                GetTXTFile();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        txt_text.setText(sb.toString());
                    }
                });
            }
        }).start();


    }

    private void GetTXTFile() {
        sb = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"GB2312"));
            String strLine = "";
            while((strLine = reader.readLine())!=null){
                String temp3 = EncodingUtils.getString(strLine.getBytes(),"utf-8");
                sb.append(temp3);
            }
            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
