package docsecuritysdk.eetrust.com.docsecuritysdk.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.eetrust.securedocsdk.SecurityDocumentCallBack;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by sunh on 16-10-27.
 */

public class HttpUtils implements TrustManager, X509TrustManager, HostnameVerifier {
    private static Context mContext = null;
    private static HttpUtils instance = null;


    public static HttpUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (HttpUtils.class) {
                if (instance == null) {
                    instance = new HttpUtils(context);
                }
            }
        }
        return instance;
    }

    private HttpUtils(Context context) {
        HttpUtils.mContext = context;
    }

    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public boolean isServerTrusted(
            java.security.cert.X509Certificate[] certs) {
        return true;
    }

    public boolean isClientTrusted(
            java.security.cert.X509Certificate[] certs) {
        return true;
    }

    public void checkServerTrusted(
            java.security.cert.X509Certificate[] certs, String authType)
            throws java.security.cert.CertificateException {
        return;
    }

    public void checkClientTrusted(
            java.security.cert.X509Certificate[] certs, String authType)
            throws java.security.cert.CertificateException {
        return;
    }

    @Override
    public boolean verify(String urlHostName, SSLSession session) { //允许所有主机
        return true;
    }


    /**
     * 查询权限
     *
     * @param body
     * @param callBackData
     */
    public void query_docRights(Map<String, String> body, final SecurityDocumentCallBack callBackData) {
        if (!ConnectUtil.isConnenct(mContext)) {
            callBackData.onSuccessResponse("网络连接失败，请重新配置");
            return;
        }

        if (HttpUrl.BASE_IP == null)
            HttpUrl.BASE_IP = getServerAddressSP(mContext);
        HttpUrl.GET_RIGHT = HttpUrl.BASE_IP + "/securedoc/clientInterface/clientQuery.do?method=query_docRights";

        urlConnectionPost(HttpUrl.GET_RIGHT, body, XMLPullUtils.RIGHT_QUERY, new com.eetrust.http.HttpUtils.CallBackData1() {
            @Override
            public void CallBackFinish(String response) {
                callBackData.onSuccessResponse(response);
            }

            @Override
            public void CallBackError(String error) {
                callBackData.onErrorResponse(error);
            }
        });

    }


    public void issue_doc(Map<String, String> body, final SecurityDocumentCallBack callBack) {

        if (!ConnectUtil.isConnenct(mContext)) {
            callBack.onErrorResponse("网络连接失败，请重新配置");
            return;
        }

        if (HttpUrl.BASE_IP == null)
            HttpUrl.BASE_IP = getServerAddressSP(mContext);
        HttpUrl.ISSUE_DOC = HttpUrl.BASE_IP + "/securedoc/clientInterface/clientInfoRecv.do?method=issue_doc";

        urlConnectionPost(HttpUrl.ISSUE_DOC, body, XMLPullUtils.ISSUEDOC, new com.eetrust.http.HttpUtils.CallBackData1() {
            @Override
            public void CallBackFinish(String response) {
                callBack.onSuccessResponse(response);
            }

            @Override
            public void CallBackError(String error) {
                callBack.onErrorResponse(error);
            }
        });
    }



    /**
     * 文档授权
     *
     * @param
     * @param
     */
    public void grant_rights(Map<String,String> body, final SecurityDocumentCallBack callBack) {
//        String right = null;
//        try {
//            right = XMLBuildUtils.buildRightGrantXMLString(mContext, grantBean);
//            Log.i("HttpUtils", HttpUrl.RIGHT_GRANT + "&rights=" + right + "&archivesFrom=1");
//        } catch (IOException e) {
//            callBackData.CallBackError(e.toString());
//        }
//
//        int appIdSP = getAppIdSP(mContext);
//        if (HttpFields.SDS_APP_ID == 0 && appIdSP != 0) {
//            HttpFields.SDS_APP_ID = appIdSP;
//        } else {
//            HttpFields.SDS_APP_ID = HttpFields.DEFAULT_APP_ID;
//        }
//
//        Map<String, String> body = new HashMap<String, String>();
//        body.put("rights", right);
//        body.put("archivesFrom", HttpFields.SDS_APP_ID + "");

        if (!ConnectUtil.isConnenct(mContext)) {
            callBack.onErrorResponse("网络连接失败，请重新配置");
            return;
        }

        if (HttpUrl.BASE_IP == null)
            HttpUrl.BASE_IP = getServerAddressSP(mContext);
        HttpUrl.RIGHT_GRANT = HttpUrl.BASE_IP + "/securedoc/clientInterface/clientInfoRecv.do?method=grant_rights";

        urlConnectionPost(HttpUrl.RIGHT_GRANT, body, XMLPullUtils.GRANTRIGHTS, new com.eetrust.http.HttpUtils.CallBackData1() {
            @Override
            public void CallBackFinish(String response) {
                callBack.onSuccessResponse(response);
            }

            @Override
            public void CallBackError(String error) {
                callBack.onErrorResponse(error);
            }
        });

    }


    public void downLoadOffline(Map<String, String> body, com.eetrust.http.HttpUtils.CallBackData1 callBackData) {

    }

    public void logout(final com.eetrust.http.HttpUtils.CallBackData1 callBackData) {
        if (!ConnectUtil.isConnenct(mContext)) {
            callBackData.CallBackError("网络中断，请配置网络");
            System.out.println("网络中断，请配置网络");
            return;
        }

        if (HttpUrl.BASE_IP == null)
            HttpUrl.BASE_IP = getServerAddressSP(mContext);
        HttpUrl.LOG_OUT = HttpUrl.BASE_IP + "/securedoc/clientInterface/clientLogout.do?method=clientLogOut";

        urlConnectionPost(HttpUrl.LOG_OUT, null, XMLPullUtils.CLIENTLOGOUT, new com.eetrust.http.HttpUtils.CallBackData1() {
            @Override
            public void CallBackFinish(String stringObjectMap) {
                callBackData.CallBackFinish(stringObjectMap);
//                System.out.println(stringObjectMap.get(HttpFields.NET_RESULT).toString());
            }

            @Override
            public void CallBackError(String error) {
                callBackData.CallBackError(error);
                System.out.println(error);
            }
        });

    }

    /**
     * HttpURLConnection的post请求方法
     *
     * @param urlPost      url地址
     * @param body         参数集合
     * @param callBackData 回调
     */
    private void urlConnectionPost(final String urlPost, final Map<String, String> body, int flag, final com.eetrust.http.HttpUtils.CallBackData1 callBackData) {
        if (!ConnectUtil.isConnenct(mContext)) {
            callBackData.CallBackError("网络连接失败，请重新配置");
            return;
        }
        HttpURLConnection connection = null;
        try {
            //调用URL对象的openConnection方法获取HttpURLConnection的实例
            URL url = new URL(urlPost);
            TrustManager[] trustAllCerts = new TrustManager[1];
            TrustManager tm = new HttpUtils(mContext);
            trustAllCerts[0] = tm;
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                    .getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc
                    .getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier((HostnameVerifier) tm);

            connection = (HttpURLConnection) url.openConnection();
            //设置请求方式,GET或POST
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            //获取SP中的session,添加入connection
            connection.setRequestProperty("Cookie", getSessionSP(mContext));
            Log.i("HttpUtils", "urlConnectionPost: " + getSessionSP(mContext));
            //如果body中含有参数,则加入输出流,否则不加
            if (body != null) {
                DataOutputStream data = new DataOutputStream(connection.getOutputStream());
                data.writeBytes(getBody(body));
                data.flush();
                data.close();
            }
            //
            //设置连接超时,读取超时时间,单位为毫秒(ms)
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);

            // 判断服务器给我们返回的状态信息。
            // 200 成功 302 从定向 404资源没找到 5xx 服务器内部错误

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                //利用链接成功的 connection 得到输入流
                //getInputStream方法获取服务器返回的输入流
                InputStream inputStream = connection.getInputStream();
                //使用BufferedReader对象读取返回的输入流
                //按行读取,存储在StringBuilder对象Response中
                //InputStreamReader(inputStream,"gbk")中要对相应的xml格式进行解析,与xml头一致
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "gbk"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
//                Map<String, Object> stringObjectMap = XMLPullUtils.getInstance().xml2Obj(response.toString(), flag);
//                Log.i("HttpUtils", "run: " + stringObjectMap.toString());
                //解析完成,给调用此方法的地方一个成功回调
                callBackData.CallBackFinish(response.toString());
            } else if (code == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                // 状态码 != 200 , 请求失败
                callBackData.CallBackError("请求超时");
            } else {
                callBackData.CallBackError("请求失败");
            }

        } catch (MalformedURLException e) {
            callBackData.CallBackError(e.toString());
        } catch (IOException e) {
            callBackData.CallBackError(e.toString());
        } catch (NoSuchAlgorithmException e) {
            callBackData.CallBackError(e.toString());
        } catch (KeyManagementException e) {
            callBackData.CallBackError(e.toString());
        } finally {
            if (connection != null) {
                //结束后,关闭连接
                connection.disconnect();
            }
        }
    }

    /**
     * 发送日志
     * @param body
     * @param
     */
    public void sendLog(Map<String, String> body, final SecurityDocumentCallBack callBack){
        if (!com.eetrust.http.ConnectUtil.isConnenct(mContext)) {
            callBack.onErrorResponse("网络连接失败，请重新配置");
            return;
        }

        if (HttpUrl.BASE_IP == null)
            HttpUrl.BASE_IP = getServerAddressSP(mContext);
        HttpUrl.LOG_SEND = com.eetrust.http.HttpUrl.BASE_IP + "/securedoc/clientInterface/clientInfoRecv.do?method=send_log";

        urlConnectionPost(HttpUrl.LOG_SEND, body,XMLPullUtils.SEND_LOG, new com.eetrust.http.HttpUtils.CallBackData1() {


            @Override
            public void CallBackFinish(String requestString) {
                callBack.onSuccessResponse(requestString);
            }

            @Override
            public void CallBackError(String error) {
                callBack.onErrorResponse(error);
            }
        });
    }
    /**
     *获取默认权限
     * @param body
     * @param
     */
    public void getDefultRights(Map<String, String> body, final SecurityDocumentCallBack callBack){
        if (!com.eetrust.http.ConnectUtil.isConnenct(mContext)) {
            callBack.onErrorResponse("网络连接失败，请重新配置");
            return;
        }

        if (HttpUrl.BASE_IP == null)
            HttpUrl.BASE_IP = getServerAddressSP(mContext);
          HttpUrl.Defult_GRANT = HttpUrl.BASE_IP+ "/securedoc//clientInterface/clientQuery.do?method=query_archivesPolicyRights";

        urlConnectionPost(HttpUrl.Defult_GRANT, body,XMLPullUtils.SEND_LOG, new com.eetrust.http.HttpUtils.CallBackData1() {


            @Override
            public void CallBackFinish(String requestString) {
                callBack.onSuccessResponse(requestString);
            }

            @Override
            public void CallBackError(String error) {
                callBack.onErrorResponse(error);
            }
        });
    }
    /**
     * 单独的登录方法, 关键是用来保存session
     *
     * @param loginName
     * @param ticket
     * @param callBackData
     */
    public void toLoginServer(String loginName, String ticket, final com.eetrust.http.HttpUtils.CallBackData callBackData) {

        if (!ConnectUtil.isConnenct(mContext)) {
            callBackData.CallBackError("网络连接失败，请重新配置");
            return;
        }

        if (HttpUrl.BASE_IP == null)
            HttpUrl.BASE_IP = getServerAddressSP(mContext);
        HttpUrl.SSO_TICKET = HttpUrl.BASE_IP + "/securedoc/clientInterface/clientLogin.do?loginType=sso_ticket";

        Map<String, String> body = new HashMap<String, String>();
        body.put("loginName", loginName);
        body.put("ticket", ticket);
        body.put("type", HttpFields.SSO_TYPE);

        HttpURLConnection connection = null;
        try {
            //调用URL对象的openConnection方法获取HttpURLConnection的实例
            URL url = new URL(HttpUrl.SSO_TICKET);
            //调用URL对象的openConnection方法获取HttpURLConnection的实例
            TrustManager[] trustAllCerts = new TrustManager[1];
            TrustManager tm = new HttpUtils(mContext);
            trustAllCerts[0] = tm;
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                    .getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc
                    .getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier((HostnameVerifier) tm);

            connection = (HttpURLConnection) url.openConnection();
            //设置请求方式,GET或POST
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            DataOutputStream data = new DataOutputStream(connection.getOutputStream());
            data.writeBytes(getBody(body));
            data.flush();
            data.close();
            //
            //设置连接超时,读取超时时间,单位为毫秒(ms)
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            // 3.判断服务器给我们返回的状态信息。
            connection.connect();
            // 200 成功 302 从定向 404资源没找到 5xx 服务器内部错误
            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                //利用链接成功的 connection 得到输入流
                //getInputStream方法获取服务器返回的输入流
                InputStream inputStream = connection.getInputStream();
                //获取当前连接的session
                String session_id = connection.getHeaderField("Set-Cookie");
                //将session保存到SP中
                Log.i("Session", "toLoginServer: " + session_id);
                saveSessionSP(mContext, session_id);
                //使用BufferedReader对象读取返回的输入流
                //按行读取,存储在StringBuilder对象Response中
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "gbk"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Map<String, Object> stringObjectMap = XMLPullUtils.getInstance().xml2Obj(response.toString(), 1);
                Log.i("HttpUtils", "run: " + stringObjectMap.toString());
                callBackData.CallBackFinish(stringObjectMap);
            } else if (code == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                callBackData.CallBackError("请求超时");
            } else {
                callBackData.CallBackError("请求失败");
            }

        } catch (MalformedURLException e) {
            callBackData.CallBackError(e.toString());
        } catch (IOException e) {
            callBackData.CallBackError(e.toString());
        } catch (IllegalAccessException e) {
            callBackData.CallBackError(e.toString());
        } catch (XmlPullParserException e) {
            callBackData.CallBackError(e.toString());
        } catch (InstantiationException e) {
            callBackData.CallBackError(e.toString());
        } catch (NoSuchFieldException e) {
            callBackData.CallBackError(e.toString());
        } catch (NoSuchAlgorithmException e) {
            callBackData.CallBackError(e.toString());
        } catch (KeyManagementException e) {
            callBackData.CallBackError(e.toString());
        } finally {
            if (connection != null) {
                //结束后,关闭连接
                connection.disconnect();
            }
        }


    }

