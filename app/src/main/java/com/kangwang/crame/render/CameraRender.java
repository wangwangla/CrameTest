package com.kangwang.crame.render;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.kangwang.crame.MainActivity;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRender implements GLSurfaceView.Renderer {
    private Camera mCamera;
    private boolean isPreviewing = false;
    private float textWidth;
    private float textHight;
    private Context context;
    public CameraRender(MainActivity mainActivity) {
        context = mainActivity;
    }

    public void create() {
        //创建一个纹理
        int textureId = createTextureId();
        //将纹理 和  surface相关联
        texture = new SurfaceTexture(textureId);
        //将纹理 传入  渲染中
        drawwe = new PhotoDraw(textureId,context);
        //打开相机   相机将预览画面放入到surface中
        doOpenCamera();
    }

    public void changeSize(float scale){
        surfaceHight = surfaceWidth/scale;
        surfaceWidth = surfaceWidth/scale;
        drawwe.surfaceChange((int)surfaceWidth,(int)surfaceHight,textWidth,textHight);
        drawwe.changeSize();
    }



    public void doOpenCamera() {
        if (mCamera == null) {
            mCamera = Camera.open();
        } else {
            doStopCamera();
        }
    }

    /*使用TextureView预览Camera*/
    public void doStartPreview() {
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                //将相机画面预览到纹理层上,纹理层有数据了，再通知view绘制,此时未开始预览
                mCamera.setPreviewTexture(texture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //真正开启预览,Camera.startPrieView()
            initCamera();

            boolean supportZoom = isSupportZoom();
            if (supportZoom) {
                setZoom(supportZoom);
            }
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    System.out.println(success);
                }
            });
        }
    }

    public void setZoom(boolean mIsSupportZoom)
    {
        if (mIsSupportZoom)
        {
            try
            {
                Camera.Parameters params = mCamera.getParameters();
                final int MAX = params.getMaxZoom();
                if(MAX==0)return;
                int zoomValue = params.getZoom();
                System.out.println("-----------------MAX:"+MAX+"   params : "+zoomValue);
//                zoomValue +=;
                params.setZoom(zoomValue);
                mCamera.setParameters(params);
                System.out.println("Is support Zoom " + params.isZoomSupported());
            }
            catch (Exception e)
            {
                System.out.println("--------exception zoom");
                e.printStackTrace();
            }
        }
        else
        {
//            Trace.Log("--------the phone not support zoom");
        }
    }

    public boolean isSupportZoom()
    {
        boolean isSuppport = true;
        if (mCamera.getParameters().isSmoothZoomSupported())
        {
            isSuppport = false;
        }
        return isSuppport;
    }

    /**
     * 停止预览，释放Camera
     * <p>
     * 在执行stop的时候，又执行了回调方法
     */
    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.Parameters mParams;

    private void initCamera() {
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
            mCamera.setDisplayOrientation(90);
            //设置摄像头为持续自动聚焦模式
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            mCamera.setParameters(mParams);
            mCamera.startPreview();//开启预览
            //设置预览标志位
            isPreviewing = true;
            Camera.Size previewSize = mParams.getPreviewSize();
            System.out.println(previewSize.width + "previewSize.width" + previewSize.height);
            textWidth = previewSize.width;
            textHight = previewSize.height;
        }
    }

    public void render() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //从图像流中将纹理图像更新为最近的帧
        texture.updateTexImage();
        drawwe.draw();
    }

    public void surfaceChange(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        if (!isPreviewing) {
            doStartPreview();
        }
        drawwe.surfaceChange(width, height, textWidth, textHight);
        //如果还未预览，就开始预览

    }

    public void dispose() {

    }

    private int createTextureId() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    private SurfaceTexture texture;
    private PhotoDraw drawwe;


    public void resume() {
        System.out.println("resume........");
        doStartPreview();
    }

    public void pause() {
        System.out.println("pause........");
        doStopCamera();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        create();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        surfaceChange(width,height);
        this.surfaceHight = height;
        this.surfaceWidth = width;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        render();
    }

    private float surfaceWidth;
    private float surfaceHight;


    public void autoFocus() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                System.out.println(success);
            }
        });

        mCamera.setAutoFocusMoveCallback(new Camera.AutoFocusMoveCallback() {
            @Override
            public void onAutoFocusMoving(boolean start, Camera camera) {

            }
        });
        
    }
}