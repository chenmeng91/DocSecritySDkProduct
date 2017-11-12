
#APP_ABI := all
#APP_ABI := armeabi
#APP_ABI := mips
#APP_ABI := x86
APP_ABI := armeabi-v7a
APP_OPTIM := debug
# ARM mode will be 'arm', not 'thumb'

APP_PLATFORM := android-19

APP_PROJECT_PATH := $(shell pwd)
APP_BUILD_SCRIPT := $(APP_PROJECT_PATH)/Android.mk
