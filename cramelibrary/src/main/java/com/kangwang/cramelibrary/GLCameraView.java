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


    public class GLRenderer implements Renderer,SurfaceTexture.OnFrameAvailableListener {
        GLSurfaceView surfaceView;
        public GLRenderer(GLSurfaceView surfaceView) {
            this.surfaceView = surfaceView;
            mCameraHelper = new CameraUtils(surfaceView);
            mCurrentFilter = new OriginalFilter(context);
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
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);
        buffer.put((byte) 4);
        buffer.put((byte) 5);

        buffer.position(0);

        GLES20.glReadPixels(0, 0, width,hight, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, buffer);
        int limit = buffer.limit();

        System.out.println("===============");
//        buffer.rewind();//reset position
//        int pixelCount = width * hight;
////        IntBuffer colors = IntBuffer.allocate(pixelCount);
////            buffer.asIntBuffer().get(colors);
//
//
//        int arr[] = new int[pixelCount];
//        buffer.asIntBuffer().get(arr,0,pixelCount);
//
//        for (int i : arr) {
//            int c = arr[i];   //2.每个int类型的c是接收到的ABGR，但bitmap需要ARGB格式，所以需要交换B和R的位置
//            arr[i] = c & -0xff0100 | (c & 0x00ff0000 >> 16) | (c & 0x000000ff << 16); //交换B和R，得到ARGB
//        }
//        //写入文件
//        FileOutputStream fos = null;
//        Bitmap bmp = null;
//        try {
//            File imageFile = FileUtils.createImageFile();
//            System.out.println("===================start create");
//            fos = new FileOutputStream(imageFile);
//            System.out.println("===================create  end");
//            bmp = Bitmap.createBitmap(arr,0,width,width, hight, Bitmap.Config.ARGB_8888);
//        } catch (IOException e) {
//            System.out.println("-----------");
//            e.printStackTrace();
//            System.out.println("-----------");
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                throw new RuntimeException("Failed to close file $filename");
//            }
//        }
        return null;
    }
}
