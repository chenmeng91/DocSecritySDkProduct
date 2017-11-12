#include "SDFormat.h"
#include "SDCrypto.h"
#include <string.h>

#ifdef _WIN32
#pragma comment(lib, "libeay32.lib")
#endif

int GenerateFileTail(int enctype, unsigned int uid1, unsigned int uid2, unsigned short appid, 
					 int keylen, long long filelen, unsigned char *tail)
{
	if(tail == NULL)
		return 0;

	SEFS_ENCRYPTED_FLAG SEFS_TAIL = {0};

	memcpy(SEFS_TAIL.mainFlag, EETRUST_MAIN_ENC_FLAG, sizeof(EETRUST_MAIN_ENC_FLAG));

	SEFS_TAIL.pedBuffer.algID = enctype;
	SEFS_TAIL.pedBuffer.uid1 = uid1;
	SEFS_TAIL.pedBuffer.uid2 = uid2;
	memcpy(SEFS_TAIL.pedBuffer.extData,  &appid, sizeof(unsigned short));

	switch(enctype)
	{
	case ENC_TYPE_AES_ECB:
		SEFS_TAIL.cryptoAlignedSize = BLOCK_SIZE_AES;
		break;
	default:
		SEFS_TAIL.cryptoAlignedSize = BLOCK_SIZE_DEFAULT;
	}
	SEFS_TAIL.cryptoKeyLen = keylen;
	SEFS_TAIL.fileSize = filelen;

	memcpy(tail, &SEFS_TAIL, sizeof(SEFS_ENCRYPTED_FLAG));

	return 1;
}

int ParseFileTail(unsigned char *tail, int &enctype, unsigned int &uid1, unsigned int &uid2, unsigned short &appid, long long &filelen)
{
	PSEFS_ENCRYPTED_FLAG pSEFS_TAIL = NULL;

	pSEFS_TAIL = (PSEFS_ENCRYPTED_FLAG)tail;

	if(memcmp(pSEFS_TAIL->mainFlag, EETRUST_MAIN_ENC_FLAG, sizeof(EETRUST_MAIN_ENC_FLAG)) != 0)
	{
		return 0;
	}

	filelen = pSEFS_TAIL->fileSize;

	enctype = pSEFS_TAIL->pedBuffer.algID;
	uid1 = pSEFS_TAIL->pedBuffer.uid1;
	uid2 = pSEFS_TAIL->pedBuffer.uid2;
	memcpy(&appid, pSEFS_TAIL->pedBuffer.extData, sizeof(unsigned short));

	return 1;
}

int IsCipherFile(char *filename, int &enctype, unsigned int &uid1, unsigned int &uid2, unsigned short &appid, long long &filelen)
{
	FILE *fp = NULL;
	unsigned char tail[FULL_TAIL_LENGTH] = {0};
	long filesize = 0;

	if(filename == NULL)
		return 0;
	
	fp = fopen(filename, "rb");
	if(fp == NULL)
		return 0;
	
	fseek(fp, 0, SEEK_END);
	filesize = ftell(fp);
	if(filesize >= FULL_TAIL_LENGTH)
	{
		fseek(fp, 0 - FULL_TAIL_LENGTH, SEEK_END);
		fread(tail, 1, FULL_TAIL_LENGTH, fp);
		fclose(fp);
		
		if(ParseFileTail(tail, enctype, uid1, uid2, appid, filelen) == 1)
			return 1;
	} else
		fclose(fp);
	
	return 0;
}

