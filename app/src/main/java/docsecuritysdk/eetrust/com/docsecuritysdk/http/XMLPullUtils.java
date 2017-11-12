package docsecuritysdk.eetrust.com.docsecuritysdk.http;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by sunh on 16-10-31.
 */
public class XMLPullUtils {

    private XmlPullParser parser;
    public static final int CLIENTLOGIN = 1;//登陆标识
    public static final int CLIENTLOGOUT = 2;//退出登录标识
    public static final int ISSUEDOC = 3;//文档组发布标识
    public static final int GRANTRIGHTS = 4;//文档组授权标识
    public static final int RIGHT_QUERY = 5;//权限查询
    public static  final int SEND_LOG = 6;//发送日志
    public static class XMLUtilsHolder {

        public static XMLPullUtils instance = new XMLPullUtils();
    }

    private XMLPullUtils() {
        parser = Xml.newPullParser();
    }

    public static XMLPullUtils getInstance() {
        return XMLUtilsHolder.instance;
    }

    /**
     * @param string 要解析的xml字符串
     * @param flag   标识符，根据不同的标识符调用不同的解析方法
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws XmlPullParserException
     * @throws NoSuchFieldException
     * @throws IOException
     */
    public synchronized Map<String, Object> xml2Obj(String string, int flag) throws IllegalAccessException, InstantiationException, XmlPullParserException, NoSuchFieldException, IOException {
        Map<String, Object> map = null;
        switch (flag) {
            case CLIENTLOGIN:
            case CLIENTLOGOUT:
            case GRANTRIGHTS:
            case RIGHT_QUERY:
            case SEND_LOG:
                map = norMalResult(string);
                break;
            case ISSUEDOC:
                map = issueDoc(string);
                break;
        }
        return map;
    }

    //普通的数据解析
    private Map<String, Object> norMalResult(String result) {
        Map map = new HashMap<String, Object>();
        try {
            parser.setInput(new StringReader(result));
            String key = "";
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        key = parser.getName();
                        break;
                    case XmlPullParser.TEXT:
                        if (parser.getText() != null && !"".equals(parser.getText()))
                            map.put(key, parser.getText());
                        break;
                }
                event = parser.next();
            }
            return map;
        } catch (XmlPullParserException e) {
            map.clear();
            map.put(HttpFields.NET_RESULT, HttpFields.NET_REQUEST_FAIL);
            map.put(HttpFields.NET_ERROR, HttpFields.NET_ABNORMAL);
            return map;
        } catch (IOException e) {
            map.clear();
            map.put(HttpFields.NET_RESULT, HttpFields.NET_REQUEST_FAIL);
            map.put(HttpFields.NET_ERROR, e.toString());
            return map;
        }
    }

    //文档组发布解析
    public Map<String, Object> issueDoc(String result) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Map<String, Object> map = null;
            Map<String, String> itemMap = null;
            List<Map<String, Object>> resultList = new ArrayList<>();
            String key = "";
            parser.setInput(new StringReader(result));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        key = parser.getName();
                        if ("issue".equals(key)) {
                            map = new HashMap<>();
                        } else if ("archives".equals(key)) {
                            itemMap = new HashMap<String, String>();
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                itemMap.put(parser.getAttributeName(i), parser.getAttributeValue(i));
                            }
                        } else if ("doc".equals(key)) {
                            itemMap = new HashMap<String, String>();
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                itemMap.put(parser.getAttributeName(i), parser.getAttributeValue(i));
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if ("result".equals(key)) {
                            resultMap.put(key, parser.getText());
                        } else if ("error".equals(key)) {
                            resultMap.put(key, parser.getText());
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if ("archives".equals(parser.getName())) {
                            map.put("archives", itemMap);
                        } else if ("doc".equals(parser.getName())) {
                            map.put("doc", itemMap);
                        } else if ("issue".equals(parser.getName())) {
                            resultList.add(map);
                            resultMap.put("issue", resultList);
                        }
                        break;
                }
                event = parser.next();
            }
            return resultMap;
        } catch (XmlPullParserException e) {
            resultMap.clear();
            resultMap.put(HttpFields.NET_RESULT, HttpFields.NET_REQUEST_FAIL);
            resultMap.put(HttpFields.NET_ERROR, HttpFields.NET_ABNORMAL);
            return resultMap;
        } catch (IOException e) {
            resultMap.clear();
            resultMap.put(HttpFields.NET_RESULT, HttpFields.NET_REQUEST_FAIL);
            resultMap.put(HttpFields.NET_ERROR, e.toString());
            return resultMap;
        }
    }

}
