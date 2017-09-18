#include <jni.h>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"
JNIEXPORT jint JNICALL
Java_com_lazts_app_opencv_OpencvNativeClass_tracking(JNIEnv *env, jclass type,
                                                     jlong threshold, jlong scribble, jlong frame) {

    Mat &mThreshold = *(Mat *) threshold;
    Mat &mScribble = *(Mat *) scribble;
    Mat &mFrame = *(Mat *) frame;

    static int posX = -1;
    static int posY = -1;

    Moments moment = moments(mThreshold);
    double mm10 = moment.m10;
    double mm01 = moment.m01;
    double area = moment.m00;

    if (area > 1000) {
        int lastX = posX;
        int lastY = posY;

        posX = (int) (mm10 / area);
        posY = (int) (mm01 / area);

        if (lastX > 0 && lastY > 0 && posX > 0 && posY > 0) {
            // Draw a yellow line from the previous point to the current point
            line(mScribble, Point(posX, posY), Point(lastX, lastY), Scalar(0, 255, 255), 5);

            add(mFrame, mScribble, mFrame);
        }

        circle(mFrame, Point(posX, posY), 32, Scalar(255, 0, 0), 2, 8, 0);
        return 1;
    } else {
        posX = -1;
        posY = -1;
        mScribble.release();
        mScribble = Mat(mThreshold.rows, mThreshold.cols, CV_8UC3);
        return 0;
    }

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_lazts_app_opencv_OpencvNativeClass_cvtThreshold(JNIEnv *env, jclass type,
                                                         jlong hsv, jlong threshold) {

    Mat &mHsv = *(Mat *) hsv;
    Mat &mThreshold = *(Mat *) threshold;

    Scalar lb = Scalar(20, 100, 100);
    Scalar ub = Scalar(30, 255, 255);

    inRange(mHsv, lb, ub, mThreshold);

    if (mHsv.cols == mThreshold.cols && mHsv.rows == mThreshold.rows) return 1;
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_lazts_app_opencv_OpencvNativeClass_cvtHsv(JNIEnv *env, jclass type,
                                                   jlong rgba, jlong hsv) {

    Mat &mRgba = *(Mat *) rgba;
    Mat &mHsv = *(Mat *) hsv;

    cvtColor(mRgba, mRgba, CV_RGBA2RGB);
    cvtColor(mRgba, mHsv, CV_RGB2HSV);

    if (mRgba.cols == mHsv.cols && mRgba.rows == mHsv.rows) return 1;
    return 0;
}
