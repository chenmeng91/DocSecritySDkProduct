package docsecuritysdk.eetrust.com.docsecuritysdk;

import android.content.Context;


import com.eetrust.securedocsdk.SecurityDocumentCallBack;
import com.eetrust.securedocsdk.SecurityDocumentInterface;

import java.util.Map;

import docsecuritysdk.eetrust.com.docsecuritysdk.http.HttpUtils;

/**
 * Created by chenmeng on 2017/5/12.
 */

public class SecurityDocumentMethod implements SecurityDocumentInterface {
    private static SecurityDocumentMethod securityDocumentMethod;
    private HttpUtils httpUtils;
    private Context context;

    private SecurityDocumentMethod(Context context) {
        this.context = context;
        httpUtils = HttpUtils.getInstance(context);
    }

    public static synchronized SecurityDocumentMethod getInstance(Context context) {
        if (securityDocumentMethod == null) {
            securityDocumentMethod = new SecurityDocumentMethod(context);
        }
        return securityDocumentMethod;
    }

    /**
     * 权限查询
     *
     * @param context
     * @param map
     * @param callBack
     */
    @Override
    public void authorityQuery(Context context, Map<String, String> map, SecurityDocumentCallBack callBack) {
        httpUtils.query_docRights(map, callBack);
    }

    /**
     * 发布
     *
     * @param context
     * @param map
     * @param callBack
     */
    @Override
    public void release(Context context, Map<String, String> map, SecurityDocumentCallBack callBack) {
        httpUtils.issue_doc(map, callBack);
    }

    /**
     * 授权
     *
     * @param context
     * @param map
     * @param callBack
     */
    @Override
    public void accredit(Context context, Map<String, String> map, SecurityDocumentCallBack callBack) {
        httpUtils.grant_rights(map,callBack);
    }



    /**
     * 下载离线包
     *
     * @param context
     * @param map
     * @param callBack
     */
    @Override
    public void download(Context context, Map<String, String> map, SecurityDocumentCallBack callBack) {

    }

    /**
     * 路径打开文件
     *
     * @param context
     * @param filePath
     * @param rights
     */
    @Override
    public void openFileByWPS(Context context, String filePath, String rights) {

    }

    /**
     * 内存流打开文件
     *
     * @param context
     * @param datas
     * @param fileType 例如，doc，docx，png，mp4等
     * @param rights
     */
    @Override
    public void openFileByWPS(Context context, byte[] datas, String fileType, String rights) {

    }

    @Override
    public void sendLog(Context context, Map<String, String> map, SecurityDocumentCallBack callBack) {
        HttpUtils.getInstance(context).sendLog(map,callBack);
    }

    @Override
    public void getDefultRights(Context context, Map<String, String> map, SecurityDocumentCallBack callBack) {
     HttpUtils.getInstance(context).getDefultRights(map,callBack);
    }
}
