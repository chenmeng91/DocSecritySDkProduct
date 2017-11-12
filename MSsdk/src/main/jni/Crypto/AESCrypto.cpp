#include <openssl/aes.h>
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>
#include <time.h>

//将十六进制数字字符串转成二进制字符串
int ConvertSymmectricKey(char *hexkey, unsigned char *key, int &keylen)
{
	char *cp, ch;
	int i, by = 0;
	
    cp = hexkey;	// this is a pointer to the hexadecimal key digits
    i = 0;			// this is a count for the input digits processed
	
    while(i < 64 && *cp)        // the maximum key length is 32 bytes and
    {                           // hence at most 64 hexadecimal digits
        ch = toupper(*cp++);    // process a hexadecimal digit
        if(ch >= '0' && ch <= '9')
            by = (by << 4) + ch - '0';
        else if(ch >= 'A' && ch <= 'F')
            by = (by << 4) + ch - 'A' + 10;
        else                    // error if not hexadecimal
            return 0;
		
        // store a key byte for each pair of hexadecimal digits
        if(i++ & 1)
            key[i / 2 - 1] = by & 0xff;
    }
	
    if(*cp)
		return 0;
    else if(i < 32 || (i & 15))
		return 0;
	
    keylen = i / 2;	
	return 1;
}

//将二进制字符串转成十六进制数字字符串
int ConvertSymmectricKeyToHexKey(unsigned char *key, int keylen, char *hexkey)
{
	if(hexkey == NULL)
		return 0;
	
	for(int i=0; i<keylen; i++)
		sprintf(hexkey+i*2, "%02x", key[i]);
	
	return 1;
}

//产生随机的对称密钥（字节）
int GenerateSymmectricKey(unsigned char *key, int keylen)
{
	if(key == NULL)
		return 0;
	
	srand((unsigned int)time(NULL));
	
	for(int i=0; i<keylen; i++)
		key[i] = (int)(255.0 * rand() / (RAND_MAX + 1.0));
	
	return 1;
}

//AES对称密钥加密
int AES_ECB_Encrypt(unsigned char *key, int keylen, 
					unsigned char *in, int inlen,
					unsigned char *out, int *outlen)
{
	AES_KEY stAesKey = {0};
	int round = inlen / 16;
	int restlen = inlen % 16;
	int count = 0;
	unsigned char groupdata[16] = {0};

	if(keylen%16 != 0 || in == NULL || out == NULL)
		return 0;

	if(AES_set_encrypt_key(key, keylen * 8, &stAesKey) != 0)
		return 0;

	for(count=0; count<round; count++)
		AES_ecb_encrypt(in+count*16, out+count*16, &stAesKey, AES_ENCRYPT);

	if(restlen != 0)
	{
		memcpy(groupdata, in+round*16, restlen);	// zero padding
		AES_ecb_encrypt(groupdata, out+round*16, &stAesKey, AES_ENCRYPT);
		round = round + 1;
	}

	if(outlen != NULL)
		*outlen = round * 16;

	return 1;
} 

//AES对称密钥解密
int AES_ECB_Decrypt(unsigned char *key, int keylen, 
					unsigned char *in, int inlen,
					unsigned char *out, int *outlen)
{
	AES_KEY stAesKey = {0};
	int round = inlen / 16;
	int count = 0;
	
	if(keylen%16 != 0 || inlen%16 != 0 || in == NULL || out == NULL)
		return 0;
	
	if(AES_set_decrypt_key(key, keylen * 8, &stAesKey) != 0)
		return 0;
	
	for(count=0; count<round; count++)
		AES_ecb_encrypt(in+count*16, out+count*16, &stAesKey, AES_DECRYPT);

	if(outlen != NULL)
		*outlen = round * 16;
	
	return 1;
} 