//    /**
//     * 网络请求的回调接口
//     */
//    public interface CallBackData {
//        //请求结束
//        void CallBackFinish(Map<String, Object> stringObjectMap);
//
//        //请求错误
//        void CallBackError(String error);
//    }

    /**
     * 将Map集合解析成String
     *
     * @param map 传进来的参数
     * @return 参数的字符串
     */
//    private static String getBody(Map<String, String> map) {
//        StringBuilder initUrl = new StringBuilder();
//        //遍历key解析map
//        for (String key : map.keySet()) {
//            initUrl.append("&" + key + "=" + map.get(key));
//        }
//        return initUrl.toString();
//    }
    /**
     * 将Map集合解析成String
     *
     * @param map 传进来的参数
     * @return 参数的字符串
     */
    private static String getBody(Map<String, String> map) throws UnsupportedEncodingException {
        StringBuilder initUrl = new StringBuilder();
//        String url = "";
        //遍历key解析map
        for (String key : map.keySet()) {
            String keyValue = map.get(key);
            if (keyValue == null) {
                keyValue = "";
            }
//            url += "&" + key + "=" + URLEncoder.encode(keyValue, "UTF-8");
            initUrl.append("&");
            initUrl.append(key);
            initUrl.append("=");
            initUrl.append(URLEncoder.encode(keyValue, "UTF-8"));
        }
        return initUrl.toString();
//        return url;
    }
    /**
     * 保存session
     *
     * @param context   传进来的上下文
     * @param sessionId 登录成功以后返回的session
     */
    private static void saveSessionSP(Context context, String sessionId) {
        //获取当前context的SP
        SharedPreferences sp = context.getSharedPreferences(HttpFields.SDS_SP_NAME, Context.MODE_PRIVATE);
        //获取SP的Editor
        SharedPreferences.Editor edit = sp.edit();
        //key-value写入SP
        edit.putString(HttpFields.SDS_SERVER_SESSION, sessionId);
        //最后要提交
        edit.commit();
    }

    /**
     * 获取当前用户的session
     *
     * @param context 传进来的上下文
     * @return 当前用户session
     */
    private static String getSessionSP(Context context) {
        //获取当前context的SP
        SharedPreferences sp = context.getSharedPreferences(HttpFields.SDS_SP_NAME, Context.MODE_PRIVATE);
        //根据key获取value
        return sp.getString(HttpFields.SDS_SERVER_SESSION, "");
    }

    /**
     * 获取SP保存的服务器IP
     *
     * @param context 传进来的上下文
     * @return 服务器地址
     */
    private static String getServerAddressSP(Context context) {
        //获取当前context的SP
        SharedPreferences sp = context.getSharedPreferences(HttpFields.SDS_SP_NAME, Context.MODE_PRIVATE);
        //根据key获取value
        return sp.getString(HttpFields.SDS_SERVER_ADRESS, "");
    }

    /**
     * 获取SP保存的AppId
     * @param context 传进来的上下文
     * @return appId
     */
    private static int getAppIdSP(Context context) {
        //获取当前context的SP
        SharedPreferences sp = context.getSharedPreferences(HttpFields.SDS_SP_NAME, Context.MODE_PRIVATE);
        //根据key获取value
        return sp.getInt(HttpFields.SDS_APP_ID_SP, 0);
    }


}
