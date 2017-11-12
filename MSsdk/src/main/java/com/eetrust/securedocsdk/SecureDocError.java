package com.eetrust.securedocsdk;

/**
 * Created by chenmeng on 2016/11/2.
 */

public class SecureDocError {

    public static final int E_OK = 0x00;//操作成功
    public static final int E_AuthenticationFail = 0x01;//身份认证失败
    public static final int E_DecryptFail = 0x02;//解密失败
    public static final int E_EncryptFail = 0x03;//加密失败
    public static final int E_NotCiphertext = 0x04;//不是密文
    public static final int E_IsCiphertext = 0x05;//已是密文
    public static final int E_NoRight = 0x06;//没有权限
    public static final int E_NotExistFile = 0x07;//密文不存在
    public static final int E_QueryRightsFail = 0x08;//文档权限获取失败
    public static final int E_ParamsError = 0x09;//参数错误
    public static final int E_PathError = 0x0A;//密文和明文路径相同
    public static final int E_ConnectionFail = 0x0B;//网络连接失败，请重新配置
    public static final int E_NetReqTimeout = 0x0C;//网络请求超时
    public static final int E_NetReqFail = 0x0D;//网络请求失败
    public static final int E_AuthorizeFail = 0x0E;//授权失败

    public static String[] EDesc = {
            "操作成功!",//0
            "身份认证失败",//1
            "解密失败",//2
            "加密失败",//3
            "不是密文",//4
            "已是密文",//5
            "没有权限",//6
            "密文不存在",//7
            "文档权限获取失败",//8
            "参数错误",//9
            "密文和明文路径相同",//10
            "网络连接失败，请重新配置",//11
            "网络请求超时",//12
            "网络请求失败",//13
            "授权失败",
    };
}
