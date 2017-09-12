package com.lazts.app.opencv;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements
        CameraBridgeViewBase.CvCameraViewListener2 {

    private static final int REQUEST_CODE_ASK_SINGLE_PERMISSION = 1000;
    private JavaCameraView jcvCamera;
    private Mat mRgba, mResult;
    private File mCascadeFile;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    mCascadeFile = createCascadeFile("haarcascade_frontalface_alt2.xml");
                    jcvCamera.enableView();
                    break;

                default:
                    super.onManagerConnected(status);
                    break;

            }
        }
    };

    private File createCascadeFile(String xml) {
        try {
            InputStream is = getAssets().open(xml);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascade = new File(cascadeDir, xml);

            FileOutputStream os;
            os = new FileOutputStream(cascade);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();

            return cascade;
        } catch (IOException e) {
            Log.wtf("IOException", e);
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        mResult = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (mCascadeFile == null) {
            return mRgba;
        }

        OpencvNativeClass.faceDetection(mCascadeFile.getAbsolutePath(),
                mRgba.getNativeObjAddr(), mResult.getNativeObjAddr());

        return mResult;
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