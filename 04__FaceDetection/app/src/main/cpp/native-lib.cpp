#include <jni.h>
#include <vector>
#include "opencv2/opencv.hpp"

using namespace cv;
using namespace std;

extern "C"
JNIEXPORT jint JNICALL
Java_com_lazts_app_opencv_OpencvNativeClass_faceDetection(JNIEnv *env, jclass type,
                                                          jstring path,
                                                          jlong matRgba,
                                                          jlong matResult) {
    const char *jnamestr = env->GetStringUTFChars(path, NULL);
    String stdFileName(jnamestr);

    Mat &mRgba = *(Mat *) matRgba;
    Mat &mResult = *(Mat *) matResult;

    Mat gray;
    vector<Rect> faces;

    mRgba.copyTo(mResult);

    cvtColor(mRgba, gray, CV_RGBA2GRAY);

    CascadeClassifier face_cascade;
    face_cascade.load(stdFileName);

    face_cascade.detectMultiScale(gray, faces, 2, 1,
                                  CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_SCALE_IMAGE,
                                  Size(30, 30), Size(900, 900));

    if (faces.size() > 0) {
        int index;
        Rect face;
        for (index = 0; index < faces.size(); index++) {
            face = faces[index];
            rectangle(mResult, face, Scalar(255, 0, 0), 3);
        }
        return 1;
    } else {
        return 0;
    }
}