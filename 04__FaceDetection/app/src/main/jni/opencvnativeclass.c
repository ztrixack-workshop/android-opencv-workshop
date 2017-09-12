JNIEXPORT jintJNICALL
              Java_com_lazts_app_opencv_OpencvNativeClass_faceDetection(JNIEnv *env, jclass type,
                                                                        jstring path_,
                                                                        jlong matRgba,
                                                                        jlong matResult)
{
    const char *path = (*env)->GetStringUTFChars(env, path_, 0);

    // TODO

    (*env)->ReleaseStringUTFChars(env, path_, path);
}