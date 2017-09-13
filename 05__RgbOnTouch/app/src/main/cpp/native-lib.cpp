#include <jni.h>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"
JNIEXPORT jint JNICALL
Java_com_lazts_app_opencv_OpencvNativeClass_getColorDetection(JNIEnv *env, jclass type,
                                                              jlong matRgba, jdouble x, jdouble y) {

    Mat &mRgba = *(Mat *) matRgba;

    Rect regionRect;

    regionRect.x = (int) round(x);
    regionRect.y = (int) round(y);
    regionRect.width = 8;
    regionRect.height = 8;

    Mat regionRgba = Mat(mRgba, regionRect);
    Mat regionHsv;

    cvtColor(regionRgba, regionHsv, COLOR_RGB2HSV_FULL);

    Scalar sHsv = sum(regionHsv);
    int pc = regionRect.width * regionRect.height;
    int size = sizeof(sHsv.val) / sizeof(*sHsv.val);
    for (int i = 0; i < size; ++i) {
        sHsv.val[i] /= pc;
    }

    Mat pmRgba;
    Mat pmHsv = Mat(1, 1, CV_8UC3, sHsv);
    cvtColor(pmHsv, pmRgba, COLOR_HSV2RGB_FULL, 4);
    Scalar sRgba = mean(pmRgba);

    return ((int) sRgba.val[3] << 24) +
           ((int) sRgba.val[0] << 16) +
           ((int) sRgba.val[1] << 8) +
           ((int) sRgba.val[2]);
}