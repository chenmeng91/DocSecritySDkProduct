#ifndef _SD_FORMAT_H_
#define _SD_FORMAT_H_


//�ַ���ʾΪ��SD_EETRUST_CIPHER-6BD10B71-722F-
unsigned char EETRUST_MAIN_ENC_FLAG[32] = {
	0x53, 0x44, 0x5F, 0x45, 0x45, 0x54, 0x52, 0x55,
	0x53, 0x54, 0x5F, 0x43, 0x49, 0x50, 0x48, 0x45,
	0x52, 0x2D, 0x36, 0x42, 0x44, 0x31, 0x30, 0x42, 
	0x37, 0x31, 0x2D, 0x37, 0x32, 0x32, 0x46, 0x2D}; 
	
#define MAX_MAIN_ENC_FLAG_SIZE		32
#define MAX_RSA_ENC_OUTPUT_SIZE		128	
#define MAX_RSA_ENC_INPUT_SIZE		117
#define MAX_EXT_DATA_SIZE			32

#define CRYPTO_DESC_SIZE			16
#define FULL_TAIL_LENGTH			512
#define DATA_GROUP_LENGTH			512
#define CRYPT_GROUP_SIZE			4096

#define BLOCK_SIZE_DEFAULT			16
#define BLOCK_SIZE_AES				16
#define BLOCK_SIZE_SM4				16


typedef struct _SD_EETRUST_ENCRYPTED_FLAG_
{
	unsigned int				uid1;			//�ĵ���ID
	unsigned int				uid2;			//�ĵ�ID
	unsigned char				algID;			//�����㷨
	unsigned char				encryptedKey[MAX_RSA_ENC_OUTPUT_SIZE];
	unsigned char				extDataType;
	unsigned char				extEncDataType;
	unsigned int				encryptedKeyLen;
	char						extData[MAX_EXT_DATA_SIZE];
	char						extEncData[MAX_RSA_ENC_OUTPUT_SIZE];

}SD_EETRUST_ENCRYPTED_FLAG, *PSD_EETRUST_ENCRYPTED_FLAG;

typedef struct _SEFS_ENCRYPTED_FLAG_ 
{
	unsigned char				mainFlag[MAX_MAIN_ENC_FLAG_SIZE];	//�ĵ����ܱ�ʶ
	unsigned char				cryptoAlignedSize;					//���ܷ��鳤�� 16
	unsigned char				cryptoKeyLen;						//��Կ���� 16
	unsigned int				cryptoAlgId;						//�����㷨ID 0
	unsigned char				cryptoDesc[CRYPTO_DESC_SIZE];		//�㷨��  NULL
	
	unsigned char				version;							//�汾
	long long						fileSize;							//�ǳ���Ҫ���ֶΣ����������ļ�����ʵ����
	long long						lastEncTime;						//����ʱ��
	
	SD_EETRUST_ENCRYPTED_FLAG	pedBuffer;							//��չ
	
}SEFS_ENCRYPTED_FLAG, *PSEFS_ENCRYPTED_FLAG;

#endif