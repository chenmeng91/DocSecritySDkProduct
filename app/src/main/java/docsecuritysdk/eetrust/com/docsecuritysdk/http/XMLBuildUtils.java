package docsecuritysdk.eetrust.com.docsecuritysdk.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Xml;

import com.eetrust.bean.IssueGrantXMLBean;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by sunh on 16-10-31.
 */
public class XMLBuildUtils {

    public static String buildDocIssueXMLString(Context context, IssueGrantXMLBean xmlBean) throws Exception {
        if (HttpUrl.loginName == null) {
            String loginNameSP = getLoginNameSP(context);
            if (loginNameSP != null && !loginNameSP.equals("")) {
                HttpUrl.loginName = loginNameSP;
            }
        }
        String count = null;
        StringWriter writer = new StringWriter();

        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(writer);
        // 相当于写入<?xml version="1.0" encoding="UTF-8"?>
        serializer.startDocument("utf-8", true);
        //写入跟元素的起始标签
        serializer.startTag(null, "archivesInfo");

        serializer.startTag(null, "docissue");
        serializer.startTag(null, "archives");
        setTag("identifier", xmlBean.getIdentifier(), serializer);
        setTag("name", xmlBean.getName(), serializer);
        setTag("createuser", HttpUrl.loginName, serializer);
        setTag("confidential", HttpFields.ARCHIVES_CONFIDENTIAL, serializer);
        setTag("archivesFrom", HttpFields.ARCHIVES_FROM, serializer);
        setTag("IsCreateCryptKey", HttpFields.ARCHIVES_IS_CREATE_CRYPT_KEY, serializer);
        serializer.endTag(null, "archives");
        serializer.startTag(null, "docs");
        serializer.startTag(null, "doc");
        setTag("identifier", xmlBean.getIdentifier(), serializer);
        setTag("name", xmlBean.getName(), serializer);
        serializer.endTag(null, "doc");
        serializer.endTag(null, "docs");
        serializer.endTag(null, "docissue");

        //写入跟元素的结束标签
        serializer.endTag(null, "archivesInfo");
        //结束文档的写入
        serializer.endDocument();
        count = writer.toString();
        writer.flush();
        writer.close();
        Log.d("docIssue--->", count);
        return count;
    }

    public static String buildRightGrantXMLString(Context context, IssueGrantXMLBean grantXMLBean) throws IOException {
        if (HttpUrl.loginName == null) {
            String loginNameSP = getLoginNameSP(context);
            if (loginNameSP != null && !loginNameSP.equals("")) {
                HttpUrl.loginName = loginNameSP;
            }
        }
        String count = null;
        StringWriter writer = new StringWriter();
        XmlSerializer serializer = Xml.newSerializer();

        serializer.setOutput(writer);
        serializer.startDocument("utf-8", true);
        serializer.startTag(null, "rights");
        serializer.startTag(null, "grantuser");
        setTag("loginname", HttpUrl.loginName, serializer);
        serializer.endTag(null, "grantuser");
        serializer.startTag(null, "archives");
        setTag("id", grantXMLBean.getArchiveId() + "", serializer);
        serializer.endTag(null, "archives");
//        serializer.startTag(null, "depts");
//        serializer.startTag(null, "dept");
//        setTag("id", -1 + "", serializer);
//        setTag("rights", 1 + "", serializer);
//        serializer.endTag(null, "dept");
//        serializer.endTag(null, "depts");

        serializer.startTag(null, "users");
        serializer.startTag(null, "user");
        setTag("loginname", HttpUrl.loginName, serializer);
        setTag("rights", HttpFields.DEFAULT_RIGHT, serializer);
        serializer.endTag(null, "user");
        serializer.endTag(null, "users");
        serializer.endTag(null, "rights");
        serializer.endDocument();
        count = writer.toString();
        writer.flush();
        writer.close();
        Log.d("Log_Send--->", count);
        return count;
    }

    private static void setTag(String tagName, String tagText, XmlSerializer serializer) throws IOException {
        serializer.startTag(null, tagName);
        serializer.text(tagText);
        serializer.endTag(null, tagName);
    }

    /**
     * 获取SP保存的loginName
     *
     * @param context 传进来的上下文
     * @return 服务器地址
     */
    private static String getLoginNameSP(Context context) {
        //获取当前context的SP
        SharedPreferences sp = context.getSharedPreferences(HttpFields.SDS_SP_NAME, Context.MODE_PRIVATE);
        //根据key获取value
        return sp.getString(HttpFields.SDS_LOGIN_NAME, "");
    }
}
