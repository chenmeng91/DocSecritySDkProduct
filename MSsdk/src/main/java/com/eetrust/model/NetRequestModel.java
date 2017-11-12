package com.eetrust.model;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.eetrust.bean.DeptBean;
import com.eetrust.bean.IsSecureDocBean2;
import com.eetrust.bean.RightAndKeyBean;
import com.eetrust.bean.UserBean;
import com.eetrust.http.HttpFields;
import com.eetrust.http.HttpUrl;
import com.eetrust.http.HttpUtils;
import com.eetrust.http.XMLBuildUtils;
import com.eetrust.http.XMLPullUtils;
import com.eetrust.securedocsdk.CryptoUtil;
import com.eetrust.securedocsdk.MSD;
import com.eetrust.securedocsdk.SecureDocError;
import com.eetrust.securedocsdk.SecurityDocumentCallBack;
import com.eetrust.securedocsdk.SecurityDocumentInterface;
import com.eetrust.utils.NetRequestErrorUtile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenmeng on 2017/8/11.
 */

public class NetRequestModel {
    private static NetRequestModel netRequestModel;
    private String TAG = "NetRequestModel";
    private NetRequestModel() {
    }
public interface NetCallresult{
    void success(Map<String, Object> stringObjectMap);
    void requestFail(String error,long archiveId,long docId);
    void notNotCiphertext();
    void buildXMLFail();//生成xml失败
}
    public static NetRequestModel getInstance(){
        synchronized (NetRequestModel.class){
            if(netRequestModel==null){
                netRequestModel = new NetRequestModel();
            }

            return netRequestModel;
        }

    }


    public IsSecureDocBean2 queryRights(final Context mContext, final String cipherPath, final SecurityDocumentInterface mSecDocInterface, final Handler handler, final NetCallresult netCallresult){
        final IsSecureDocBean2 isSecureDocBean = CryptoUtil.getInstance().isSecureDoc(cipherPath);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //根据cipherPath对文件进行解析得到archiveId 和 docId
                //网络请求得到key 如果失败了就去数据库中得到key，如果数据库中也没就解密失败,如果成功了就获得key和right 存入
                //根据key对文件进行解密

                if (isSecureDocBean.secureDoc == 1) {//是加密文件
                    final long archiveId = isSecureDocBean.archiveId;//6193;
                    final long docId = isSecureDocBean.docId;//2356;
                    final int appId = isSecureDocBean.appId;
                    Map<String, String> body = new HashMap<String, String>();
                    body.put(HttpFields.ARCHIVES_ID, archiveId + "");
                    body.put(HttpFields.DOC_ID, docId + "");
                    body.put(HttpFields.ARCHIVES_FROM_APPID, appId + "");
                    body.put(HttpFields.LOGIN_NAME, HttpUrl.loginName);//
                    if (HttpFields.IS_NET_REQUEST) {
                        HttpUtils.getInstance(mContext).query_docRights(body, new HttpUtils.CallBackData() {
                            @Override
                            public void CallBackFinish(Map<String, Object> stringObjectMap) {
                                rightsNetDataDealer(stringObjectMap, netCallresult, archiveId, docId);

                            }

                            @Override
                            public void CallBackError(String error) {
                                Log.d(TAG,"获取权限失败："+error);
                                netCallresult.requestFail(error,archiveId,docId);
                            }
                        });
                    } else {
                        mSecDocInterface.authorityQuery(mContext, body, new SecurityDocumentCallBack() {
                            @Override
                            public void onSuccessResponse(String requestString) {
                                Map<String, Object> stringObjectMap = MSD.getInstance(mContext).parseXml(requestString, archiveId, docId, cipherPath, null, handler);
                                rightsNetDataDealer(stringObjectMap, netCallresult, archiveId, docId);
                            }

                            @Override
                            public void onErrorResponse(String error) {
                                Log.d(TAG, "CallBackError: " + error);
                                netCallresult.requestFail(error,archiveId,docId);
                            }
                        });
                    }


                } else {
                    //不是加密文件
                    netCallresult.notNotCiphertext();
                }
            }
        }).start();

        return isSecureDocBean;
    }

    private void rightsNetDataDealer(Map<String, Object> stringObjectMap, NetCallresult netCallresult, long archiveId, long docId) {
        String result = (String) stringObjectMap.get(HttpFields.NET_RESULT);
        if (result != null && result.equals(HttpFields.NET_REQUEST_SUCESS)) {
            netCallresult.success(stringObjectMap);
        } else if (result == null) {
            //权限获取异常
            netCallresult.requestFail("权限信息获取异常",archiveId,docId);
        } else {
            //result = 0 权限获取失败
            String error = (String) stringObjectMap.get(HttpFields.RIGHT_ERROR);
            Log.d(TAG, "error: " + error);
            netCallresult.requestFail(error,archiveId,docId);
        }
    }

    public void accreditNetRequest(Context mContext, IsSecureDocBean2 isSecureDocBean, final List<UserBean> userBeens, final List<DeptBean> deptBeens,final SecurityDocumentInterface mSecDocInterface, final NetCallresult netCallresult){

        final long archiveId = isSecureDocBean.archiveId;//6193;
        Log.d(TAG,"授权的文档组archiveId:"+archiveId);
        final long docId = isSecureDocBean.docId;//2356;
        Log.d(TAG,"授权的文档docId:"+archiveId);
        final int appId = isSecureDocBean.appId;

        String right = null;
        try {
            right = XMLBuildUtils.buildRightGrantXMLString(mContext, archiveId + "", userBeens, deptBeens);
            Log.i("HttpUtils", HttpUrl.RIGHT_GRANT + "&rights=" + right + "&archivesFrom=1");
        } catch (IOException e) {
            netCallresult.buildXMLFail();
        }

        Map<String, String> grantBody = new HashMap<String, String>();
        grantBody.put("rights", right);
        grantBody.put("archivesFrom", HttpFields.SDS_APP_ID + "");
        if (HttpFields.IS_NET_REQUEST) {
            HttpUtils.getInstance(mContext).grant_rights(grantBody, new HttpUtils.CallBackData() {
                @Override
                public void CallBackFinish(Map<String, Object> stringObjectMap1) {
                    netCallresult.success(stringObjectMap1);
                }

                @Override
                public void CallBackError(String error) {
                    Log.d(TAG,"授权失败："+error);
                   netCallresult.requestFail(error,archiveId,docId);
                }
            });
        } else {
            mSecDocInterface.accredit(mContext, grantBody, new SecurityDocumentCallBack() {
                @Override
                public void onSuccessResponse(String response) {
                    Map<String, Object> stringObjectMap1 = XMLPullUtils.getInstance().norMalResult(response);
                    netCallresult.success(stringObjectMap1);
                }

                @Override
                public void onErrorResponse(String error) {
                    Log.d(TAG,"授权失败："+error);
                    netCallresult.requestFail(error,archiveId,docId);
                }
            });
        }

    }

}
