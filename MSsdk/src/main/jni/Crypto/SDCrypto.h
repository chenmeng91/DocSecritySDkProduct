#ifndef __SD_CRYPT_H_
#define __SD_CRYPT_H_

#include <stdio.h>

//ENC_TYPE
#define ENC_TYPE_AES_ECB			0x01


//产生随机的对称密钥（字节数组）
int GenerateSymmectricKey(unsigned char *key, int keylen);

//将十六进制数字字符串转成字节数组
int ConvertSymmectricKey(char *hexkey, unsigned char *key, int &keylen);

//将字节数组转成十六进制数字字符串
int ConvertSymmectricKeyToHexKey(unsigned char *key, int keylen, char *hexkey);

//AES对称密钥加密
int AES_ECB_Encrypt(unsigned char *key, int keylen, 
					unsigned char *in, int inlen,
					unsigned char *out, int *outlen);

//AES对称密钥解密
int AES_ECB_Decrypt(unsigned char *key, int keylen, 
					unsigned char *in, int inlen,
					unsigned char *out, int *outlen);

//产生文件尾
int GenerateFileTail(int enctype, unsigned int uid1, unsigned int uid2, unsigned short appid,
					 int keylen, long long filelen, unsigned char *tail);

//解析文件尾
int ParseFileTail(unsigned char *tail, int &enctype, unsigned int &uid1, unsigned int &uid2, unsigned short &appid, long long &filelen);

//是否是密文
int IsCipherFile(char *filename, int &enctype, unsigned int &uid1, unsigned int &uid2,unsigned short &appid, long long &filelen);

//加密文件
int SD_EncryptFile(int enctype, char *hexkey, char *infilename, unsigned int uid1, unsigned int uid2, unsigned short appid, char *outfilename = NULL);

//解密文件
int SD_DecryptFile(int enctype, char *hexkey, char *infilename, char *outfilename = NULL);

#endif
