//
// Created by Administrator on 2017/1/7 0007.
//

#include "a_baozouptu_ptu_tietu_pictureSynthesis_PictureSynthesis.h"
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include <math.h>
#include <stdlib.h>
#include <android/bitmap.h>
#include <android/log.h>

using namespace std;
using namespace cv;

/*
 * Class:     a_baozouptu_ptu_tietu_pictureSynthesis_PictureSynthesis
 * Method:    synthesisBm
 * Signature: (Ljava/lang/Object;Ljava/lang/Object;[I)Ljava/lang/Object;
 */

#ifndef LOGE
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"@",__VA_ARGS__)
#endif

#define max_uchar(a, b)    (((a) > (b)) ? (a) : (b))
#define min_uchar(a, b)    (((a) < (b)) ? (a) : (b))

int transformData(JNIEnv *env, jobject under,
                  jobject above,
                  AndroidBitmapInfo &underInfo,
                  AndroidBitmapInfo &aboveInfo,
                  void *underPixes, void *abovePixes) {
    LOGE("error=%d", "intothe jni");
    int ret;
//解析bitmap
    if ((
                ret = AndroidBitmap_getInfo(env, under, &underInfo)
        ) < 0) {
        LOGE("AndroidBitmap_getInfo() failed !first error=%d", ret);
        return
                -1;
    }
    if ((
                ret = AndroidBitmap_getInfo(env, above, &aboveInfo)
        ) < 0) {
        LOGE("AndroidBitmap_getInfo() failed !second error=%d", ret);
        return
                -1;
    }


    if (underInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888 ||
        underInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888!");
        return
                -1;
    }

//即使有数据也可能发生异常
    if ((
                ret = AndroidBitmap_lockPixels(env, under, &underPixes)
        ) < 0) {
        LOGE("First Bitmap LockPixels Failed return=%d!", ret);
        return
                -1;
    }


    if ((
                ret = AndroidBitmap_lockPixels(env, above, &abovePixes)
        ) < 0) {
        LOGE("Second Bitmap LockPixels Failed return=%d!", ret);
        return
                -1;
    }

    AndroidBitmap_unlockPixels(env, under
    );
    AndroidBitmap_unlockPixels(env, above
    );
    return 1;
}

// 计算彩色图像均值和标准差
void CompMeanAndVariance(Mat &img, Vec3f &mean3f, Vec3f &variance3f) {
    int row = img.rows;
    int col = img.cols;
    int total = row * col;
    float sum[3] = {0.0f};
    // 均值
    uchar *pImg = img.data;
    for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
            sum[0] += pImg[3 * j + 0];
            sum[1] += pImg[3 * j + 1];
            sum[2] += pImg[3 * j + 2];
        }
        pImg += img.step;
    }

    mean3f[0] = sum[0] / total;
    mean3f[1] = sum[1] / total;
    mean3f[2] = sum[2] / total;

    memset(sum, 0, sizeof(sum));
    // 标准差
    pImg = img.data;
    for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
            sum[0] += (pImg[3 * j + 0] - mean3f[0]) * (pImg[3 * j + 0] - mean3f[0]);
            sum[1] += (pImg[3 * j + 1] - mean3f[1]) * (pImg[3 * j + 1] - mean3f[1]);
            sum[2] += (pImg[3 * j + 2] - mean3f[2]) * (pImg[3 * j + 2] - mean3f[2]);
        }
        pImg += img.step;
    }

    variance3f[0] = sqrt(sum[0] / total);
    variance3f[1] = sqrt(sum[1] / total);
    variance3f[2] = sqrt(sum[2] / total);
}

// 颜色转换
void ColorTransfer(Mat &src, Mat &tar, Mat &dst) {
    Mat srcLab, tarLab;
    Vec3f srcMean3f, tarMean3f;// 源/目标图像均值
    Vec3f srcVariance3f, tarVariance3f;// 源/目标图像标准差
    Vec3f ratioVariance3f;// 标准差比例

    // BGR空间转Lab空间
    cvtColor(src, srcLab, CV_BGR2Lab);
    cvtColor(tar, tarLab, CV_BGR2Lab);
    // 计算当前图像与目标图像均值及标准差
    CompMeanAndVariance(srcLab, srcMean3f, srcVariance3f);
    CompMeanAndVariance(tarLab, tarMean3f, tarVariance3f);

    // 标准差比
    ratioVariance3f[0] = tarVariance3f[0] / srcVariance3f[0];
    ratioVariance3f[1] = tarVariance3f[1] / srcVariance3f[1];
    ratioVariance3f[2] = tarVariance3f[2] / srcVariance3f[2];

    // 计算颜色转换值
    int row = srcLab.rows;
    int col = srcLab.cols;
    uchar *pImg = srcLab.data;
    for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
            pImg[3 * j + 0] = (uchar)min_uchar(255, max_uchar(0, ratioVariance3f[0] *
                                                                 (pImg[3 * j + 0] - srcMean3f[0]) +
                                                                 tarMean3f[0]));
            pImg[3 * j + 1] = (uchar)min_uchar(255, max_uchar(0, ratioVariance3f[1] *
                                                                 (pImg[3 * j + 1] - srcMean3f[1]) +
                                                                 tarMean3f[1]));
            pImg[3 * j + 2] = (uchar)min_uchar(255, max_uchar(0, ratioVariance3f[2] *
                                                                 (pImg[3 * j + 2] - srcMean3f[2]) +
                                                                 tarMean3f[2]));
        }
        pImg += srcLab.step;
    }

    // Lab空间转BGR空间
    cvtColor(srcLab, dst, CV_Lab2BGR);
}

JNIEXPORT jintArray

JNICALL Java_a_baozouptu_ptu_tietu_pictureSynthesis_PictureSynthesis_synthesisBm
        (JNIEnv *env, jobject hostObj, jobject under, jobject above, jintArray interRect) {
    AndroidBitmapInfo underInfo;
    void *underPixes;
    AndroidBitmapInfo aboveInfo;
    void *abovePixes;
    int u_height, u_width, a_height, a_width, ret, y, x;
//获取数据并检查有效性
    if (transformData(env, under, above, underInfo, aboveInfo, underPixes, abovePixes)) {
        return NULL;
    }
    u_height = underInfo.height;
    u_width = underInfo.width;
    Mat m_under(u_height, u_width, CV_8UC4, underPixes);

    a_height = aboveInfo.height;
    a_width = aboveInfo.width;
    Mat m_above(a_height, a_width, CV_8UC4, abovePixes);
    if (!(m_under.data && m_above.data)) {
        LOGE("bitmap failed convert to Mat return=%d!", ret);
        return NULL;
    }

    Mat dst;
    ColorTransfer(m_under, m_above, dst);
    int size = u_height * u_width;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size,(int *)dst.data);
    dst.release();
    LOGE("合成bitmap成功，size为", size);
    return result;
}


