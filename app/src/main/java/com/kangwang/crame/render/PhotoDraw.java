package com.kangwang.crame.render;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.kangwang.crame.constant.Constant;

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
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec2 inputTextureCoordinate;" +
                    "varying vec2 textureCoordinate;" +
                    "uniform mat4 vMatrix;" +
                    "void main()" +
                    "{" +
                        "gl_Position = vPosition * vMatrix;" +
                        "textureCoordinate = inputTextureCoordinate;" +
                    "}";

    /**
     * 相机显示   将坐标系发生了变换,比如xx.x>0.4世界上是y上发生了变化,
     *
     *
     * 下图方式
     * y---------|
     *          |
     *          |
     *          |
     *          x
     */
    private final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform samplerExternalOES s_texture;\n" +
                    "void main() {" +
                        "vec2 uv = textureCoordinate;" +
                    "if(uv.x <= 0.33){" +
                    "   uv.x =uv.x * 3.0;" +


                    "}else if(uv.x <=0.66){" +
                    "   uv.x = (uv.x - 0.33)*3.0;" +
                    "gl_FragColor=texture2D(s_texture,uv);" +
                    "}else{" +
                    "   uv.x = (uv.x - 0.66)*3.0;" +
                    "gl_FragColor=texture2D(s_texture,uv);" +
                    "}" +

                    "       if(uv.y <= 0.33){" +
                    "   uv.y =uv.y * 3.0;" +
                    "    vec4 nColor=texture2D(s_texture,uv);" +
                    "    float c = nColor.r * 0.3 + nColor.g * 0.59 + nColor.b * 0.11;" +
                    "    gl_FragColor=vec4(c,c,c,nColor.a);" +
                    "}else if(uv.y <= 0.66){" +
                    "   uv.y = (uv.y - 0.33)*3.0;" +
                    "gl_FragColor=texture2D(s_texture,uv);" +
                    "}else{" +
                    "   uv.y = (uv.y - 0.66)*3.0;" +
                    "gl_FragColor=texture2D(s_texture,uv);" +
                    "}" +


                    "}";
    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawListBuffer;
    int mProgram;
    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int vMatrix;

    private short drawOrder[] = {0, 1, 2, 0, 2, 3};

    private static final int COORDS_PER_VERTEX = 2;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    static float squareCoords[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f,
    };

    static float textureVertices[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    private int texture;
    private float[] mMVPMatrix=new float[16];

    public PhotoDraw(int texture) {
        this.texture = texture;
        //顶点坐标
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
        GLES20.glViewport(0,0,width,height);
        float sWH= textHight / textWidth;
        float sWidthHeight=width/(float)height;


//        GLES20.glViewport(0,0,width,(height));
//        float sWH= textHight / textWidth;
//        float sWidthHeight=(width)/(float)height;
//        sWH = sWidthHeight;
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
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mViewMatrix,0);
    }

}