//
// Created by Administrator on 2017/1/16 0016.
//

#ifndef BAOZOUPTU_EDGECLUR_H
#define BAOZOUPTU_EDGECLUR_H

#endif //BAOZOUPTU_EDGECLUR_H
#include <opencv2/core.hpp>
using namespace cv;
Mat edge_blur(bool **vis, Mat src);
bool** createVisArray(int w, int h);
void deleteVisArray(bool ** &p, int h);
void recordTransparentInfo(Mat &src, bool **transInfo);
void restoreTransparentInfo(Mat &src, bool **transInfo);
