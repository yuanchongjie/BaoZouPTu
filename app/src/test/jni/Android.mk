LOCAL_PATH :=$(call my-dir)
include $(CLEAR_VARS)

#opencv
OPENCVROOT:=F:\Android\OpenCV-3.1.0-android-sdk\OpenCV-android-sdk
OPENCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES := off

OPENCV_LIB_TYPE :=STATIC

ifeq ("$(wildcard $(OPENCV_MK_PATH))","")
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk
else
include $(OPENCV_MK_PATH)
endif

LOCAL_MODULE :=pictureSynthesis    #生成的so库的名称
LOCAL_SRC_FILES :=PictureSynthesis.cpp  #源文件名
LOCAL_LDLIBS    += -llog -landroid -lEGL -lGLESv1_CM -ljnigraphics

include $(BUILD_SHARED_LIBRARY)