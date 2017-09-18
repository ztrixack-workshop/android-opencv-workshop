#include <jni.h>
#include <vector>
#include <opencv2/opencv.hpp>

using namespace cv;
using namespace std;

extern "C"
JNIEXPORT jint JNICALL
Java_com_lazts_app_opencv_OpencvNativeClass_tracking(JNIEnv *env, jclass type,
                                                     jlong threshold, jlong frame) {

    Mat &mThreshold = *(Mat *) threshold;
    Mat &mFrame = *(Mat *) frame;

    dilate(mThreshold, mThreshold, Mat());

    vector<vector<Point> > contours;
    vector<Vec4i> hierarchy;
    findContours(mThreshold, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);

    for (int i = 0; i < contours.size(); i++) {
        drawContours(mFrame, contours, i, Scalar(255, 255, 255), 2, 8, hierarchy, 0);
    }

    if (mFrame.rows == mThreshold.rows && mFrame.cols == mThreshold.cols) return 1;
    else return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_lazts_app_opencv_OpencvNativeClass_threshold(JNIEnv *env, jclass type,
                                                      jlong rgba, jlong threshold) {

    Mat &mRgba = *(Mat *) rgba;
    Mat &mThreshold = *(Mat *) threshold;

    Mat hsv;
    Mat mBgr;
    Mat hsvInv;

    cvtColor(mRgba, mBgr, COLOR_RGBA2BGR);

    blur(mBgr, mBgr, Size(2, 2));

    cvtColor(mBgr, hsv, CV_BGR2HSV);
    cvtColor(~mBgr, hsvInv, CV_BGR2HSV);

    int r = 90;
    int g = 60;
    int b = 120;
    int s = 15;

    Mat markRed;
    Mat markGreen;
    Mat markBlue;

    inRange(hsv, Scalar(g - s, 60, 60), Scalar(g + s, 255, 255), markGreen);
    inRange(hsvInv, Scalar(r - s, 120, 140), Scalar(r + s, 255, 255), markRed);
    inRange(hsv, Scalar(b - s, 70, 50), Scalar(b + s, 255, 255), markBlue);

    addWeighted(markRed, 1.0, markGreen, 1.0, 0.0, mThreshold);
    addWeighted(markBlue, 1.0, mThreshold, 1.0, 0.0, mThreshold);

    GaussianBlur(mThreshold, mThreshold, Size(9, 9), 2, 2);

    if (mRgba.rows == mThreshold.rows && mRgba.cols == mThreshold.cols) return 1;
    else return 0;
}
