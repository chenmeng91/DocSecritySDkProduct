package com.eetrust.db_operation;

/**
 * Created by chenmeng on 2017/8/2.
 */

public class LogTable {
    public static String TABLE_NAME = "LogTable";
    public static String LOGI_NNAME = "loginName";
    public static String ID = "id";
    public static String DOC_ID = "docId";
    public static String ARCHIVE_ID = "archiveId";
    public static String OPER = "oper";//操作类型
    public static String ONLINE="online";//是否为在线操作
    public static String RESULT="result";//是否成功，无则成功0：失败；1：成功
    public static String MEMO = "memo";//失败原因
    public static String CLIENT_IP="clientIP";//IP,如不传以请求为准
    public static String OPERTIME="opertime";//如果为离线操作，必须传送离线操作时间,格式
}
