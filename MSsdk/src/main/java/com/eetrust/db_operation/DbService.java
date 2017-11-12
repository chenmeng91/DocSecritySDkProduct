package com.eetrust.db_operation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eetrust.bean.RightAndKeyBean;
import com.eetrust.bean.UserLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenmeng on 2016/10/27.
 */

public class DbService {
    private static DbService instance;
    private SqlHelper helper;
   private DatabaseManager mDatabaseManager;

    //private SQLiteDatabase db;
    public static DbService getInstance(Context context) {
        if (instance == null) {
            instance = new DbService(context);
        }
        return instance;
    }

    public DbService(Context context) {
        helper = new SqlHelper(context, "COM_EETRUST_MSSDK.db");//数据库名字
        mDatabaseManager = DatabaseManager.getInstance(helper);
    }

    /**
     * 增加或者修改一条记录
     */
    public void insertOrUpdateRightAndkeyTable(RightAndKeyBean rightAndKeyBean) {
        //查询用户名 文档组id 文档id 相同的记录，如果有相同的就替换掉
        List<RightAndKeyBean> rightAndKeyBeenSelects = selectRightAndkeyTable(rightAndKeyBean.getLoginName(), rightAndKeyBean.getArchiveId(), rightAndKeyBean.getDocId(), RightAndkeyTable.ASC);
        if (rightAndKeyBeenSelects.size() == 0) {
            ContentValues values = new ContentValues();
            values.put(RightAndkeyTable.LOGI_NNAME, rightAndKeyBean.getLoginName());
            values.put(RightAndkeyTable.DOC_ID, rightAndKeyBean.getDocId()+"");
            values.put(RightAndkeyTable.ARCHIVE_ID, rightAndKeyBean.getArchiveId()+"");
            values.put(RightAndkeyTable.RIGHT, rightAndKeyBean.getRight());
            values.put(RightAndkeyTable.KEY, rightAndKeyBean.getKey());
            SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
            long backdata = db.insert(RightAndkeyTable.TABLE_NAME, null, values);
            Log.d("add", backdata + "");
            mDatabaseManager.closeDatabase();
        } else {
            updateRightAndkeyTable(rightAndKeyBean);//更新
        }
    }

    /**
     * 删除一条记录 根据loginName、archiveId、docId定位一条记录
     *
     * @param
     */
    public void deleteRightAndkeyTable(RightAndKeyBean rightAndKeyBean) {
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        //注意“and”要有空格
        int backdata = db.delete(RightAndkeyTable.TABLE_NAME
                , RightAndkeyTable.LOGI_NNAME + "=? and "
                        + RightAndkeyTable.ARCHIVE_ID + "=? and "
                        + RightAndkeyTable.DOC_ID + "=?"
                , new String[]{rightAndKeyBean.getLoginName(), rightAndKeyBean.getArchiveId() + "", rightAndKeyBean.getDocId() + ""});
        Log.d("delete", backdata + "");
//        db.close();
        mDatabaseManager.closeDatabase();
    }

    public void deleteAll() {
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        //注意“and”要有空格
        int backdata = db.delete(RightAndkeyTable.TABLE_NAME,null,null);
        Log.d("delete", backdata + "");
//        db.close();
        mDatabaseManager.closeDatabase();
    }

