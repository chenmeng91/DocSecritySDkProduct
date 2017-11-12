package com.eetrust.securedocsdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.eetrust.bean.DeptBean;
import com.eetrust.bean.IsSecureDocBean2;
import com.eetrust.bean.IssueGrantXMLBean;
import com.eetrust.bean.RightAndKeyBean;
import com.eetrust.bean.UserBean;
import com.eetrust.bean.UserLog;
import com.eetrust.db_operation.DbService;
import com.eetrust.db_operation.RightAndkeyTable;
import com.eetrust.http.ConnectUtil;
import com.eetrust.http.HttpFields;
import com.eetrust.http.HttpUrl;
import com.eetrust.http.HttpUtils;
import com.eetrust.http.XMLBuildUtils;
import com.eetrust.http.XMLPullUtils;
import com.eetrust.model.NetRequestModel;
import com.eetrust.utils.DateUtils;
import com.eetrust.utils.NetRequestErrorUtile;
import com.eetrust.utils.OpenFile;
import com.eetrust.utils.StringUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.eetrust.http.HttpUrl.isOpenFile;
import static com.eetrust.http.HttpUrl.versionId;

/**
 * Created by chenmeng on 2016/11/2.
 */

public class MSD {
    private Context mContext = null;
    private volatile static MSD instance = null;
    private DbService dbService;//数据库类
    private CryptoUtil cryptoUtil;//jni类
    private HttpUtils httpUtils;
    private static final String TAG = "MSD";
    private String loginName;
    private static Map<String,String> defultRights = new HashMap<>();
    private static SecurityDocumentInterface mSecDocInterface;

    private MSD(Context mContext) {
        this.mContext = mContext;
    }

    public static MSD getInstance(Context context) {
        if (instance == null) {
            synchronized (MSD.class) {
                if (instance == null) {
                    instance = new MSD(context);
                }
            }
        }
        if (instance != null) {
            instance.init();
        }
        return instance;
    }

    private void init() {
        dbService = DbService.getInstance(mContext);
        cryptoUtil = CryptoUtil.getInstance();
        httpUtils = HttpUtils.getInstance(mContext);
        //数据库 网络轻轻的初始化过程
    }

    public interface AuthenticateCallBack {
        void CallBackFinish();

        void CallBackError(int errorCode, String errorInfo);
    }

    public interface EncryptCallBack {
        void CallBackFinish();

        void CallBackError(int errorCode, String errorInfo);
    }


    public interface IsCiphertextCallBack {//判断是否是加密文件的回调

        void CallBackFinish(boolean isCipherText);

        void CallBackError(int errorCode, String errorInfo);

    }


    public interface LogoutCallBack {
        void CallBackFinish();

        void CallBackError(int errorCode, String errorInfo);
    }

    public interface OpenFileCallBack {
        void CallBackFinish();

        void CallBackError(int errorCode, String errorInfo);
    }


    public interface AuthorizeCallBack{
        void CallBackFinish();

        void CallBackError(int errorCode, String errorInfo);
    }

    /**
     * 配置接口一 sdk有网络请求
     * 用户自传appID
     *
     * @param serverIp   服务器IP
     * @param serverPort 服务器端口
     * @param isHttps    是否是Https协议
     * @param
     */
    public void sds_config(String loginName,String userID, String serverIp, String serverPort, String authorities, boolean isHttps) {
        if (serverIp == null || serverIp.equals("") || serverPort == null || serverPort.equals("")) {
            return ;
        }
        this.loginName = loginName;
        HttpUrl.loginName = loginName;
//        HttpUrl.isOpenFile = isOpenFile;
        String serverAddress = "";
        if (isHttps) {
            serverAddress = "https://" + serverIp + ":" + serverPort;
        } else {
            serverAddress = "http://" + serverIp + ":" + serverPort;
        }
        HttpUrl.BASE_IP = serverAddress;
        HttpFields.IS_NET_REQUEST = true;
        HttpFields.authorities = authorities;
        HttpFields.userId = userID;
        return ;
    }

    /**
     * 配置接口二 sdk无网络请求
     *
     * @param loginName   登录名
     * @param authorities 自定义权限
     * @return
     */
    public void sds_config(String loginName,String userID, String authorities, SecurityDocumentInterface securityDocumentInterface) {
        this.loginName = loginName;
        HttpUrl.loginName = loginName;
        this.mSecDocInterface = securityDocumentInterface;
        Log.d("aa", "aa");
        HttpFields.IS_NET_REQUEST = false;
        HttpFields.authorities = authorities;
        HttpFields.userId = userID;
        sendLog(loginName,DbService.getInstance(mContext).selectAllLog(loginName));//发送离线日志
    }

