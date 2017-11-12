package com.artifex.mupdfdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eetrust.utils.OpenFile;

import java.io.File;

public class openPDFActivity extends AppCompatActivity {

    private ReaderView mDocView;
    private MuPDFCore mCore;
    private MuPDFPageAdapter mAdapter;
    private String mPdfFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPdfFilePath =  getIntent().getStringExtra("FILE");
        RelativeLayout layout = new RelativeLayout(this);
        File file  = new File(mPdfFilePath);
        if(file.exists()){
          Log.d("aa","aa");
        }
        mDocView = new ReaderView(this);
        try {
            mCore = new MuPDFCore(this,mPdfFilePath);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (mCore!=null&&mCore.countPages()==0){
            mCore = null;
        }
        if (null == mCore){
            Toast.makeText(this,"文件已损坏，无法打开", Toast.LENGTH_LONG).show();
            return;
        }
        mAdapter = new MuPDFPageAdapter(this,mCore);
        layout.setBackgroundColor(Color.BLACK);
        mDocView.setAdapter(mAdapter);
        layout.addView(mDocView);
        setContentView(layout);
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {

            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OpenFile.getInstence(getApplicationContext()).deleteFiel(mPdfFilePath);
    }
}