    /**
     * 改
     */
    public void updateRightAndkeyTable(RightAndKeyBean rightAndKeyBean) {
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RightAndkeyTable.RIGHT, rightAndKeyBean.getRight());
        contentValues.put(RightAndkeyTable.KEY, rightAndKeyBean.getKey());
        int backdata = db.update(RightAndkeyTable.TABLE_NAME,
                contentValues, RightAndkeyTable.LOGI_NNAME + "=? and "
                        + RightAndkeyTable.ARCHIVE_ID + "=? and "
                        + RightAndkeyTable.DOC_ID + "=?"
                , new String[]{rightAndKeyBean.getLoginName(), rightAndKeyBean.getArchiveId() + "", rightAndKeyBean.getDocId() + ""});
        Log.d("updateRightAndkeyTable", backdata + "");
//        db.close();
        mDatabaseManager.closeDatabase();
    }

    /**
     * @param loginName 登录名
     * @param archiveId 文档组id
     * @param docId     文档id
     * @param orderBy   顺序
     * @return 返回List<RightAndKeyBean>的集合
     */
    public List<RightAndKeyBean> selectRightAndkeyTable(String loginName, long archiveId, long docId, String orderBy) {
        if(loginName==null||"".equals(loginName)){
            return null;
        }

        List<RightAndKeyBean> rightAndKeyBeens = new ArrayList<>();
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        Cursor cursor = db.query(RightAndkeyTable.TABLE_NAME, null
                , RightAndkeyTable.LOGI_NNAME + "=? and "
                        + RightAndkeyTable.ARCHIVE_ID + "=? and "
                        + RightAndkeyTable.DOC_ID + "=?"
                , new String[]{loginName, archiveId + "", docId + ""}, null, null, "id " + orderBy, null);//DESC为降序 asc 为正序 1为偏移量，3为数量
        cursor.moveToFirst();//移动到第一条
        //如果不是最后一条的下边
        while (!cursor.isAfterLast()) {//遍历整个查询结果
            long id = cursor.getLong(cursor.getColumnIndex("id"));
            String login_name = cursor.getString(cursor.getColumnIndex(RightAndkeyTable.LOGI_NNAME));//cursor.getColumnIndex("name")为获得name的idex
            String right = cursor.getString(cursor.getColumnIndex(RightAndkeyTable.RIGHT));//cursor.getString()为获得相应的字符串=
            long doc_id = Long.parseLong(cursor.getString(cursor.getColumnIndex(RightAndkeyTable.DOC_ID)));
            long archive_id = Long.parseLong(cursor.getString(cursor.getColumnIndex(RightAndkeyTable.ARCHIVE_ID))) ;
            String key = cursor.getString(cursor.getColumnIndex(RightAndkeyTable.KEY));//cursor.getString()为获得相应的字符串
            RightAndKeyBean rightAndKeyBeen = new RightAndKeyBean(id, login_name, doc_id, archive_id, right, key);
            rightAndKeyBeens.add(rightAndKeyBeen);
            Log.d("cursor", " id:" + id + "用户名:" + login_name + " 权限:" + right + " doc_id: " + doc_id + " archive_id: " + archive_id + " key: " + key);
            cursor.moveToNext();//下一条
        }
//        db.close();
        mDatabaseManager.closeDatabase();
        return rightAndKeyBeens;
    }

    /**
     * 查全部
     */
    public void selectRightAndkeyTable() {
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        Cursor cursor = db.query(RightAndkeyTable.TABLE_NAME, null, null, null, null, null, "id DESC", null);//DESC为降序 1为偏移量，3为数量
        cursor.moveToFirst();//移动到第一条
        //如果不是最后一条的下边
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor.getColumnIndex("id"));
            String login_name = cursor.getString(cursor.getColumnIndex(RightAndkeyTable.LOGI_NNAME));//cursor.getColumnIndex("name")为获得name的idex
            String right = cursor.getString(cursor.getColumnIndex(RightAndkeyTable.RIGHT));//cursor.getString()为获得相应的字符串
            long doc_id = Long.parseLong(cursor.getString(cursor.getColumnIndex(RightAndkeyTable.DOC_ID)));
            long archive_id = Long.parseLong(cursor.getString(cursor.getColumnIndex(RightAndkeyTable.ARCHIVE_ID)));
            String key = cursor.getString(cursor.getColumnIndex(RightAndkeyTable.KEY));//cursor.getString()为获得相应的字符串
            Log.d("cursor", " id:" + id + "用户名:" + login_name + " 权限:" + right + " doc_id: " + doc_id + " archive_id: " + archive_id + " key: " + key);
            cursor.moveToNext();//下一条
        }
