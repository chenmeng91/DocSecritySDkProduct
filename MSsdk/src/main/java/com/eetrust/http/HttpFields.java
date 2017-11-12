package com.eetrust.http;

/**
 * Created by sunh on 16-10-31.
 */

public class HttpFields {
    public static  String ARCHIVES_CONFIDENTIAL = "1";
    public static final String ARCHIVES_FROM = "1";
    public static final String ARCHIVES_IS_CREATE_CRYPT_KEY = "true";
    public static String userId = "";
    //网络请求的成功与否
    public final static String NET_REQUEST_SUCESS = "1";
    public final static String NET_REQUEST_FAIL = "0";
    public final static String NET_RESULT = "result";
    public final static String NET_ERROR = "error";
    public final static String NET_ABNORMAL = "服务器返回数据异常";

    //权限获取字段
    public final static String RIGHT_RIGHTS = "rights";
    public final static String RIGHT_KEY = "key";
    public final static String RIGHT_ERROR = "error";

    //权限字段
    public final static int READ_RIGHT = 1;//阅读权限
    public final static int COPY_RIGHT = 2;//复制权限
    public final static int EDIT_RIGHT = 3;//编辑权限
    public final static int PRINT_RIGHT = 4;//打印权限
    public final static int WATERMARK_PRINTING_RIGHT = 5;//水印打印权限
    public final static int SCREENSHOT_RIGHT = 6;//截屏权限
    public final static int DECRYPT_RIGHT = 7;//解密权限
    public final static int OFFLINE_RIGHT = 8;//离线权限
    public final static int PUTTING_OUT_RIGHT = 9;//外发权限
    public final static int DISTRIBUTE_RIGHT = 10;//分发权限
    public final static String DEFAULT_RIGHT = "1,2,3,4,5,6,7,8,9,10,";//用户默认权限

    public static final String SDS_SERVER_ADRESS = "serverAddress"; //SP中的BaseIp
    public static final String SDS_APP_ID_SP = "appId"; //SP中的appId
    public static final String SDS_LOGIN_NAME = "loginName";    //SP中的loginName
    public static final String SDS_SERVER_SESSION = "serverSession";    //SP中的session
    public static final String SDS_SP_NAME = "sds_login_user";  //SP的名字

    public static final String SSO_TYPE = "3";  //移动端登录
    public static int SDS_APP_ID = 1;
    public static final int DEFAULT_APP_ID = 3;  //APP_ID默认值
    public static final int ENCRYPT_SUCCESS = 1;

    //网络请求参数
    public static final String ARCHIVES_ID = "archivesID";//文档组id
    public static final String DOC_ID = "docID";//文档id
    public static final String ARCHIVES_FROM_APPID = "archivesFrom";//appid
    public static final String LOGIN_NAME = "loginName";//登录名
    public static boolean IS_NET_REQUEST = false;

    public static String authorities =null;

    //日志审计
    public static String USER_LOG="userLog";
    public static String TYPE = "type";
    public static int Offline=0;//离线
    public static int ONLINE = 1;//在线
    public static int OPER_READ = 1;//阅读
    public static int OPER_DECRYPT = 7;//阅读
    public static int OPER_SUCCESS=1;
    public static int OPER_FAIL = 0;

    //获取默认
    public static String DEFAULT_RIGHTS = "rights";

}
