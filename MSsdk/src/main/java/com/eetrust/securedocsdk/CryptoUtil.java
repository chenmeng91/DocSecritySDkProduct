package com.eetrust.securedocsdk;


import com.eetrust.bean.IsSecureDocBean2;

/**
 * Created by rocwzp on 16/9/8.
 */
public class CryptoUtil {

    private volatile static CryptoUtil instance = null;

    private CryptoUtil() {
    }

    /* 单例模式：双重检查模式(DCL) */
    public static CryptoUtil getInstance() {
        if (instance == null) {
            synchronized (CryptoUtil.class) {
                if(instance == null){
                    instance = new CryptoUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 加密文件
     * @param key
     * @param fileName
     * @param docid
     * @param archiveid
     * @param outFileNmae
     * @return
     */
    public native int EncryptFile(String key, String fileName, long archiveid, long docid,int appId,String outFileNmae);

    /**
     *
     * 解密文件
     * @param key
     * @param fileName
     * @param outfilename
     * @return
     */
    public native int DecryptFile(String key, String fileName, String outfilename);

    /**
     * 是否是加密文件
     * @param fileName
     * @return 返回一个类
     */
    public native IsSecureDocBean2 isSecureDoc(String fileName);

    public native TestBean getSecureDocInfo(String fileName);

    static {
        //注意顺序，第三方依赖库在前面，自己的库在后面，不这样低版本的android会找不到这个库
        System.loadLibrary("opencrypto");
        System.loadLibrary("openssl");
        System.loadLibrary("JniCryptoAPI");

    }
}
