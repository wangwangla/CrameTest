package com.kangwang.crame.render;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.transition.ChangeBounds;

import com.kangwang.crame.constant.Constant;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * 目标1: 一般显示原色,一般显示黑白
 * 目标2:将图片显示在一个圆里面
 *
 * 显示正圆需要一个摸具传进来
 * 目标3:冷色  暖色 ^
 */
public class PhotoDraw {
    private final String vertexShaderCode;
    /**
     * 相机显示   将坐标系发生了变换,比如xx.x>0.4世界上是y上发生了变化,
     */
    private final String fragmentShaderCode;
    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawListBuffer;
    private int mProgram;
    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int vMatrix;
//    private int offsetY ;

    private short drawOrder[] = {0, 1, 2, 0, 2, 3};

    private static final int COORDS_PER_VERTEX = 2;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    static float xx = 1f;
    static float squareCoords[] = {
            -1.0f*xx, 1.0f*xx,
            -1.0f*xx, -1.0f*xx,
            1.0f*xx, -1.0f*xx,
            1.0f*xx, 1.0f*xx,
    };

    float yy = 1f;
    float textureVertices[] = {
            1-yy, 1.0f*yy,
            1.0f*yy, 1.0f*yy,
            1.0f*yy, 1-yy,
            1-yy, 1-yy,
    };

    private int texture;
    private float[] mMVPMatrix=new float[16];
    private float[] model = new float[16];
    void changeSize(){
        float yy = 1f;
        float textureVertices[] = {
                1-yy, 1.0f*yy,
                1.0f*yy, 1.0f*yy,
                1.0f*yy, 1-yy,
                1-yy, 1-yy,
        };

    }
    private Context context;
    public PhotoDraw(int texture, Context context) {
        this.texture = texture;
        this.context = context;
        //顶点坐标
        vertexShaderCode = uRes("shader/vershader.sh");
        fragmentShaderCode = uRes("shader/fragment.sh");
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);
        //顶点绘制顺序
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        //纹理坐标
        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        textureVerticesBuffer = bb2.asFloatBuffer();
        textureVerticesBuffer.put(textureVertices);
        textureVerticesBuffer.position(0);
        //编译着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        vMatrix = GLES20.glGetUniformLocation(mProgram,"vMatrix");
//        offsetY = GLES20.glGetUniformLocation(mProgram,"offsetY");
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);
        //使用纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
        //顶点位置
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        //纹理坐标
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureVerticesBuffer);
        GLES20.glUniformMatrix4fv(vMatrix,1,false,mMVPMatrix,0);
//        GLES20.glUniform1f(offsetY,offsetY);
        //绘制
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        //结束
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
    }

    //编译着色器
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


    private float[] mViewMatrix=new float[16];
    //透视
    private float[] mProjectMatrix=new float[16];

    public void surfaceChange(int width, int height, float textWidth, float textHight) {
        System.out.println(width+"---------"+height);
        GLES20.glViewport(0,0,width,height);
        float sWH= textHight / textWidth;
        float sWidthHeight=width/(float)height;
        if(width<height){
            if(sWH>sWidthHeight){
            Matrix.orthoM(mProjectMatrix, 0, -1, 1, -1/sWidthHeight*sWH, 1/sWidthHeight*sWH,3, 7);
        }else{
            Matrix.orthoM(mProjectMatrix, 0, -1, 1, -sWH/sWidthHeight, sWH/sWidthHeight,3, 7);
        }

        }else{
            if(sWH<sWidthHeight){
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight*sWH,sWidthHeight*sWH, -1,1, 3, 7);
            }else{
                Matrix.orthoM(mProjectMatrix, 0, -sWidthHeight/sWH,sWidthHeight/sWH, -1,1, 3, 7);
            }
        }
        Matrix.setIdentityM(model,0);
//        Matrix.scaleM(model,0,1,1,1);
        Matrix.translateM(model,0,0,0,2);
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mViewMatrix,0,mProjectMatrix,0);
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mMVPMatrix,0);
    }


    public String uRes(String path){
        if (context == null) System.out.println("八嘎!");
        StringBuilder result=new StringBuilder();
        try{
            InputStream is=context.getAssets().open(path);
            int ch;
            byte[] buffer=new byte[1024];
            while (-1!=(ch=is.read(buffer))){
                result.append(new String(buffer,0,ch));
            }
        }catch (Exception e){
            return null;
        }
        return result.toString().replaceAll("\\r\\n","\n");
    }

}