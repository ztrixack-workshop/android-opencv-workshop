package com.lazts.app.opencv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements
        CameraBridgeViewBase.CvCameraViewListener2 {

    private static final int REQUEST_CODE_ASK_SINGLE_PERMISSION = 1000;
    private JavaCameraView jcvCamera;
    private Mat mRgba;
    private Mat mThreshold;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    jcvCamera.enableView();
                    break;

                default:
                    super.onManagerConnected(status);
                    break;

            }
        }
    };

    static {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        jcvCamera.setVisibility(SurfaceView.VISIBLE);
        jcvCamera = (JavaCameraView) findViewById(R.id.jcv_camera);
        jcvCamera.setCvCameraViewListener(this);

        requestCameraPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (jcvCamera != null) {
            jcvCamera.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d("OpenCVLoader", "SUCCESSFULLY LOADED");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d("OpenCVLoader", "NOT LOADED");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0,
                    this, mLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (jcvCamera != null) {
            jcvCamera.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mThreshold = new Mat(height, width, CvType.CV_8UC3);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mThreshold.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        OpencvNativeClass.threshold(mRgba.getNativeObjAddr(), mThreshold.getNativeObjAddr());
        OpencvNativeClass.tracking(mThreshold.getNativeObjAddr(), mRgba.getNativeObjAddr());

        return mRgba;
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_SINGLE_PERMISSION);
        }
    }

}