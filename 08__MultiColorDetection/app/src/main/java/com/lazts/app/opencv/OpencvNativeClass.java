package com.lazts.app.opencv;

/**
 * @author Tanawat Hongthai - http://www.sic.co.th/
 * @version 1.0.0
 * @since 14/Sep/2017
 */
public class OpencvNativeClass {
    public static native int threshold(long rgba, long threshold);
    public static native int tracking(long threshold, long frame);
}
