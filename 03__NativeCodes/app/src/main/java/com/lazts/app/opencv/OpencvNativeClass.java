package com.lazts.app.opencv;

public class OpencvNativeClass {
    public native static int toGray(long matRgba, long matGray);

    public native static int toCanny(long matGray, long matCanny);
}