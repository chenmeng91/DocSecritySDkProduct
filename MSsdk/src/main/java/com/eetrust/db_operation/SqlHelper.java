package com.eetrust.db_operation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chenmeng on 2016/10/27.
 */

public class SqlHelper extends SQLiteOpenHelper {
    public SqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);//version为版本号，name为数据库的名字，factory一般为null
    }
    public SqlHelper(Context context, String name ){
        this(context, name,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库的表格 id从一开始的
//        db.execSQL("create table if not exists user(id integer primary key  autoincrement,name varchar(20),password varchar(20))");
        db.execSQL("create table if not exists "+ RightAndkeyTable.TABLE_NAME+"(id integer primary key  autoincrement,"
                +RightAndkeyTable.LOGI_NNAME+" TEXT,"
                +RightAndkeyTable.DOC_ID+" TEXT,"
                +RightAndkeyTable.ARCHIVE_ID+" TEXT,"
                +RightAndkeyTable.RIGHT+" TEXT,"
                +RightAndkeyTable.KEY+" TEXT)");

        db.execSQL("create table if not exists "+ LogTable.TABLE_NAME+"(id integer primary key  autoincrement,"
                +LogTable.LOGI_NNAME+" TEXT,"
                +LogTable.DOC_ID+" TEXT,"
                +LogTable.ARCHIVE_ID+" TEXT,"
                +LogTable.OPER+" TEXT,"
                +LogTable.ONLINE+" TEXT,"
                +LogTable.RESULT+" TEXT,"
                +LogTable.MEMO+" TEXT,"
                +LogTable.CLIENT_IP+" TEXT,"
                +LogTable.OPERTIME+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}