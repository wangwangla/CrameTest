package com.kangwang.cramelibrary;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.kangwang.cramelibrary.filter.BaseFilter;
import com.kangwang.cramelibrary.filter.CoolFilter;

import javax.microedition.khronos.egl.EGLConfig;
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

    public class GLRenderer implements Renderer,SurfaceTexture.OnFrameAvailableListener {
        GLSurfaceView surfaceView;
        public GLRenderer(GLSurfaceView surfaceView) {
            this.surfaceView = surfaceView;
            mCameraHelper = new CameraUtils(surfaceView);
            mCurrentFilter = new CoolFilter(context);
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
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mSTMatrix);
            mCurrentFilter.draw(mTextureId, mSTMatrix);
        }

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            surfaceView.requestRender();
        }
    }
}
