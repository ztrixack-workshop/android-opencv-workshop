package com.lazts.app.opencv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class MainActivity extends AppCompatActivity implements
        View.OnTouchListener,
        CameraBridgeViewBase.CvCameraViewListener2 {

    private static final int REQUEST_CODE_ASK_SINGLE_PERMISSION = 1000;
    private JavaCameraView jcvCamera;
    private TextView tvCoordinate;
    private ImageView ivColor;
    private TextView tvColor;

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
    private Mat mRgba;
    Scalar sRgba;
    Scalar sHsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        jcvCamera = (JavaCameraView) findViewById(R.id.jcv_camera);
        tvCoordinate = (TextView) findViewById(R.id.tv_coordinate);
        tvColor = (TextView) findViewById(R.id.tv_color);
        ivColor = (ImageView) findViewById(R.id.iv_color);

        jcvCamera.setVisibility(SurfaceView.VISIBLE);
        jcvCamera.setCvCameraViewListener(this);
        jcvCamera.setOnTouchListener(this);

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

        sRgba = new Scalar(255);
        sHsv = new Scalar(255);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int cameraX = mRgba.cols();
        int cameraY = mRgba.rows();

        double offsetX = (jcvCamera.getWidth() - cameraX) / 2.0;
        double offsetY = (jcvCamera.getHeight() - cameraY) / 2.0;

        double x = (motionEvent.getX() - offsetX);
        double y = (motionEvent.getY() - offsetY);

        if (x < 0 || y < 0 || x > cameraX || y > cameraY) return false;

        int color = OpencvNativeClass.getColorDetection(mRgba.getNativeObjAddr(), x, y);

        tvCoordinate.setText("X: " + x + ", Y: " + y);
        tvColor.setText(String.format("Color: #%08X", color));
        ivColor.setBackgroundColor(color);

        return false;
    }
}