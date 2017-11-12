LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := JniCryptoAPI
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := -llog

LOCAL_SHARED_LIBRARIES := \
	opencrypto \
	openssl

LOCAL_SRC_FILES := \
	ET_Crypto_JNI.cpp \
    SDCrypto.cpp \
    AESCrypto.cpp \

#LOCAL_C_INCLUDES := $(LOCAL_PATH)/include

LOCAL_ARM_MODE := arm
LOCAL_PROGUARD_ENABLED:= disabled
include $(BUILD_SHARED_LIBRARY)
