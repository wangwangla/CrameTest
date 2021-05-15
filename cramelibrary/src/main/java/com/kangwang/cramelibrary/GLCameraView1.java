package com.kangwang.cramelibrary;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLCameraView1 extends GLSurfaceView {
    public GLCameraView1.GLRenderer renderer;
    private CameraUtils mCameraHelper;

//    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;

    public GLCameraView1(Context context) {
        super(context);
        init(context);
    }

    public GLCameraView1(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        renderer = new GLCameraView1.GLRenderer(this);
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

    public class GLRenderer implements Renderer,SurfaceTexture.OnFrameAvailableListener {
        GLSurfaceView surfaceView;

        public GLRenderer(GLSurfaceView surfaceView) {
            this.surfaceView = surfaceView;
            mCameraHelper = new CameraUtils(surfaceView);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            mCameraHelper.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
//            mSurfaceTexture = new SurfaceTexture(mTextureId);
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
            mSurfaceTexture.updateTexImage();
        }

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            //Log.v("aaaaa","avaible");
            surfaceView.requestRender();
        }
        private final Queue<Runnable> runOnDraw = new LinkedList<>();
        private final Queue<Runnable> runOnDrawEnd = new LinkedList<>();

        void runOnDraw(final Runnable runnable) {
            synchronized (runOnDraw) {
                runOnDraw.add(runnable);
            }
        }

        void runOnDrawEnd(final Runnable runnable) {
            synchronized (runOnDrawEnd) {
                runOnDrawEnd.add(runnable);
            }
        }

        private void runAll(Queue<Runnable> queue) {
            synchronized (queue) {
                while (!queue.isEmpty()) {
                    queue.poll().run();
                }
            }
        }
    }
}
