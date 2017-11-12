package com.eetrust.securedocsdk;

/**
 * Created by chenmeng on 2016/11/2.
 */

public class ETResult {
    public int resultCode;//返回码
    public String errorInfo;//错误描述
    public String resultData;//返回值
    public String openFileUrl;
    public ETResult() {
        resultCode = SecureDocError.E_OK;
        errorInfo = SecureDocError.EDesc[resultCode];
        resultData = "";
    }


    public ETResult(int errId) {
        resultCode = errId;
        errorInfo = SecureDocError.EDesc[resultCode];
        resultData = "";
    }

    public ETResult(String sValue) {
        resultData = sValue;

        resultCode = SecureDocError.E_OK;
        errorInfo = SecureDocError.EDesc[resultCode];
    }

    public void setError(int errId, String errDesc) {
        resultCode = errId;
        errorInfo = errDesc;
        resultData = "";
    }

    public void setError(int errId) {
        resultCode = errId;
        errorInfo = SecureDocError.EDesc[resultCode];
        resultData = "";
    }

    public void setOK(String sValue) {
        resultCode = SecureDocError.E_OK;
        errorInfo = SecureDocError.EDesc[resultCode];
        resultData = sValue;
    }
}
