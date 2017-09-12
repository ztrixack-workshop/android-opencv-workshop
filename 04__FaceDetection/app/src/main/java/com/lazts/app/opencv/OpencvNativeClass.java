package com.lazts.app.opencv;

public class OpencvNativeClass {
    public native static int faceDetection(String path, long matRgba, long matResult);
}