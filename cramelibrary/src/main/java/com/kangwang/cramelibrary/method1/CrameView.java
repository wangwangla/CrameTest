package com.kangwang.cramelibrary.method1;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.kangwang.cramelibrary.CameraUtils;
import com.kangwang.cramelibrary.ImageCallback;


public class CrameView extends GLSurfaceView implements SurfaceHolder.Callback{
    private CameraUtils cameraHelper;

    public CrameView(Context context) {
        super(context);
        init();
    }

    public CrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        SurfaceHolder mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        cameraHelper = new CameraUtils(this);
        cameraHelper.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        cameraHelper.startPreview(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraHelper.releaseCamera();
    }

    /**
     * 拍照
     */
    public void takePicture(ImageCallback imageCallback){
        cameraHelper.takePicture(imageCallback);
    }

    public void switchCarme(){
        cameraHelper.switchCame(getHolder());
    }
}
