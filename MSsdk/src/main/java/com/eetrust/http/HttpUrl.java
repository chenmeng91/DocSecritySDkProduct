package com.eetrust.http;

/**
 * Created by sunh on 16-10-27.
 */
public class HttpUrl {
    public static String versionId = null;
    public static String loginName = null;
    public static String BASE_IP = null;
    public static boolean isOpenFile;
    public static String SSO_TICKET = BASE_IP + "/securedoc/clientInterface/clientLogin.do?loginType=sso_ticket";
    public static String RIGHT_GRANT  = BASE_IP + "/securedoc/clientInterface/clientInfoRecv.do?method=grant_rights";
    public static String Defult_GRANT  = BASE_IP + "/securedoc//clientInterface/clientQuery.do?method=query_archivesPolicyRights";
    public static String LOG_OUT  = BASE_IP + "/securedoc/clientInterface/clientLogout.do?method=clientLogOut";
    public static String ISSUE_DOC = BASE_IP + "/securedoc/clientInterface/clientInfoRecv.do?method=issue_doc";

    //获取权限
    public static String GET_RIGHT = BASE_IP + "/securedoc/clientInterface/clientQuery.do?method=query_docRights";

    //日志审计
    public static String LOG_SEND  = BASE_IP + "/securedoc/clientInterface/clientInfoRecv.do?method=send_log";

    public static int appId;
    public static String userId = "";
    public static String userName = "";
    public static String userFrom = "";
    public static String configdential = "";
    public static String deptName = "";
    public static String email = "";
    public static String phone = "";
    public static String URL_IP = "";

}
