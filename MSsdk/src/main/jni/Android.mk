LOCAL_PATH := $(call my-dir)

BUILD_TOP_DIR := $(LOCAL_PATH)
THIRDPARTY_TOP_DIR := $(BUILD_TOP_DIR)/../../../../thirdparty

include $(THIRDPARTY_TOP_DIR)/openssl/openssl-1.0.1i/Android.mk

include $(BUILD_TOP_DIR)/Crypto/Android.mk

#include $(call all-subdir-makefiles)
