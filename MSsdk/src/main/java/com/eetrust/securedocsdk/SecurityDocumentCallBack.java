package com.eetrust.securedocsdk;

/**
 * 功能描述：
 * Created by xichao on 2017/5/11 12:07
 */
public interface SecurityDocumentCallBack {

    public void  onSuccessResponse(String response);
    public void  onErrorResponse(String error);

}
