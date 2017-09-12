#include <jni.h>
#include "opencv2/opencv.hpp"

using namespace cv;

extern "C"
JNIEXPORT jint JNICALL
Java_com_lazts_app_opencv_OpencvNativeClass_toGray(JNIEnv *env, jclass type, jlong matRgba,
                                                       jlong matGray) {

    Mat &mRgba = *(Mat *) matRgba;
    Mat &mGray = *(Mat *) matGray;

    cvtColor(mRgba, mGray, CV_RGBA2GRAY);

    if (mGray.rows == mRgba.rows && mGray.cols == mRgba.cols) {
        return 1;
    }

    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_lazts_app_opencv_OpencvNativeClass_toCanny(JNIEnv *env, jclass type, jlong matGray,
                                                        jlong matCanny) {

    Mat &mGray = *(Mat *) matGray;
    Mat &mCanny = *(Mat *) matCanny;

    Canny(mGray, mCanny, 50, 150);

    if (mGray.rows == mCanny.rows && mGray.cols == mCanny.cols) {
        return 1;
    }

    return 0;

}