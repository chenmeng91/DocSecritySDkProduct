package com.eetrust.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


import com.artifex.mupdfdemo.openPDFActivity;
import com.eetrust.utils.fileOpen.PresentationActivity;
import com.eetrust.utils.fileOpen.SpreadsheetActivity;
import com.eetrust.utils.fileOpen.TXTActivity;
import com.eetrust.utils.fileOpen.WordActivity;


import java.io.File;
import java.util.Locale;

/**
 * Created by android on 2016/8/12.
 */
public class OpenFile {
    private Context context;
    private static OpenFile openFile;
    public static final String EXTRA_FILE_NAME = "FILE";
    public static final String RIGHTS = "Rights";
    private OpenFile(Context context) {
        this.context = context;
    }

    public synchronized static OpenFile getInstence(Context context) {
        if (openFile == null) {
            openFile = new OpenFile(context);
        }
        return openFile;
    }

    public void openAttachment(String name, String rights) {
        String ext = name.substring(name.lastIndexOf('.')).toLowerCase(Locale.US);
        if(ext.endsWith(".doc") || ext.endsWith(".docx") || ext.endsWith(".wps")|| ext.endsWith(".wpt")
                ||ext.endsWith(".xls") || ext.endsWith(".xlsx")||ext.endsWith(".et")||ext.endsWith(".ett")
                ||ext.endsWith(".ppt") || ext.endsWith(".pptx")||ext.endsWith(".dps")||ext.endsWith(".dpt")
                ||ext.endsWith(".pdf")|| ext.endsWith(".txt")
                ){

            File file = new File(name);
            String renmeFile = null;
            if(ext.endsWith(".wps")|| ext.endsWith(".wpt")){
                renmeFile = name.substring(0,name.length()-3)+"docx";
            }else if(ext.endsWith(".et")){
                renmeFile = name.substring(0,name.length()-2)+"xlsx";
            }else if(ext.endsWith(".ett")){
                renmeFile = name.substring(0,name.length()-3)+"xlsx";
            }else if(ext.endsWith(".dps")||ext.endsWith(".dpt")){
                renmeFile = name.substring(0,name.length()-3)+"pptx";
            }
            if(renmeFile==null){
                open(name,rights);
            }else {
                file.renameTo(new File(renmeFile));
                open(renmeFile,rights);
            }

        }else {
            try {
                // 打开；
                File file = new File(name);
//
//                Intent intent = new Intent();
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                // 设置intent的Action属性
//                intent.setAction(Intent.ACTION_VIEW);
//                // 获取文件file的MIME类型
//                String type = getMIMEType(file);
//                // 设置intent的data和Type属性。
//                intent.setDataAndType(Uri.fromFile(file), type);
//                // 跳转
//                context.startActivity(intent);

                FileUtils.startActionFile(context,file,getMIMEType(file));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "未找到可以打开该文件的程序", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void open(String filePath, String rights) {
        String ext = filePath.substring(filePath.lastIndexOf('.')).toLowerCase(Locale.US);
        Class<? extends Activity> targetActivity = null;
        if (ext.endsWith(".doc") || ext.endsWith(".docx")) {
            targetActivity = WordActivity.class;
        } else if (ext.endsWith(".xls") || ext.endsWith(".xlsx")||ext.endsWith(".et")) {
            targetActivity = SpreadsheetActivity.class;
        } else if (ext.endsWith(".ppt") || ext.endsWith(".pptx")) {
            targetActivity = PresentationActivity.class;
        }else if (ext.endsWith(".pdf")) {
            targetActivity = openPDFActivity.class;
        }else if( ext.endsWith(".txt")){
            targetActivity = TXTActivity.class;
        }else {

        }
        Intent intent = new Intent(context, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_FILE_NAME, filePath);
        intent.putExtra(RIGHTS,rights);
        context.startActivity(intent);
    }
    private String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        // 获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "")
            return type;
        // 在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { // MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private final String[][] MIME_MapTable = {
            {".doc", "application/msword"},
            {".docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".txt", "text/plain"}, {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"}, {"", "*/*"}};


    public void deleteFiel(String fielPath){
        File deleteFile = new File(fielPath);
        if(deleteFile.exists()&&deleteFile.isFile()){
            deleteFile.delete();
        }
    }
}