//        db.close();
        mDatabaseManager.closeDatabase();
    }




    //************************************************************************************************
    //日志审计操作
    //************************************************************************************************
    /**
     * 增加或者修改一条记录
     */
    public void insertOrUpdateLog( UserLog userLog) {
        //查询用户名 文档组id 文档id 相同的记录，如果有相同的就替换掉
            ContentValues values = new ContentValues();
            values.put(LogTable.LOGI_NNAME, userLog.getLoginName());
            values.put(LogTable.DOC_ID, userLog.getDocID()+"");
            values.put(LogTable.ARCHIVE_ID, userLog.getArchivesID()+"");
            values.put(LogTable.OPER, userLog.getOper());
            values.put(LogTable.ONLINE, userLog.getOnline());
            values.put(LogTable.RESULT, userLog.getResult());
            values.put(LogTable.MEMO, userLog.getResult());
            values.put(LogTable.CLIENT_IP, userLog.getClientIP());
            values.put(LogTable.OPERTIME, userLog.getOpertime());
            SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
            long backdata = db.insert(LogTable.TABLE_NAME, null, values);
            Log.d("insertOrUpdateLog-->add", backdata + "");
            mDatabaseManager.closeDatabase();

    }


    /**
     * @param loginName 登录名
     * @param archiveId 文档组id
     * @param docId     文档id
     * @param orderBy   顺序
     * @return 返回List<RightAndKeyBean>的集合
     */
    public List<RightAndKeyBean> selectLog(String loginName, long archiveId, long docId, String orderBy) {
        if(loginName==null||"".equals(loginName)){
            return null;
        }

        List<RightAndKeyBean> rightAndKeyBeens = new ArrayList<>();
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        Cursor cursor = db.query(LogTable.TABLE_NAME, null
                , LogTable.LOGI_NNAME + "=? and "
                        + LogTable.ARCHIVE_ID + "=? and "
                        + LogTable.DOC_ID + "=?"
                , new String[]{loginName, archiveId + "", docId + ""}, null, null, "id " + orderBy, null);//DESC为降序 asc 为正序 1为偏移量，3为数量
        cursor.moveToFirst();//移动到第一条
        //如果不是最后一条的下边
        while (!cursor.isAfterLast()) {//遍历整个查询结果
            long id = cursor.getLong(cursor.getColumnIndex("id"));
            String login_name = cursor.getString(cursor.getColumnIndex(RightAndkeyTable.LOGI_NNAME));//cursor.getColumnIndex("name")为获得name的idex
            String right = cursor.getString(cursor.getColumnIndex(RightAndkeyTable.RIGHT));//cursor.getString()为获得相应的字符串=
            long doc_id = Long.parseLong(cursor.getString(cursor.getColumnIndex(RightAndkeyTable.DOC_ID)));
            long archive_id = Long.parseLong(cursor.getString(cursor.getColumnIndex(RightAndkeyTable.ARCHIVE_ID))) ;
            String key = cursor.getString(cursor.getColumnIndex(RightAndkeyTable.KEY));//cursor.getString()为获得相应的字符串
            RightAndKeyBean rightAndKeyBeen = new RightAndKeyBean(id, login_name, doc_id, archive_id, right, key);
            rightAndKeyBeens.add(rightAndKeyBeen);
            Log.d("cursor", " id:" + id + "用户名:" + login_name + " 权限:" + right + " doc_id: " + doc_id + " archive_id: " + archive_id + " key: " + key);
            cursor.moveToNext();//下一条
        }
//        db.close();
        mDatabaseManager.closeDatabase();
        return rightAndKeyBeens;
    }



    /**
     * 查全部日志
     *     public static String LOGI_NNAME = "loginName";

     public static String DOC_ID = "docId";
     public static String ARCHIVE_ID = "archiveId";
     public static String OPER = "oper";//操作类型  int
     public static String ONLINE="online";//是否为在线操作
     public static String RESULT="result";//是否成功，无则成功0：失败；1：成功
     public static String MEMO = "memo";//失败原因
     public static String CLIENT_IP="clientIP";//IP,如不传以请求为准
     public static String OPERTIME="opertime";//如果为离线操作，必须传送离线操作时间,格式
     */
    public List<UserLog> selectAllLog(String loginName) {
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();

        Cursor cursor = db.query(LogTable.TABLE_NAME, null
                , LogTable.LOGI_NNAME + "=?"
                , new String[]{loginName}, null, null,"id DESC", null);//DESC为降序 asc 为正序 1为偏移量，3为数量
//        Cursor cursor = db.query(LogTable.TABLE_NAME, null, null, null, null, null, "id DESC", null);//DESC为降序 1为偏移量，3为数量
        cursor.moveToFirst();//移动到第一条
        //如果不是最后一条的下边
        List<UserLog> userLogs = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            UserLog userLog = new UserLog();
            long id = cursor.getLong(cursor.getColumnIndex("id"));
            String login_name = cursor.getString(cursor.getColumnIndex(LogTable.LOGI_NNAME));//cursor.getColumnIndex("name")为获得name的idex
            long doc_id = Long.parseLong(cursor.getString(cursor.getColumnIndex(LogTable.DOC_ID)));
            long archive_id = Long.parseLong(cursor.getString(cursor.getColumnIndex(LogTable.ARCHIVE_ID)));
            int oper = Integer.parseInt(cursor.getString(cursor.getColumnIndex(LogTable.OPER)));
            int online = Integer.parseInt(cursor.getString(cursor.getColumnIndex(LogTable.ONLINE)));
            int result = Integer.parseInt(cursor.getString(cursor.getColumnIndex(LogTable.RESULT)));
            String memo = cursor.getString(cursor.getColumnIndex(LogTable.MEMO));//cursor.getString()为获得相应的字符串
            String clientIP = cursor.getString(cursor.getColumnIndex(LogTable.CLIENT_IP));//cursor.getString()为获得相应的字符串
            String opertime = cursor.getString(cursor.getColumnIndex(LogTable.OPERTIME));//cursor.getString()为获得相应的字符串
//            Log.d("cursor", " id:" + id + "用户名:" + login_name + " 权限:" + right + " doc_id: " + doc_id + " archive_id: " + archive_id + " key: " + key);
            userLog.setId(id);
            userLog.setLoginName(login_name);
            userLog.setDocID(doc_id);
            userLog.setArchivesID(archive_id);
            userLog.setOper(oper);
            userLog.setOnline(online);
            userLog.setResult(result);
            userLog.setMemo(memo);
            userLog.setClientIP(clientIP);
            userLog.setOpertime(opertime);
            userLogs.add(userLog);
            cursor.moveToNext();//
        }
//        db.close();下一条
        mDatabaseManager.closeDatabase();

        return userLogs;
    }


    /**
     * 删除一条记录 根据loginName、archiveId、docId定位一条记录
     *
     * @param
     */
    public void deleteLog(long id) {
        SQLiteDatabase db = mDatabaseManager.getReadableDatabase();
        //注意“and”要有空格
        int backdata = db.delete(LogTable.TABLE_NAME
                , LogTable.ID + "=? "
                , new String[]{id+""});
        Log.d("delete", backdata + "");
//        db.close();
        mDatabaseManager.closeDatabase();
    }

}