    /**
     * 身份认证接口 sdk有网络请求时使用的接口
     *
     * @param ticket
     * @param
     */
    public void sds_authenticate(final String ticket, final AuthenticateCallBack authenticateCallBack) {
        if (authenticateCallBack == null) {
            return;
        }

        if (loginName == null || loginName.equals("")) {
            authenticateCallBack.CallBackError(SecureDocError.E_ParamsError, "loginName不能为空");
            return;
        }

        //初始化当前用户
//        HttpUrl.loginName = loginName;
//        this.loginName = loginName;
//        saveLoginNameSP(mContext, loginName);
        final Handler handler = new Handler() {//切换到主线程
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case SecureDocError.E_OK://成功
                        authenticateCallBack.CallBackFinish();
                        sendLog(loginName,DbService.getInstance(mContext).selectAllLog(loginName));//发送离线日志
                        break;
                    case SecureDocError.E_AuthenticationFail:
                    case SecureDocError.E_NetReqTimeout:
                    case SecureDocError.E_NetReqFail:
                    case SecureDocError.E_ConnectionFail:
                        authenticateCallBack.CallBackError(msg.what, StringUtils.getErrorMessage((String) msg.obj,"授权失败"));
//                        if(StringUtils.isChineseLetter((String) msg.obj)){
//                            authenticateCallBack.CallBackError(msg.what, (String) msg.obj);
//                        }else {
//                            authenticateCallBack.CallBackError(msg.what, "授权失败");
//                        }

                        break;
                    default:
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                httpUtils.toLoginServer(loginName, ticket, new HttpUtils.CallBackData() {
                    @Override
                    public void CallBackFinish(Map<String, Object> stringObjectMap) {
                        Log.i(TAG, "CallBackFinish: " + stringObjectMap.toString());
                        String result = (String) stringObjectMap.get(HttpFields.NET_RESULT);
                        if (result != null && result.equals(HttpFields.NET_REQUEST_SUCESS)) {
                            sendMsg(SecureDocError.E_OK, "");
                        } else {
                            sendMsg(SecureDocError.E_AuthenticationFail, (String) stringObjectMap.get(HttpFields.NET_ERROR));
                        }
                    }

                    @Override
                    public void CallBackError(String error) {
                        if ("请求超时".equals(error)) {
                            sendMsg(SecureDocError.E_NetReqTimeout, SecureDocError.EDesc[SecureDocError.E_NetReqTimeout]);
                        } else if ("网络连接失败，请重新配置".equals(error)) {
                            sendMsg(SecureDocError.E_ConnectionFail, error);
                        } else {
                            sendMsg(SecureDocError.E_NetReqFail, error);
                        }
                    }
                });
            }

            private void sendMsg(int errorCode, String errorInfo) {
                Message msg = Message.obtain();
                msg.what = errorCode;
                msg.obj = errorInfo;
                handler.sendMessage(msg);
            }
        }).start();


    }

    /**
     * 是否为加密文件接口
     *
     * @param docPath 文件路径
     */
    public void sds_isCiphertext(final String docPath, final IsCiphertextCallBack isCiphertextCallBack) {
        if (isCiphertextCallBack == null) {
            return;
        }
        if (docPath == null || "".equals(docPath)) {
            isCiphertextCallBack.CallBackError(SecureDocError.E_ParamsError, "文件路径为空");
            return;
        }

        File file = new File(docPath);
        if ((!file.exists()) || (!file.isFile())) {
            isCiphertextCallBack.CallBackError(SecureDocError.E_NotExistFile, SecureDocError.EDesc[SecureDocError.E_NotExistFile]);
            return;
        }
        final Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        ETResult etResult = (ETResult) msg.obj;
                        if (etResult.resultCode == SecureDocError.E_OK) {
                            isCiphertextCallBack.CallBackFinish(true);
                        } else if (etResult.resultCode == SecureDocError.E_NotCiphertext) {
                            isCiphertextCallBack.CallBackFinish(false);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                IsSecureDocBean2 isSecureDocBean = cryptoUtil.isSecureDoc(docPath);//判断是否是加密文件
                if (isSecureDocBean.secureDoc == 1) {//是加密文件
                    Log.d(TAG, "archiveId:" + isSecureDocBean.archiveId + "");
                    Log.d(TAG, "archiveId:" + isSecureDocBean.docId + "");
                    sendHandlerMessage(handler, SecureDocError.E_OK, null, null,1);
                } else {//不是加密文件
                    sendHandlerMessage(handler, SecureDocError.E_NotCiphertext, null, null,1);//不是加密文件
                }
            }
        }).start();
    }


    /**
     * 解密打开文件
     *
     * @param cipherPath
     * @param openFileCallBack
     */
    public void sds_openFile(final String cipherPath, final String plainPath, final boolean isOpenFile, final OpenFileCallBack openFileCallBack) {
        final long[] archiveId = {1L};
        final long[] docId = {1L};
        if (openFileCallBack == null) {
            return;
        }
        if (cipherPath == null || "".equals(cipherPath)) {
            openFileCallBack.CallBackError(SecureDocError.E_ParamsError, "密文文件路径为空");
            return;
        }

        File file = new File(cipherPath);
        if ((!file.exists()) || (!file.isFile())) {
            openFileCallBack.CallBackError(SecureDocError.E_NotExistFile, SecureDocError.EDesc[SecureDocError.E_NotExistFile]);
            return;
        }

        final Handler handler = new Handler() {//切换到主线程
            public void handleMessage(android.os.Message msg) {
                // 处理消息时需要知道是成功的消息还是失败的消息
                switch (msg.what) {
                    case 1:
                        openFileResultHandler(msg, isOpenFile, archiveId, docId, openFileCallBack);
                        break;
                    default:
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                //根据cipherPath对文件进行解析得到archiveId 和 docId
                //网络请求得到key 如果失败了就去数据库中得到key，如果数据库中也没就解密失败,如果成功了就获得key和right 存入
                //根据key对文件进行解密
                IsSecureDocBean2 isSecureDocBean = CryptoUtil.getInstance().isSecureDoc(cipherPath);
                if (isSecureDocBean.secureDoc == 1) {//是加密文件
                    archiveId[0] = isSecureDocBean.archiveId;//6193;
                     docId[0]= isSecureDocBean.docId;//2356;
                    final int appId = isSecureDocBean.appId;
                    Map<String, String> body = new HashMap<String, String>();
                    body.put(HttpFields.ARCHIVES_ID, archiveId[0] + "");
                    body.put(HttpFields.DOC_ID, docId[0] + "");
                    body.put(HttpFields.ARCHIVES_FROM_APPID, appId + "");
                    body.put(HttpFields.LOGIN_NAME, loginName);//
                    if (HttpFields.IS_NET_REQUEST) {
                        HttpUtils.getInstance(mContext).query_docRights(body, new HttpUtils.CallBackData() {
                            @Override
                            public void CallBackFinish(Map<String, Object> stringObjectMap) {
                                queryRightsSuccess(stringObjectMap, docId[0], archiveId[0], cipherPath, plainPath, handler,isOpenFile);
                            }

                            @Override
                            public void CallBackError(String error) {
                                Log.d(TAG,"获取权限失败："+error);
                                getKeyByDB(archiveId[0], docId[0], cipherPath, plainPath, handler, SecureDocError.E_NetReqFail, NetRequestErrorUtile.getInstance().getErrorinf(error),isOpenFile);
                            }
                        });
                    } else {
                        mSecDocInterface.authorityQuery(mContext, body, new SecurityDocumentCallBack() {
                            @Override
                            public void onSuccessResponse(String requestString) {
                                Map<String, Object> stringObjectMap = parseXml(requestString, archiveId[0], docId[0], cipherPath, null, handler);
                                queryRightsSuccess(stringObjectMap, docId[0], archiveId[0], cipherPath, plainPath, handler,isOpenFile);
                            }

                            @Override
                            public void onErrorResponse(String error) {
                                Log.d(TAG, "CallBackError: " + error);
                                getKeyByDB(archiveId[0], docId[0], cipherPath, plainPath, handler, SecureDocError.E_NetReqFail, error,isOpenFile);
                            }
                        });
                    }


                } else {
                    //不是加密文件
                    sendHandlerMessage(handler, SecureDocError.E_NotCiphertext, null, null,1);
                }
            }
        }).start();
    }

    private void openFileResultHandler(Message msg, boolean isOpenFile, long[] archiveId, long[] docId, OpenFileCallBack openFileCallBack) {
        ETResult etResult = (ETResult) msg.obj;
        List<UserLog> userLogs = new ArrayList<>();
        if (etResult.resultCode == SecureDocError.E_OK) {
//                            openFileCallBack.CallBackFinish();
//                            mSecDocInterface.openFileByWPS();//调用打开文件的接口.
            if (isOpenFile) {
                userLogs.add(getLogBean(archiveId[0], docId[0], HttpFields.OPER_READ, HttpFields.OPER_SUCCESS, null));
                OpenFile.getInstence(mContext).openAttachment(etResult.openFileUrl, etResult.resultData);
            } else {
                userLogs.add(getLogBean(archiveId[0], docId[0], HttpFields.OPER_DECRYPT, HttpFields.OPER_SUCCESS, null));
                openFileCallBack.CallBackFinish();
            }
        } else {
            openFileCallBack.CallBackError(etResult.resultCode,StringUtils.getErrorMessage(etResult.errorInfo, "解密失败"));
//            if(StringUtils.isChineseLetter(etResult.errorInfo)){
//                openFileCallBack.CallBackError(etResult.resultCode, etResult.errorInfo);
//            }else {
//                openFileCallBack.CallBackError(etResult.resultCode, "解密失败");
//            }

            if (isOpenFile) {
                userLogs.add(getLogBean(archiveId[0], docId[0], HttpFields.OPER_READ, HttpFields.OPER_FAIL, etResult.errorInfo));
            } else {
                userLogs.add(getLogBean(archiveId[0], docId[0], HttpFields.OPER_DECRYPT, HttpFields.OPER_FAIL, etResult.errorInfo));
            }
        }

        sendLog(loginName, userLogs);
    }

    private void queryRightsSuccess(Map<String, Object> stringObjectMap, long docId, long archiveId, String cipherPath, String plainPath, Handler handler,boolean isopenFile) {
        if (stringObjectMap != null) {
            String result = (String) stringObjectMap.get(HttpFields.NET_RESULT);
            if (result != null && result.equals(HttpFields.NET_REQUEST_SUCESS)) {
                //获取权限成功
                String rights = (String) stringObjectMap.get(HttpFields.RIGHT_RIGHTS);
                String key = (String) stringObjectMap.get(HttpFields.RIGHT_KEY);
                //将获取的key和right存储到数据库
                dbService.insertOrUpdateRightAndkeyTable(new RightAndKeyBean(1L, loginName, docId, archiveId, rights, key));
                isHaveRightRead(rights, key, cipherPath, plainPath, handler,isopenFile);//判断是否有权限
            } else if (result == null) {
                //权限获取异常
                getKeyByDB(archiveId, docId, cipherPath, null, handler, SecureDocError.E_NetReqFail, null,isopenFile);
            } else {
                //result = 0 权限获取失败
                String error = (String) stringObjectMap.get(HttpFields.RIGHT_ERROR);
                Log.d(TAG, "error: " + error);
                getKeyByDB(archiveId, docId, cipherPath, plainPath, handler, SecureDocError.E_NetReqFail, error+"",isopenFile);
            }
        }
    }

    public Map<String, Object> parseXml(String requestString, long archiveId, long docId, String cipherPath, String plainPath, Handler handler) {
        try {

            Map<String, Object> stringObjectMap = XMLPullUtils.getInstance().xml2Obj(requestString, XMLPullUtils.RIGHT_QUERY);
            return stringObjectMap;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            getKeyByDB(archiveId, docId, cipherPath, plainPath, handler, SecureDocError.E_NetReqFail, e.toString(),isOpenFile);
        } catch (InstantiationException e) {
            e.printStackTrace();
            getKeyByDB(archiveId, docId, cipherPath, plainPath, handler, SecureDocError.E_NetReqFail, e.toString(),isOpenFile);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            getKeyByDB(archiveId, docId, cipherPath, plainPath, handler, SecureDocError.E_NetReqFail, e.toString(),isOpenFile);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            getKeyByDB(archiveId, docId, cipherPath, plainPath, handler, SecureDocError.E_NetReqFail, e.toString(),isOpenFile);
        } catch (IOException e) {
            e.printStackTrace();
            getKeyByDB(archiveId, docId, cipherPath, plainPath, handler, SecureDocError.E_NetReqFail, e.toString(),isOpenFile);
        }

        return null;
    }

    //判断是否有权限阅读
    private void isHaveRightRead(String rights, String key, String cipherPath, String plainPath, Handler handler,boolean isOpenFile) {
        boolean ishaveRights =false;
        if(isOpenFile&&rights != null && rights.contains(HttpFields.READ_RIGHT + ",")){//阅读权限判断
            ishaveRights = true;
        }else if(!isOpenFile&&rights != null && rights.contains(HttpFields.DECRYPT_RIGHT + ",")){//解密权限判断
            ishaveRights = true;
        }else {
            ishaveRights = false;
        }
        if (ishaveRights) {//有阅读权限
            int decryptResult = 0;
//            if (plainPath == null || "".equals(plainPath)) {//解密到内存中
//                decryptResult = cryptoUtil.DecryptFile(key, cipherPath, "/storage/emulated/0/pppencode.docx");
//            } else {//解密到文件路径中
                decryptResult = cryptoUtil.DecryptFile(key, cipherPath, plainPath);
//            }


            if (decryptResult == 1) {
                //解密成功
                sendHandlerMessage(handler, SecureDocError.E_OK, rights, plainPath,1);
            } else {
                //解密失败
                sendHandlerMessage(handler, SecureDocError.E_DecryptFail, null, null,1);
            }
        } else {//没有权限
            sendHandlerMessage(handler, SecureDocError.E_NoRight, null, null,1);
        }
    }


    private void sendHandlerMessage(Handler handler, int errorCode, String ResultData, String openFileUrl,int what) {
        Message msg = Message.obtain();
        ETResult etResult = new ETResult();
        if (errorCode == SecureDocError.E_OK) {
            if (ResultData != null) {
                etResult.setOK(ResultData);
            }
            etResult.openFileUrl = openFileUrl;
        } else if (ResultData == null) {
            etResult.setError(errorCode);
        } else {
            etResult.setError(errorCode, ResultData);
        }

        msg.obj = etResult;
        msg.what = 1;
        handler.sendMessage(msg);
    }

    /**
     * 在数据库中得到key并且解密
     *
     * @param
     * @param archiveId
     * @param docId
     * @param cipherPath
     * @param plainPath
     * @param handler
     */
    private void getKeyByDB(long archiveId, long docId, String cipherPath, String plainPath, Handler handler, int resultCode,String errorInformation,boolean isOpenFile) {
        List<RightAndKeyBean> rightAndKeyBeanList = dbService.selectRightAndkeyTable(loginName, archiveId, docId, RightAndkeyTable.ASC);//查询key值
        if (rightAndKeyBeanList != null && rightAndKeyBeanList.size() > 0) {//有数据key缓存
            String key_db = rightAndKeyBeanList.get(0).getKey();
            String rights = rightAndKeyBeanList.get(0).getRight();
            isHaveRightRead(rights, key_db, cipherPath, plainPath, handler,isOpenFile);//判断是否有权限阅读
        } else {
            if (errorInformation == null) {
                sendHandlerMessage(handler, resultCode, null, null,1);
            } else {
                sendHandlerMessage(handler, resultCode, errorInformation, null,1);
            }
        }
    }



    /**
     * 授权接口
     * @param cipherPath
     * @param authorizeCallBack
     */
    public void sds_accredit(final String cipherPath, final List<UserBean> userBeens, final List<DeptBean> deptBeens, final AuthorizeCallBack authorizeCallBack){

        if (cipherPath == null || cipherPath.equals("")) {
            authorizeCallBack.CallBackError(SecureDocError.E_ParamsError, "cipherPath不能为空");
            return;
        }

        File plainFile = new File(cipherPath);
        if (!plainFile.exists() || !plainFile.isFile()) {
            authorizeCallBack.CallBackError(SecureDocError.E_NotExistFile, "密文不存在");
            return;
        }

        final Handler handler = new Handler() {//切换到主线程
            public void handleMessage(android.os.Message msg) {
                ETResult etResult =(ETResult) msg.obj;
                 if(etResult.resultCode==SecureDocError.E_OK){
                    authorizeCallBack.CallBackFinish();
                }else {
                    authorizeCallBack.CallBackError(msg.what, StringUtils.getErrorMessage(etResult.errorInfo,"授权失败"));
                }

            }
        };

        NetRequestModel.getInstance().queryRights(mContext, cipherPath, mSecDocInterface, handler, new NetRequestModel.NetCallresult() {
            @Override
            public void success(Map<String, Object> stringObjectMap) {
                //获取权限成功
                String rights = (String) stringObjectMap.get(HttpFields.RIGHT_RIGHTS);
                if(rights!=null&&!StringUtils.isCanAccerdit(rights,userBeens,deptBeens)){
                    sendHandlerMessage(handler,SecureDocError.E_NoRight,"存在不被允许授予的权限，请修改！",null,1);
                }else if(rights!=null&&rights.contains(HttpFields.DISTRIBUTE_RIGHT+",")){
                    accerditDealer(cipherPath, userBeens, deptBeens, handler);
                }else {
                    sendHandlerMessage(handler,SecureDocError.E_NoRight,"没有授权权限",null,1);
                }

            }

            @Override
            public void requestFail(String error, long archiveId, long docId) {
                List<RightAndKeyBean> rightAndKeyBeanList = dbService.selectRightAndkeyTable(loginName, archiveId, docId, RightAndkeyTable.ASC);//查询key值
                if (rightAndKeyBeanList != null && rightAndKeyBeanList.size() > 0) {//有数据key缓存
                    String rights = rightAndKeyBeanList.get(0).getRight();
                    if(rights.contains(HttpFields.DECRYPT_RIGHT+",")){
                        accerditDealer(cipherPath, userBeens, deptBeens, handler);
                    }else {
                        sendHandlerMessage(handler,SecureDocError.E_NoRight,"没有授权权限",null,1);
                    }

                }else {
                    sendHandlerMessage(handler,SecureDocError.E_QueryRightsFail,StringUtils.getErrorMessage(error,"权限获取失败"),null,1);
                }
            }



            @Override
            public void notNotCiphertext() {
                sendHandlerMessage(handler, SecureDocError.E_NotCiphertext, null, null,1);
            }

            @Override
            public void buildXMLFail() {

            }
        });

    }

    private void accerditDealer(String cipherPath, List<UserBean> userBeens, List<DeptBean> deptBeens, final Handler handler) {
        //根据cipherPath对文件进行解析得到archiveId 和 docId
        //网络请求得到key 如果失败了就去数据库中得到key，如果数据库中也没就解密失败,如果成功了就获得key和right 存入
        //根据key对文件进行解密

        IsSecureDocBean2 isSecureDocBean = CryptoUtil.getInstance().isSecureDoc(cipherPath);
        if (isSecureDocBean.secureDoc == 1) {//是加密文件
            long archiveId = isSecureDocBean.archiveId;//6193;
            Log.d(TAG,"授权的文档组archiveId:"+archiveId);
            long docId = isSecureDocBean.docId;//2356;
            Log.d(TAG,"授权的文档docId:"+archiveId);
            final int appId = isSecureDocBean.appId;
            addDefaultRights(userBeens,deptBeens);

            NetRequestModel.getInstance().accreditNetRequest(mContext,isSecureDocBean,userBeens,deptBeens,mSecDocInterface, new NetRequestModel.NetCallresult() {
                @Override
                public void success(Map<String, Object> stringObjectMap) {
                    authorizeSuccess(stringObjectMap, handler);
                }

                @Override
                public void requestFail(String error, long archiveId, long docId) {
                    authorizeErrorDeal(NetRequestErrorUtile.getInstance().getErrorinf(error), handler);
                }


                @Override
                public void notNotCiphertext() {

                }

                @Override
                public void buildXMLFail() {
                    sendHandlerMessage(handler, SecureDocError.E_AuthorizeFail,"生成XML失败",null,1);
                }
            });
//                    String right = null;
//                    try {
//                        right = XMLBuildUtils.buildRightGrantXMLString(mContext, archiveId + "", userBeens, deptBeens);
//                        Log.i("HttpUtils", HttpUrl.RIGHT_GRANT + "&rights=" + right + "&archivesFrom=1");
//                    } catch (IOException e) {
//                        sendHandlerMessage(handler,SecureDocError.E_AuthorizeFail,"生成XML失败",null,1);
//                    }
//
//                    Map<String, String> grantBody = new HashMap<String, String>();
//                    grantBody.put("rights", right);
//                    grantBody.put("archivesFrom", HttpFields.SDS_APP_ID + "");
//                    if (HttpFields.IS_NET_REQUEST) {
//                        HttpUtils.getInstance(mContext).grant_rights(grantBody, new HttpUtils.CallBackData() {
//                            @Override
//                            public void CallBackFinish(Map<String, Object> stringObjectMap1) {
////                                accreditSuccess(stringObjectMap);
//                                authorizeSuccess(stringObjectMap1, handler);
//                            }
//
//                            @Override
//                            public void CallBackError(String error) {
//                                Log.d(TAG,"授权失败："+error);
//
//                                authorizeErrorDeal(NetRequestErrorUtile.getInstance().getErrorinf(error), handler);
////                                dealError(error);
//                            }
//                        });
//                    } else {
//                        mSecDocInterface.accredit(mContext, grantBody, new SecurityDocumentCallBack() {
//                            @Override
//                            public void onSuccessResponse(String response) {
//                                Map<String, Object> stringObjectMap1 = XMLPullUtils.getInstance().norMalResult(response);
////                                accreditSuccess(stringObjectMap1);
//                                authorizeSuccess(stringObjectMap1,handler);
//                            }
//
//                            @Override
//                            public void onErrorResponse(String error) {
//                                Log.d(TAG,"授权失败："+error);
//                                authorizeErrorDeal(NetRequestErrorUtile.getInstance().getErrorinf(error), handler);
//                            }
//                        });
//                    }
        }
    }

    private void addDefaultRights(List<UserBean> userBeens, List<DeptBean> deptBeens) {
        if(userBeens!=null){
            for(UserBean userBean:userBeens){
                String rights = userBean.getRights();
                if(rights!=null&&!"".equals(rights)&&!rights.contains("1,")){
                    userBean.setRights("1,"+rights);
                }
            }
        }
       if(deptBeens!=null){
           for(DeptBean deptBean:deptBeens){
               String rights = deptBean.getRights();
               if(rights!=null&&!"".equals(rights)&&!rights.contains("1,")){
                   deptBean.setRights("1,"+rights);
               }
           }
       }

    }

    private void authorizeErrorDeal(String error, Handler handler) {
        if ("请求超时".equals(error)) {
            sendMsg(SecureDocError.E_NetReqTimeout, SecureDocError.EDesc[SecureDocError.E_NetReqTimeout],handler);
        } else if ("网络连接失败，请重新配置".equals(error)) {
            sendMsg(SecureDocError.E_ConnectionFail, error,handler);
        } else {
            sendMsg(SecureDocError.E_NetReqFail, error,handler);
        }
    }

    private void sendMsg(int errorCode, String errorInfo,Handler handler) {
        sendHandlerMessage(handler,errorCode,errorInfo,null,1);
//        Message msg = Message.obtain();
//        msg.what = errorCode;
//        msg.obj = errorInfo;
//        handler.sendMessage(msg);
    }
    private void authorizeSuccess(Map<String, Object> stringObjectMap1, Handler handler) {
        Log.i(TAG, "CallBackFinish: " + stringObjectMap1.toString());
        if (!"1".equals(stringObjectMap1.get("result"))) {
            Log.i(TAG, "onError: 授权失败");
            sendHandlerMessage(handler, SecureDocError.E_AuthorizeFail,"授权失败",null,1);
            return;
        }
        sendMsg(SecureDocError.E_OK,null,handler);
        Log.i(TAG, "onNext: 文档授权成功");
    }


    /**
     * 加密接口
     *
     * @param plainPath  明文文件路径
     * @param cipherPath 密文文件路径
     * @return
     */
    public void sds_encrypt(final String plainPath, final String cipherPath, final int appId, String confidential, final List<UserBean> userBeens, final List<DeptBean> deptBeens, final EncryptCallBack encryptCallBack) {
        if (encryptCallBack == null) return;

        if (plainPath == null || plainPath.equals("")) {
            encryptCallBack.CallBackError(SecureDocError.E_ParamsError, "plainPath不能为空");
            return;
        }

        if (cipherPath == null || cipherPath.equals("")) {
            encryptCallBack.CallBackError(SecureDocError.E_ParamsError, "cipherPath不能为空");
            return;
        }

        File plainFile = new File(plainPath);
        if (!plainFile.exists() || !plainFile.isFile()) {
            encryptCallBack.CallBackError(SecureDocError.E_NotExistFile, "明文不存在");
            return;
        }

        File file = new File(getPath(cipherPath));
        if (!file.exists() || !file.isDirectory()) file.mkdir();
        HttpFields.ARCHIVES_CONFIDENTIAL = confidential;

        final Handler handler = new Handler() {//切换到主线程
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case SecureDocError.E_OK://成功
                        encryptCallBack.CallBackFinish();
                        break;
                    case SecureDocError.E_IsCiphertext://不是明文
                    case SecureDocError.E_AuthenticationFail://认证失败
                    case SecureDocError.E_NetReqTimeout://超时
                    case SecureDocError.E_NetReqFail://网络请求失败
                    case SecureDocError.E_EncryptFail://失败
                    case SecureDocError.E_ConnectionFail://网络连接失败
                        encryptCallBack.CallBackError(msg.what,StringUtils.getErrorMessage((String) msg.obj,"加密失败"));
//                        if(StringUtils.isChineseLetter((String) msg.obj)){
//                            encryptCallBack.CallBackError(msg.what, (String) msg.obj);
//
//                        }else {
//                            encryptCallBack.CallBackError(msg.what,"加密失败");
//
//                        }
                        break;
                    default:
                        break;
                }
            }
        };

        new Thread(new Runnable() {

            private final IssueGrantXMLBean xmlBean = getIssueBeen(plainPath);
            private final String[] key = {null};

            @Override
            public void run() {
                try {
                    IsSecureDocBean2 secureDoc = cryptoUtil.isSecureDoc(plainPath);
                    if (secureDoc.secureDoc == 1) {
                        sendMsg(SecureDocError.E_IsCiphertext, SecureDocError.EDesc[SecureDocError.E_IsCiphertext]);
                        return;
                    }

                    //生成doc的xml
                    String doc = null;

                    doc = XMLBuildUtils.buildDocIssueXMLString(mContext, xmlBean);
                    Log.i("HttpUtils", HttpUrl.ISSUE_DOC + "&doc=" + doc + "&archivesFrom=1");

                    HttpFields.SDS_APP_ID = appId;

                    //发布的网络请求
                    Map<String, String> issueBody = new HashMap<String, String>();
                    issueBody.put("doc", doc);
                    issueBody.put("archivesFrom", HttpFields.SDS_APP_ID + "");
                    if (HttpFields.IS_NET_REQUEST) {//sdk是否有网络请求
                        HttpUtils.getInstance(mContext).issue_doc(issueBody, new HttpUtils.CallBackData() {
                            @Override
                            public void CallBackFinish(Map<String, Object> stringObjectMap) {
                                issueSuccess(stringObjectMap);
                            }

                            @Override
                            public void CallBackError(String error) {
                                sendMsg(SecureDocError.E_EncryptFail, error);
                                ;
                            }
                        });
                    } else {
                        mSecDocInterface.release(mContext, issueBody, new SecurityDocumentCallBack() {
                            @Override
                            public void onSuccessResponse(String response) {
                                Map<String, Object> stringObjectMap = XMLPullUtils.getInstance().issueDoc(response);
                                issueSuccess(stringObjectMap);
                            }

                            @Override
                            public void onErrorResponse(String error) {
                                dealError(error);
                            }
                        });
                    }


                } catch (Exception e) {
                    sendMsg(SecureDocError.E_EncryptFail, e.toString());
                }

            }

            private void issueSuccess(Map<String, Object> stringObjectMap) {
//                Log.i(TAG, "CallBackFinish: " + response);
//                Map<String, Object> stringObjectMap = XMLPullUtils.getInstance().issueDoc(response);

                if (!"1".equals(stringObjectMap.get("result"))) {
                    sendMsg(SecureDocError.E_EncryptFail, (String) stringObjectMap.get(HttpFields.NET_ERROR));
                    return;
                }
                if(defultRights.get(HttpFields.userId)==null){
                    Map<String, String> getDefultRightBody = new HashMap<String, String>();
                    getDefultRightBody.put("loginName",loginName);
                    if(HttpFields.IS_NET_REQUEST){
                        HttpUtils.getInstance(mContext).getDefultRights(getDefultRightBody, new HttpUtils.CallBackData() {
                            @Override
                            public void CallBackFinish(Map<String, Object> stringObjectMap) {
                                if (!"1".equals(stringObjectMap.get("result"))) {
                                   Log.d(TAG,"获取用户默认权限失败:"+(String) stringObjectMap.get(HttpFields.NET_ERROR));
//                                    sendMsg(SecureDocError.E_EncryptFail, (String) stringObjectMap.get(HttpFields.NET_ERROR));
//                                    return;
                                    defultRights.put(HttpFields.userId, "1,2,3,4,5,6,7,8,9,10,");
                                }else {
                                    String rights = (String) stringObjectMap.get(HttpFields.DEFAULT_RIGHTS);
                                    if(rights==null){
                                        defultRights.put(HttpFields.userId, "1,2,3,4,5,6,7,8,9,10,");
                                    }else {
                                        defultRights.put(HttpFields.userId,rights);
                                    }

                                    Log.d(TAG,"获取用户默认权限成功:"+(String) (String) stringObjectMap.get(HttpFields.DEFAULT_RIGHTS));
                                }
                            }

                            @Override
                            public void CallBackError(String error) {
                                Log.d(TAG,"获取用户默认权限失败："+error);
                                defultRights.put(HttpFields.userId, "1,2,3,4,5,6,7,8,9,10");
                            }
                        });
                    }else {
                        mSecDocInterface.getDefultRights(mContext, getDefultRightBody, new SecurityDocumentCallBack() {
                            @Override
                            public void onSuccessResponse(String response) {
                                Map<String, Object> stringObjectMap1 = XMLPullUtils.getInstance().norMalResult(response);
                                if (!"1".equals(stringObjectMap1.get("result"))) {
                                    Log.d(TAG,"获取用户默认权限失败:"+(String) stringObjectMap1.get(HttpFields.NET_ERROR));
                                    defultRights.put(HttpFields.userId, "1,2,3,4,5,6,7,8,9,10");
//                                    sendMsg(SecureDocError.E_EncryptFail, (String) stringObjectMap.get(HttpFields.NET_ERROR));
//                                    return;
                                }else {
                                    defultRights.put(HttpFields.userId, (String) stringObjectMap1.get(HttpFields.DEFAULT_RIGHTS));
                                }
                            }

                            @Override
                            public void onErrorResponse(String error) {
                                Log.d(TAG,"获取用户默认权限失败："+error);
                                defultRights.put(HttpFields.userId, "1,2,3,4,5,6,7,8,9,10");
                            }
                        });
                    }
                }



                List<Map<String, Map<String, String>>> resultMaps = (List<Map<String, Map<String, String>>>) stringObjectMap.get("issue");
                Map<String, Map<String, String>> resultMap = resultMaps.get(0);
                Map<String, String> docResult = resultMap.get("doc");
                Map<String, String> archivesResult = resultMap.get("archives");
                key[0] = archivesResult.get("key");
                String mArchiveId = archivesResult.get("id");
                String mDocId = docResult.get("assignedid");
                Log.i(TAG, "CallBackFinish: " + key[0] + mDocId + versionId);
                Log.i(TAG, "onNext: docId= " + mDocId + "archivesId" + mArchiveId);
                xmlBean.setDocId(Long.parseLong(mDocId));
                xmlBean.setArchiveId(Long.parseLong(mArchiveId));

                //文档请求授权
                IssueGrantXMLBean grantBean = new IssueGrantXMLBean(Long.parseLong(mArchiveId), 1);
                String right = null;
                boolean isConainLogin = false;
                try {
                    for(UserBean user:userBeens){
                        if(user.getIdentifier().equals(HttpFields.userId)){
                           isConainLogin = true;
                        }
                    }
                    if(!isConainLogin){
                        userBeens.add(new UserBean(HttpFields.userId,defultRights.get(HttpFields.userId)));
                    }
                    addDefaultRights(userBeens,deptBeens);
                    right = XMLBuildUtils.buildRightGrantXMLString(mContext, grantBean.getArchiveId()+"", userBeens, deptBeens);
                    Log.i("HttpUtils", HttpUrl.RIGHT_GRANT + "&rights=" + right + "&archivesFrom=1");
                } catch (IOException e) {
                    sendMsg(SecureDocError.E_EncryptFail, e.toString());
                }

                Map<String, String> grantBody = new HashMap<String, String>();
                grantBody.put("rights", right);
                grantBody.put("archivesFrom", HttpFields.SDS_APP_ID + "");
                if (HttpFields.IS_NET_REQUEST) {
                    HttpUtils.getInstance(mContext).grant_rights(grantBody, new HttpUtils.CallBackData() {
                        @Override
                        public void CallBackFinish(Map<String, Object> stringObjectMap) {
                            accreditSuccess(stringObjectMap);
                        }

                        @Override
                        public void CallBackError(String error) {
                            dealError(error);
                        }
                    });
                } else {
                    mSecDocInterface.accredit(mContext, grantBody, new SecurityDocumentCallBack() {
                        @Override
                        public void onSuccessResponse(String response) {
                            Map<String, Object> stringObjectMap1 = XMLPullUtils.getInstance().norMalResult(response);
                            accreditSuccess(stringObjectMap1);
                        }

                        @Override
                        public void onErrorResponse(String error) {
                            dealError(error);
                        }
                    });
                }


            }

            /**
             * 授权成功
             * @param stringObjectMap1
             */
            private void accreditSuccess(Map<String, Object> stringObjectMap1) {
                Log.i(TAG, "CallBackFinish: " + stringObjectMap1.toString());
                if (!"1".equals(stringObjectMap1.get("result"))) {
                    Log.i(TAG, "onError: 授权失败");
                    sendMsg(SecureDocError.E_EncryptFail, (String) stringObjectMap1.get(HttpFields.NET_ERROR));
                    return;
                }
                Log.i(TAG, "onNext: 文档授权成功");

                int result = cryptoUtil.EncryptFile(key[0], xmlBean.getOldPath(), xmlBean.getArchiveId(), xmlBean.getDocId(), HttpFields.SDS_APP_ID, cipherPath);
                Log.i(TAG, "doInBackground: " + result);
                if (result == 1) {
//                    RightAndKeyBean rBean = new RightAndKeyBean(xmlBean.getDocId(), HttpUrl.loginName, xmlBean.getDocId(), xmlBean.getArchiveId(), HttpFields.DEFAULT_RIGHT, key[0]);
//                    Log.i(TAG, "加密: " + rBean.getArchiveId() + rBean.getDocId() + rBean.getRight());
//                    dbService.insertOrUpdateRightAndkeyTable(rBean);
                    sendMsg(SecureDocError.E_OK, SecureDocError.EDesc[SecureDocError.E_OK]);
                } else {
                    sendMsg(SecureDocError.E_EncryptFail, xmlBean.getName() + "加密失败");
                }
            }

            private void dealError(String error) {
                if ("请求超时".equals(error)) {
                    sendMsg(SecureDocError.E_NetReqTimeout, SecureDocError.EDesc[SecureDocError.E_NetReqTimeout]);
                } else if ("网络连接失败，请重新配置".equals(error)) {
                    sendMsg(SecureDocError.E_ConnectionFail, error);
                } else {
                    sendMsg(SecureDocError.E_NetReqFail, error);
                }
            }

            private void sendMsg(int errorCode, String errorInfo) {
                Message msg = Message.obtain();
                msg.what = errorCode;
                msg.obj = errorInfo;
                handler.sendMessage(msg);
            }

        }).start();
    }



    @NonNull
    private IssueGrantXMLBean getIssueBeen(String plainPath) {
        String uuid = UUID.randomUUID() + "";
        String docIdentifier = uuid.replaceAll("-", "");
        String fileName = getName(plainPath);
        return new IssueGrantXMLBean(fileName, plainPath, docIdentifier);
    }

    /*
    * Java文件操作 获取文件名
    */
    public static String getName(String plainPath) {
        File file = new File(plainPath);
        return file.getName();
//        if ((plainPath != null) && (plainPath.length() > 0)) {
//            int dot = plainPath.lastIndexOf('/');
//            if ((dot > -1) && (dot < (plainPath.length()))) {
//                return plainPath.substring(dot, plainPath.length());
//            }
//        }
//        return plainPath;
    }

    /*
       * Java文件操作 获取文件名
       */
    public static String getPath(String cipherPath) {
        if ((cipherPath != null) && (cipherPath.length() > 0)) {
            int dot = cipherPath.lastIndexOf('/');
            if ((dot > -1) && (dot < (cipherPath.length()))) {
                return cipherPath.substring(0, dot);
            }
        }
        return cipherPath;
    }


    /**
     * 服务器登出接口
     */
    public void sds_logout(final LogoutCallBack logoutCallBack) {
        final Handler handler = new Handler() {//切换到主线程
            public void handleMessage(android.os.Message msg) {
                // 处理消息时需要知道是成功的消息还是失败的消息
                switch (msg.what) {
                    case 1:
                        ETResult etResult = (ETResult) msg.obj;
                        if (etResult.resultCode == SecureDocError.E_OK) {
                            logoutCallBack.CallBackFinish();
                        } else {
                            logoutCallBack.CallBackError(etResult.resultCode, etResult.errorInfo);
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtils.getInstance(mContext).logout(new HttpUtils.CallBackData() {
                    @Override
                    public void CallBackFinish(Map<String, Object> stringObjectMap) {
                        String result = (String) stringObjectMap.get(HttpFields.NET_RESULT);
                        if (result != null && result.equals(HttpFields.NET_REQUEST_SUCESS)) {
                            sendHandlerMessage(handler, SecureDocError.E_OK, null, null,1);
                        } else {
                            String error = (String) stringObjectMap.get(HttpFields.NET_ERROR);
                            if ("No Access".equals(error) || "认证失败，请重新登录客户端".equals(error)) {
                                //OK}
                                sendHandlerMessage(handler, SecureDocError.E_AuthenticationFail, null, null,1);
                            } else {
                                sendHandlerMessage(handler, SecureDocError.E_NetReqFail, error, null,1);
                            }
                        }
                    }

                    @Override
                    public void CallBackError(String error) {
                        sendHandlerMessage(handler, SecureDocError.E_NetReqFail, error, null,1);
                    }
                });

            }
        }).start();
    }

    /**
     * 保存loginName
     *
     * @param context   传进来的上下文
     * @param loginName 本地保存的loginName
     */
    private static boolean saveLoginNameSP(Context context, String loginName) {
        //获取当前context的SP
        SharedPreferences sp = context.getSharedPreferences(HttpFields.SDS_SP_NAME, Context.MODE_PRIVATE);
        //获取SP的Editor
        SharedPreferences.Editor edit = sp.edit();
        //key-value写入SP
        edit.putString(HttpFields.SDS_LOGIN_NAME, loginName);
        //最后要提交
        return edit.commit();
    }

    String userLogsString;

    /**
     * 发送日志
     *
     * @param loginName
     * @param userLogs
     */
    private void sendLog(final String loginName, final List<UserLog> userLogs) {

        if(userLogs==null||userLogs.size()==0){
            return;
        }
        try {
            userLogsString = XMLBuildUtils.buildLogSendXMLString(userLogs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Map map = new HashMap();
                map.put(HttpFields.LOGIN_NAME, loginName);
                map.put(HttpFields.TYPE, 1+"");
                map.put(HttpFields.USER_LOG, userLogsString);
                if (HttpFields.IS_NET_REQUEST) {
                    HttpUtils.getInstance(mContext).sendLog(map, new HttpUtils.CallBackData() {
                        @Override
                        public void CallBackFinish(Map<String, Object> stringObjectMap) {
                            Log.d(TAG, "上传日志成功1：");
                            sendLogSuccess(stringObjectMap, userLogs);

                        }

                        @Override
                        public void CallBackError(String error) {
                            Log.d(TAG, "上传日志失败1：" + error);
                            for (UserLog userLog : userLogs) {
                                if (userLog.getId() == null) {
                                    DbService.getInstance(mContext).insertOrUpdateLog(userLog);
                                }
                            }
                        }
                    });
                }else {
                    mSecDocInterface.sendLog(mContext, map, new SecurityDocumentCallBack() {
                        @Override
                        public void onSuccessResponse(String response) {
                            Log.d(TAG, "上传日志成功2："+response);
                            Map<String, Object> stringObjectMap1 = XMLPullUtils.getInstance().norMalResult(response);
                            sendLogSuccess(stringObjectMap1, userLogs);
                        }

                        @Override
                        public void onErrorResponse(String error) {
                            Log.d(TAG, "上传日志失败2：" + error);
                            for (UserLog userLog : userLogs) {
                                if (userLog.getId() == null) {
                                    DbService.getInstance(mContext).insertOrUpdateLog(userLog);
                                }
                            }
                        }
                    });
                }

            }
        }).start();
    }

    private void sendLogSuccess(Map<String, Object> stringObjectMap, List<UserLog> userLogs) {
        String result = (String) stringObjectMap.get(HttpFields.NET_RESULT);
        if(HttpFields.NET_REQUEST_SUCESS.equals(result)){
            for (UserLog userLog : userLogs) {
                if (userLog.getId() != null) {
                    //从数据库中删除
                    DbService.getInstance(mContext).deleteLog(userLog.getId());
                }
            }
        }else {
            for (UserLog userLog : userLogs) {
                if (userLog.getId() == null) {
                    DbService.getInstance(mContext).insertOrUpdateLog(userLog);
                }
            }
        }
    }


    /**
     * @param archivesID
     * @param docID
     * @param oper       操作类型
     * @param result     操作结果
     * @param memo       失败原因
     * @return
     */
    private UserLog getLogBean(long archivesID, long docID, int oper, int result, String memo) {
        UserLog userLog = new UserLog();
        userLog.setArchivesID(archivesID);
        userLog.setDocID(docID);
        userLog.setOper(oper);
        userLog.setResult(result);
        userLog.setMemo(memo+"");
        if (ConnectUtil.isConnenct(mContext)) {
            userLog.setOnline(HttpFields.ONLINE);
        } else {
            userLog.setOnline(HttpFields.Offline);
        }
        userLog.setOpertime(DateUtils.getStringDate());
        userLog.setLoginName(loginName);
        return userLog;
    }


}
