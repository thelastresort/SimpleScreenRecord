LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ImageToVideo
LOCAL_SRC_FILES := ImageToVideo.cpp
LOCAL_LDLIBS+= -L$(SYSROOT)/usr/lib -llog

include $(BUILD_SHARED_LIBRARY)

include $(LOCAL_PATH)/prebuilt/Android.mk
