#ifndef __SD_CRYPT_H_
#define __SD_CRYPT_H_

#include <stdio.h>

//ENC_TYPE
#define ENC_TYPE_AES_ECB			0x01


//��������ĶԳ���Կ���ֽ����飩
int GenerateSymmectricKey(unsigned char *key, int keylen);

//��ʮ�����������ַ���ת���ֽ�����
int ConvertSymmectricKey(char *hexkey, unsigned char *key, int &keylen);

//���ֽ�����ת��ʮ�����������ַ���
int ConvertSymmectricKeyToHexKey(unsigned char *key, int keylen, char *hexkey);

//AES�Գ���Կ����
int AES_ECB_Encrypt(unsigned char *key, int keylen, 
					unsigned char *in, int inlen,
					unsigned char *out, int *outlen);

//AES�Գ���Կ����
int AES_ECB_Decrypt(unsigned char *key, int keylen, 
					unsigned char *in, int inlen,
					unsigned char *out, int *outlen);

//�����ļ�β
int GenerateFileTail(int enctype, unsigned int uid1, unsigned int uid2, unsigned short appid,
					 int keylen, long long filelen, unsigned char *tail);

//�����ļ�β
int ParseFileTail(unsigned char *tail, int &enctype, unsigned int &uid1, unsigned int &uid2, unsigned short &appid, long long &filelen);

//�Ƿ�������
int IsCipherFile(char *filename, int &enctype, unsigned int &uid1, unsigned int &uid2,unsigned short &appid, long long &filelen);

//�����ļ�
int SD_EncryptFile(int enctype, char *hexkey, char *infilename, unsigned int uid1, unsigned int uid2, unsigned short appid, char *outfilename = NULL);

//�����ļ�
int SD_DecryptFile(int enctype, char *hexkey, char *infilename, char *outfilename = NULL);

#endif
