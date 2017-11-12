package com.eetrust.securedocsdk;

import com.eetrust.bean.IssueGrantXMLBean;
import com.eetrust.http.HttpUtils;

import java.util.Map;

/**
 * Created by chenmeng on 2017/5/10.
 */

public interface NetworkRequestInterface {
    void query_docRights(Map<String, String> body, HttpUtils.CallBackData1 callBackData);

    void issue_doc(Map<String,String> body, HttpUtils.CallBackData1 callBackData);

    void grant_rights(IssueGrantXMLBean grantBean, HttpUtils.CallBackData1 callBackData);

    void downLoadOffline(Map<String, String> body,HttpUtils.CallBackData1 callBackData);
}
