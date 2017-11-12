package com.eetrust.securedocsdk;

import android.content.Context;

import java.util.Map;

/**
 * 功能描述：
 * Created by xichao on 2017/5/11 10:27
 */
public interface SecurityDocumentInterface {



    //权限查询
    public void authorityQuery(Context context, Map<String, String> map, SecurityDocumentCallBack callBack);

    //发布
    public void release(Context context, Map<String, String> map, SecurityDocumentCallBack callBack);

    //授权
    public void accredit(Context context, Map<String, String> map, SecurityDocumentCallBack callBack);

    //下载离线包
    public void download(Context context, Map<String, String> map, SecurityDocumentCallBack callBack);

    //wps文件打开
	/** ---------- 权限对应表 -------- */
// 1 阅读
// 2 复制
// 3 编辑
// 4 打印
// 5 水印打印
// 6 截屏
// 7 解密
// 8 离线
// 9 外发
// 10 分发
// 如："1,3,10," 具有阅读、编辑、分发权限。
    public void openFileByWPS(Context context, String filePath,String rights);

    //wps文件打开

    /**
     *
     * @param context
     * @param datas
     * @param fileType 例如，doc，docx，png，mp4等
     * @param rights
     */
    public void openFileByWPS(Context context, byte[] datas,String fileType,String rights);


    void sendLog(Context context,Map<String, String> map, SecurityDocumentCallBack callBack);

    void getDefultRights(Context context,Map<String, String> map, SecurityDocumentCallBack callBack);
}
