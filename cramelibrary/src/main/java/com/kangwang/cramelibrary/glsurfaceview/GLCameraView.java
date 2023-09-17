package com.kangwang.cramelibrary.glsurfaceview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;

import com.kangwang.cramelibrary.R;
import com.kangwang.cramelibrary.crame.CameraFocusView;
import com.kangwang.cramelibrary.crame.CameraUtils;
import com.kangwang.cramelibrary.FilterFactory;
import com.kangwang.cramelibrary.FilteredBitmapCallback;
import com.kangwang.cramelibrary.filter.BaseFilter;
import com.kangwang.cramelibrary.filter.OriginalFilter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLCameraView extends GLSurfaceView {
    public GLCameraView.GLRenderer renderer;
    private BaseFilter currentFilter;
    private CameraUtils cameraHelper;
    private Context context;
    private int textureId;
    private SurfaceTexture mSurfaceTexture;
    private float[] mSTMatrix = new float[16];
    private Queue<Runnable> runOnDraw;
    private CameraFocusView cameraFocusView;
    private int width;
    private int height;

    @RequiresApi(api = 30)
    public GLCameraView(Context context) {
        super(context);
        init(context);


//        实例化画笔对象
    }

    public GLCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
//        Display display = context.getDisplay();
//        width = display.getWidth();
//        height = display.getHeight();
//        cameraFocusView = new CameraFocusView(context);
//        cameraFocusView.setMinimumWidth(width);
//        cameraFocusView.setMinimumHeight(height);

        this.context = context;
        setEGLContextClientVersion(2);
        renderer = new GLCameraView.GLRenderer(this);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        if (cameraHelper !=null){
            cameraHelper.releaseCamera();
        }
    }

    public void changeStyle1(int type) {
        runOnDraw(()->{
            currentFilter.releaseProgram();
            currentFilter = FilterFactory.createFilter(context,type);
            //调整预览画面
            currentFilter.createProgram();
            currentFilter.onInputSizeChanged(getWidth(),getHeight());
        });
    }


    public class GLRenderer implements Renderer,SurfaceTexture.OnFrameAvailableListener {
        GLSurfaceView surfaceView;
        public GLRenderer(GLSurfaceView surfaceView) {
            this.surfaceView = surfaceView;
            cameraHelper = new CameraUtils(surfaceView);
            cameraHelper.openCamera(0);
            currentFilter = new OriginalFilter(context);
            runOnDraw = new LinkedList<>();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            cameraHelper.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            currentFilter.createProgram();
            currentFilter.onInputSizeChanged(width, height);
            textureId = BaseFilter.bindTexture();
            mSurfaceTexture = new SurfaceTexture(textureId);
            mSurfaceTexture.setOnFrameAvailableListener(this);
            cameraHelper.startPreview(mSurfaceTexture);
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
            currentFilter.draw(textureId, mSTMatrix);
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

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }

    public void takePicture(FilteredBitmapCallback imageCallback) {
        imageCallback.onData(capture());
    }

    public void switchCame() {
        cameraHelper.switchCame();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
