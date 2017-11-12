#include "../com_eetrust_securedocsdk_CryptoUtil.h"

#include <stdlib.h>
#include <stdio.h>
#include "SDCrypto.h"
#include <android/log.h>
//#include "ET_Crypto_JNI.h"

#define ET_Crypto_JNI_TAG "ET_Crypto_JNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, ET_Crypto_JNI_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_DEBUG, ET_Crypto_JNI_TAG, __VA_ARGS__)


/////////////////////////////////////////////////////////////////////////////////

/*
 * Class:     com_eetrust_securedoc_crypto_CryptoUtil
 * Method:    EncryptFile
 * Signature: (Ljava/lang/String;)I
 */
 JNIEXPORT jint JNICALL Java_com_eetrust_securedocsdk_CryptoUtil_EncryptFile
   (JNIEnv *env, jobject thiz, jstring key, jstring sFileName, jlong docid, jlong archiveid, jint appId, jstring outFileName) {
    LOGD("Java_com_eetrust_securedoc_crypto_CryptoUtil_EncryptFile");
   const char *key_c = (const char*)(env->GetStringUTFChars(key, JNI_FALSE));
   const char *sFileName_c = (const char*)(env->GetStringUTFChars(sFileName, JNI_FALSE));
   const char *outFileName_c = (const char*)(env->GetStringUTFChars(outFileName, JNI_FALSE));
   long  docid_c =docid;
   long archiveid_c = archiveid;
   unsigned short appId_short = appId;
   int result = SD_EncryptFile(ENC_TYPE_AES_ECB, (char*)key_c, (char*)sFileName_c, docid_c, archiveid_c,appId_short,(char*)outFileName_c);

    env->ReleaseStringUTFChars(sFileName, sFileName_c);
    env->ReleaseStringUTFChars(key, key_c);
    env->ReleaseStringUTFChars(outFileName, outFileName_c);

    return result;
}

/*
 * Class:     com_eetrust_securedoc_crypto_CryptoUtil
 * Method:    DecryptFile
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_eetrust_securedocsdk_CryptoUtil_DecryptFile
  (JNIEnv *env, jobject thiz,jstring key, jstring sFileName,jstring outFileName) {

    LOGD("Java_com_eetrust_securedoc_crypto_CryptoUtil_DecryptFile");

      const char *key_c = (const char*)(env->GetStringUTFChars(key, JNI_FALSE));
      const char *sFileName_c = (const char*)(env->GetStringUTFChars(sFileName, JNI_FALSE));
      const char *outFileName_c = (const char*)(env->GetStringUTFChars(outFileName, JNI_FALSE));
      int result = SD_DecryptFile(ENC_TYPE_AES_ECB, (char*)key_c, (char*)sFileName_c, (char*)outFileName_c);

      env->ReleaseStringUTFChars(sFileName, sFileName_c);
      env->ReleaseStringUTFChars(key, key_c);
      env->ReleaseStringUTFChars(outFileName, outFileName_c);

    return result;
}

/*
 * Class:     com_eetrust_securedoc_crypto_CryptoUtil
 * Method:    isSecureDoc
 * Signature: (Ljava/lang/String;)Z
 */

 JNIEXPORT jobject JNICALL Java_com_eetrust_securedocsdk_CryptoUtil_isSecureDoc
   (JNIEnv *env, jobject thiz, jstring sFileName) {
 jobject objOut = NULL;
    LOGD("Java_com_eetrust_securedoc_crypto_CryptoUtil_isSecureDoc");
    const char *pName = (const char*)(env->GetStringUTFChars(sFileName, JNI_FALSE));
    LOGD("FileName = %s", pName);
    int enctype =0;
    unsigned short appId_short =1;
   unsigned int archive_id =1;
   unsigned int doc_id = 1;
    long long filelen = 0;
    int result =IsCipherFile((char*)pName, enctype, archive_id,doc_id,appId_short,filelen);
   jclass clssObjOut = env->FindClass("com/eetrust/bean/IsSecureDocBean2");

 if (clssObjOut) {
        jmethodID constructObjOut = env->GetMethodID(clssObjOut, "<init>", "()V");
        jfieldID secureDoc = env->GetFieldID(clssObjOut, "secureDoc", "I");
        jfieldID appId = env->GetFieldID(clssObjOut, "appId", "I");
        jfieldID archiveId = env->GetFieldID(clssObjOut, "archiveId", "J");
        jfieldID docId = env->GetFieldID(clssObjOut, "docId", "J");

        objOut = env->NewObject(clssObjOut, constructObjOut);
        env->SetIntField(objOut, secureDoc, result);
        if(result==1){
            env->SetLongField(objOut, archiveId, archive_id);
            env->SetLongField(objOut, docId, doc_id);
            env->SetIntField(objOut, appId, appId_short);
        }
    env->ReleaseStringUTFChars(sFileName, pName);
   // return JNI_TRUE;
   return objOut;
}
}

/*
 * Class:     com_eetrust_securedoc_crypto_CryptoUtil
 * Method:    getSecureDocInfo
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jobject JNICALL Java_com_eetrust_securedocsdk_CryptoUtil_getSecureDocInfo
  (JNIEnv *env, jobject thiz, jstring sFileName) {
jobject objOut = NULL;
jobject objOut2 =NULL;
jmethodID pp;
int enctype =0;
jint ss =100;
    LOGD("Java_com_eetrust_securedoc_crypto_CryptoUtil_getSecureDocInfo");

    const char *pName = (const char*)(env->GetStringUTFChars(sFileName, JNI_FALSE));
    LOGD("FileName = %s", pName);
    env->ReleaseStringUTFChars(sFileName, pName);
     jclass clssObjOut = env->FindClass("com/eetrust/securedocsdk/Test");
     jclass clssObjOut2 = env->FindClass("com/eetrust/securedocsdk/TestBean");

     if (clssObjOut) {
     jmethodID constructObjOut = env->GetMethodID(clssObjOut, "<init>", "()V");
     pp = env->GetMethodID(clssObjOut,"pp","(II)Lcom/eetrust/securedocsdk/TestBean;");
    objOut = env->NewObject(clssObjOut, constructObjOut);
     }
  // enctype = env->CallIntMethod(objOut,pp,100,20);
  objOut2 = env->CallObjectMethod(objOut,pp,100,20);
    return objOut2;
}