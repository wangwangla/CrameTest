package com.kangwang.cramelibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLCameraView extends GLSurfaceView {
    public GLCameraView.GLRenderer renderer;
    private CameraUtils mCameraHelper;
    private static final String TAG = "aaaaa";
    private Context c;
    private int mTextureId;
    private SurfaceTexture mSurfaceTexture;
    private float[] mSTMatrix = new float[16];
    private boolean mRecordingEnabled ;
    private static final int DEFAULT_BITRATE = 1000 * 1000;
    private int mChannels = 1;
    private int mSampleRate = 48000;
    private File mOutputFile;
    private static int STATE_ON = 1;
    private static int STATE_OFF = 2;
    private int state = 2;
    WhiteCatFilter whiteCatFilter;

    public GLCameraView(Context context) {
        super(context);
        init(context);
    }

    public GLCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        this.c = context;
        whiteCatFilter = new WhiteCatFilter(context);
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

    public class GLRenderer implements Renderer,SurfaceTexture.OnFrameAvailableListener {
        GLSurfaceView surfaceView;

        public GLRenderer(GLSurfaceView surfaceView) {
            this.surfaceView = surfaceView;
            mRecordingEnabled = false;
            mCameraHelper = new CameraUtils(surfaceView);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            mCameraHelper.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            whiteCatFilter.createProgram();
            whiteCatFilter.onInputSizeChanged(getWidth(),getHeight());

            mTextureId = BaseGameScreen.bindTexture();
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
            mSurfaceTexture.updateTexImage();
            whiteCatFilter.draw(mTextureId,mSTMatrix);
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