//加密文件
int SD_EncryptFile(int enctype, char *hexkey, char *infilename, unsigned int uid1, unsigned int uid2, unsigned short appid, char *outfilename)
{
	FILE *fpr = NULL, *fpw = NULL;
	char tmpfilename[4096] = {0};
	unsigned char key[16] = {0};
	int keylen = 16, datalen = CRYPT_GROUP_SIZE, rlen = 0, wlen = 0,  paddinglen = 0;
	unsigned char indata[CRYPT_GROUP_SIZE]={0}, outdata[CRYPT_GROUP_SIZE] = {0};
	unsigned char padding[DATA_GROUP_LENGTH] = {0}, tail[FULL_TAIL_LENGTH] = {0};
	long filesize = 0, rfilelen = 0, wfilelen = 0;

	int m_enctype = 0;
	unsigned int m_uid1 = 0, m_uid2 = 0;
	unsigned short m_appid = 0;
	long long realfilelen = 0;
	
	if(hexkey == NULL || infilename == NULL || enctype != ENC_TYPE_AES_ECB)
		return 0;

	if(ConvertSymmectricKey(hexkey, key, keylen) == 0)
		return 0;

	fpr = fopen(infilename, "rb");
	if(fpr == NULL)
		return 0;

	fseek(fpr, 0, SEEK_END);
	filesize = ftell(fpr);
	if(filesize >= FULL_TAIL_LENGTH)
	{
		fseek(fpr, 0 - FULL_TAIL_LENGTH, SEEK_END);
		fread(tail, 1, FULL_TAIL_LENGTH, fpr);
		if(ParseFileTail(tail, m_enctype, m_uid1, m_uid2, m_appid, realfilelen) == 1)	//已经是加密文档
		{
			fclose(fpr);
			return 0;
		}
	}
	rewind(fpr);
	
	if(outfilename == NULL)
		sprintf(tmpfilename, "%s.enc", infilename);
	else
		strcpy(tmpfilename, outfilename);

	fpw = fopen(tmpfilename, "wb");
	if(fpw == NULL)
	{
		fclose(fpr);
		return 0;
	}

	while(rfilelen < filesize)
	{
		rlen = fread(indata, 1, CRYPT_GROUP_SIZE, fpr);
		if(rlen > 0)
		{
			if(AES_ECB_Encrypt(key, keylen, indata, rlen, outdata, &datalen) == 0)
			{
				fclose(fpr);
				fclose(fpw);
				remove(tmpfilename);
				return 0;
			}

			wlen = fwrite(outdata, 1, datalen, fpw);
			wfilelen += wlen;
		}
		rfilelen += rlen;
	}
	fclose(fpr);

	paddinglen = (DATA_GROUP_LENGTH - wfilelen % DATA_GROUP_LENGTH) % DATA_GROUP_LENGTH;
	if(paddinglen > 0)
		fwrite(padding, 1, paddinglen, fpw);

	if(GenerateFileTail(enctype, uid1, uid2, appid, keylen, filesize, tail) == 0)
	{
		fclose(fpw);
		remove(tmpfilename);
		return 0;
	}
	fwrite(tail, 1, FULL_TAIL_LENGTH, fpw);
	fclose(fpw);

	if(outfilename == NULL)
	{
		if(remove(infilename) != 0)
		{
			remove(tmpfilename);
			return 0;
		} else {
			if(rename(tmpfilename, infilename) != 0)
				return 0;
			else
				return 1;
		}
	}
	return 1;
}

//解密文件
int SD_DecryptFile(int enctype, char *hexkey, char *infilename, char *outfilename)
{
	FILE *fpr = NULL, *fpw = NULL;
	char tmpfilename[4096] = {0};
	unsigned char key[16] = {0}, tail[FULL_TAIL_LENGTH] = {0};
	int keylen = 16, datalen = CRYPT_GROUP_SIZE, rlen = 0, wlen = 0;
	unsigned char indata[CRYPT_GROUP_SIZE]={0}, outdata[CRYPT_GROUP_SIZE] = {0};
	long filesize = 0, rfilelen = 0, wfilelen = 0;

	int m_enctype = 0;
	unsigned int m_uid1 = 0, m_uid2 = 0;
	unsigned short m_appid = 0;
	long long realfilelen = 0;

	if(hexkey == NULL || infilename == NULL || enctype != ENC_TYPE_AES_ECB)
		return 0;

	if(ConvertSymmectricKey(hexkey, key, keylen) == 0)
		return 0;

	fpr = fopen(infilename, "rb");
	if(fpr == NULL)
		return 0;

	fseek(fpr, 0, SEEK_END);
	filesize = ftell(fpr);
	if(filesize < FULL_TAIL_LENGTH)
	{
		fclose(fpr);
		return 0;
	}

	fseek(fpr, 0 - FULL_TAIL_LENGTH, SEEK_END);
	fread(tail, 1, FULL_TAIL_LENGTH, fpr);
	rewind(fpr);
	if(ParseFileTail(tail, m_enctype, m_uid1, m_uid2, m_appid, realfilelen) == 0)	//不是加密文档
	{
		fclose(fpr);
		return 0;
	}

	if(outfilename == NULL)
		sprintf(tmpfilename, "%s.dec", infilename);
	else
		strcpy(tmpfilename, outfilename);

	fpw = fopen(tmpfilename, "wb");
	if(fpw == NULL)
	{
		fclose(fpr);
		return 0;
	}

	while(rfilelen < filesize - FULL_TAIL_LENGTH)
	{
		if(rfilelen + CRYPT_GROUP_SIZE >= filesize)
			rlen = fread(indata, 1, filesize - rfilelen - FULL_TAIL_LENGTH, fpr);
		else
			rlen = fread(indata, 1, CRYPT_GROUP_SIZE, fpr);

		if(rlen != 0)
		{
			if(AES_ECB_Decrypt(key, keylen, indata, rlen, outdata, &datalen) == 0)
			{
				fclose(fpr);
				fclose(fpw);
				remove(tmpfilename);
				return 0;
			}

			if(wfilelen + datalen >= realfilelen)
				wlen = fwrite(outdata, 1, realfilelen - wfilelen, fpw);
			else
				wlen = fwrite(outdata, 1, datalen, fpw);
			wfilelen += wlen;
		}
		rfilelen += rlen;
	}
	fclose(fpr);
	fclose(fpw);

	if(outfilename == NULL)
	{
		if(remove(infilename) != 0)
		{
			remove(tmpfilename);
			return 0;
		} else {
			if(rename(tmpfilename, infilename) != 0)
				return 0;
			else
				return 1;
		}
	}
	return 1;
}
