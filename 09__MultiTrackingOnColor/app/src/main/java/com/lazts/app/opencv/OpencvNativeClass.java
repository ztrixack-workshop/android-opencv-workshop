package com.lazts.app.opencv;

/**
 * @author Tanawat Hongthai - http://www.sic.co.th/
 * @version 1.0.0
 * @since 14/Sep/2017
 */
public class OpencvNativeClass {
    public static native int cvtHsv(long rgba, long hsv);
    public static native int cvtThreshold(long hsv, long threshold);
    public static native int tracking(long threshold, long scribble, long rgba);
}
