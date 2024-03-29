package com.kangwang.cramelibrary.crame;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kangwang.cramelibrary.ImageCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraUtils implements Camera.AutoFocusCallback, View.OnTouchListener,View.OnClickListener {
    public Camera camera;
    public static int cameraId = 0;
    public static int orientation = 0;
    public SurfaceView surfaceView;
    public int fitWidth;
    public int fitHeight;
    public SurfaceTexture texture;

    public CameraUtils(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        surfaceView.setOnTouchListener(this::onTouch);
    }

    public void startPreview(SurfaceTexture texture0) {
        this.texture = texture0;
        try {
            camera.setPreviewTexture(texture0);
            camera.startPreview();
            camera.autoFocus(this::onAutoFocus);
        } catch (IOException e) {
            Log.v("glcamera",e.getMessage());
        }
    }

    protected void focusOnRect(Rect rect) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters(); // 先获取当前相机的参数配置对象
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO); // 设置聚焦模式
//            Log.d(TAG, "parameters.getMaxNumFocusAreas() : " + parameters.getMaxNumFocusAreas());
            if (parameters.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<>();
                focusAreas.add(new Camera.Area(rect, 1000));
                parameters.setFocusAreas(focusAreas);
            }
            camera.cancelAutoFocus(); // 先要取消掉进程中所有的聚焦功能
            camera.setParameters(parameters); // 一定要记得把相应参数设置给相机
            camera.autoFocus(this::onAutoFocus);
        }
    }

    /**
     * 打开相机
     */
    public void openCamera(int mCameraId0){
        try {
            cameraId = mCameraId0;
            camera = Camera.open(mCameraId0);
            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            //1.设置预览尺寸，防止预览画面变形
            List<Camera.Size> sizes1 = parameters.getSupportedPreviewSizes(); //得到的比例，宽是大头
            //比例可以查询可以显示的大小，然后选择一个近似值
            int[] result1 = getOptimalSize(sizes1, surfaceView.getWidth(), surfaceView.getHeight());
            parameters.setPreviewSize(result1[0], result1[1]);
            fitWidth = result1[0];
            fitHeight = result1[1];
            //2.设置拍照取得的图片尺寸
            List<Camera.Size>sizes2 = parameters.getSupportedPictureSizes();
            int[] result2 = getOptimalSize(sizes2,surfaceView.getWidth(),surfaceView.getHeight());
            parameters.setPictureSize(result2[0],result2[1]);
            camera.setParameters(parameters);
            //设置相机方向
            setCameraDisplayOrientation(cameraId);
        }catch (Exception e){
            Log.v("aaaaa",e.getMessage());
        }
    }

    public void startPreview(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
//            Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
//
//                public void onAutoFocus(boolean success, Camera camera) {
//                    // TODO Auto-generated method stub
//                    System.out.println("===聚焦成功@@@@@@@@@@@@@@@@@@@@@@@@");
//                    if(success){
//                        camera.setOneShotPreviewCallback(null);
////                        Toast.makeText(.this,
////                                "自动聚焦成功" , Toast.LENGTH_SHORT).show();
//                        System.out.println("===聚焦成功--------------");
//                    }
//                }
//            };
            camera.autoFocus(this::onAutoFocus);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放相机
     */
    public void releaseCamera() {
        if (camera !=null){
            camera.stopPreview();
            //释放向机
            camera.release();
            camera = null;
        }
    }

    /**
     * *找出最接近的尺寸，以保证图像不会被拉伸
     * @param sizes
     * @param currentWidth
     * @param currentHeight
     * @return
     */
    private int[] getOptimalSize(List<Camera.Size> sizes, int currentWidth, int currentHeight) {
        int i = 1;
        //大头
        int bestWidth = sizes.get(0).width;
        //小头
        int bestHeight = sizes.get(0).height;
        //很重要，第一项一定是高/宽
        float min = Math.abs((float) bestHeight / (float) bestWidth -
                (float) currentWidth / (float) currentHeight);
        while (i < sizes.size()) {
            float current = Math.abs((float) sizes.get(i).height / (float) sizes.get(i).width - (float) currentWidth / (float) currentHeight);
            if (current < min) {
                min = current;
                bestWidth = sizes.get(i).width;
                bestHeight = sizes.get(i).height;
            }
            System.out.println(current+"=="+min+"===="+bestHeight+"===="+bestWidth);
            i++;
        }
        int[] result = new int[2];
        result[0] = bestWidth;
        result[1] = bestHeight;
        Log.v("glcamera", bestWidth + "//" + bestHeight);
        return result;
    }

    /**
     * 根据手机屏幕以及前后摄来调整相机角度
     *
     * @param cameraId
     */
    private void setCameraDisplayOrientation(int cameraId) {
        Activity targetActivity = (Activity) surfaceView.getContext();
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = targetActivity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        orientation = result;
    }

    /**
     * 拍照
     * @param imageCallback
     */
    public void takePicture(final ImageCallback imageCallback) {
        camera.takePicture(null, null, (data, camera) ->
                imageCallback.onData(data, new ImageCallback.kkk() {
                    @Override
                    public void run1() {
                        camera.stopPreview();
                        camera.startPreview();
                    }
                }));

    }

    public void switchCame() {
        if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        releaseCamera();
        openCamera(cameraId);
        startPreview(texture);
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (success){
            System.out.println("SUCCESS 自动聚焦！！！！！！！！！！");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        focusOnTouch((int)event.getX(),(int)event.getY());
        return false;
    }

    private void focusOnTouch(int x, int y) {
        Rect rect = new Rect(x - 100, y - 100, x + 100, y + 100);
        int left = rect.left * 2000 / surfaceView.getWidth() - 1000;
        int top = rect.top * 2000 / surfaceView.getHeight() - 1000;
        int right = rect.right * 2000 / surfaceView.getWidth() - 1000;
        int bottom = rect.bottom * 2000 / surfaceView.getHeight() - 1000;
        // 如果超出了(-1000,1000)到(1000, 1000)的范围，则会导致相机崩溃
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        focusOnRect(new Rect(left, top, right, bottom));
    }

    @Override
    public void onClick(View v) {

    }


}
