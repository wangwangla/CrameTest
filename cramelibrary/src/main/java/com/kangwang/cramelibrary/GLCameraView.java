package com.kangwang.cramelibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.kangwang.cramelibrary.filter.BaseFilter;
import com.kangwang.cramelibrary.filter.CoolFilter;
import com.kangwang.cramelibrary.filter.OriginalFilter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class GLCameraView extends GLSurfaceView {
    public GLCameraView.GLRenderer renderer;
    private BaseFilter mCurrentFilter;
    private CameraUtils mCameraHelper;
    private Context context;
    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;
    private float[] mSTMatrix = new float[16];

    public GLCameraView(Context context) {
        super(context);
        init(context);
    }

    public GLCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setEGLContextClientVersion(2);
        renderer = new GLCameraView.GLRenderer(this);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        if (mCameraHelper!=null){
            mCameraHelper.releaseCamera();
        }
    }

    public void takePicture(FilteredBitmapCallback imageCallback) {
        imageCallback.onData(capture());
    }

    public void switchCame() {
        mCameraHelper.switchCame();
    }

    public void changeStyle1() {
        runOnDraw(()->{
            mCurrentFilter.releaseProgram();
            int type = 1;
            mCurrentFilter = FilterFactory.createFilter(context,type);
            //调整预览画面
            mCurrentFilter.createProgram();
            mCurrentFilter.onInputSizeChanged(getWidth(),getHeight());
            //调整录像画面
        });
    }


    public class GLRenderer implements Renderer,SurfaceTexture.OnFrameAvailableListener {
        GLSurfaceView surfaceView;
        public GLRenderer(GLSurfaceView surfaceView) {
            this.surfaceView = surfaceView;
            mCameraHelper = new CameraUtils(surfaceView);
            mCurrentFilter = new OriginalFilter(context);
            runOnDraw = new LinkedList<>();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            mCameraHelper.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            mCurrentFilter.createProgram();
            mCurrentFilter.onInputSizeChanged(width, height);
            mTextureId = BaseFilter.bindTexture();
            mSurfaceTexture = new SurfaceTexture(mTextureId);
            mSurfaceTexture.setOnFrameAvailableListener(this);
            mCameraHelper.startPreview(mSurfaceTexture);
        }

        /**
         * 关于预览出现镜像，旋转等问题，有两种方案:
         * 1.在相机预览的地方进行调整
         * 2.通过opengl的矩阵变换在绘制的时候进行调整
         * 这里我采用了前者
         */
        @Override
        public void onDrawFrame(GL10 gl) {
            runAll(runOnDraw);
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mSTMatrix);
            mCurrentFilter.draw(mTextureId, mSTMatrix);
//            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        }

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            surfaceView.requestRender();
        }

    }

    public Bitmap capture(){
        int width = 100;
        int hight = 100;
        GLES20.glPixelStorei(GLES20.GL_PACK_ALIGNMENT, 1);
//
        ByteBuffer buffer = ByteBuffer.allocateDirect(102400);
        buffer.order(ByteOrder.nativeOrder());
        buffer.position(0);
        GLES20.glReadPixels(0, 0, width,hight, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, buffer);
        return null;
    }

    void runOnDraw(final Runnable runnable) {
        synchronized (runOnDraw) {
            runOnDraw.add(runnable);
        }
    }
    private Queue<Runnable> runOnDraw;

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }
}
