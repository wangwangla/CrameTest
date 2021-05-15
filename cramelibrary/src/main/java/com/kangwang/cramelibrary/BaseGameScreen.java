package com.kangwang.cramelibrary;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class BaseGameScreen {

        protected float color[];
        protected float triangleCoords[];
        protected String fragmentShaderCode;
        protected String vertexShaderCode;
        protected FloatBuffer vertexBuffer;
        protected FloatBuffer colorBuffer;
        protected int mProgram;
        private Resources mRes;

        public BaseGameScreen() {
        }

        public BaseGameScreen(Resources resources) {
            this.mRes = resources;
        }

        public int loadShader(int type, String shaderCode) {
            //根据type创建顶点着色器或者片元着色器
            int shader = GLES20.glCreateShader(type);
            //将资源加入到着色器中，并编译
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            int[] arr = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, arr, 0);
            int i = arr[0];
            if (i == 0) {
                //失败了
                int[] length = new int[1];
                GLES20.glGetShaderiv(shader, GLES20.GL_INFO_LOG_LENGTH, length, 0);
                if (length[0] > 0) {
                    String s = GLES20.glGetShaderInfoLog(shader);
                    System.out.println(s);
                }
            }

            return shader;
        }

        public abstract void render();

        public void create() {
            ByteBuffer bb = ByteBuffer.allocateDirect(
                    triangleCoords.length * 4);
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            vertexBuffer.put(triangleCoords);
            vertexBuffer.position(0);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(color.length * 4);
            byteBuffer.order(ByteOrder.nativeOrder());
            colorBuffer = byteBuffer.asFloatBuffer();
            colorBuffer.put(color);
            colorBuffer.position(0);

            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);
            int lin[] = new int[1];
            GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, lin, 0);
            if (lin[0] == 0) {
                String s = GLES20.glGetProgramInfoLog(mProgram);
                System.out.println(s);
            }
            GLES20.glDeleteShader(1);
        }

        public abstract void surfaceChange(int width, int height);

        public abstract void dispose();

        public void resume() {

        }

        public void pause() {
        }

//    protected final void createProgramByAssetsFile(String vertex,String fragment){
////        createProgram(uRes(mRes,vertex),uRes(mRes,fragment));
////        String s = uRes(mRes, vertex);
//    }

        public String uRes(String path) {
            if (mRes == null) System.out.println("八嘎!");
            StringBuilder result = new StringBuilder();
            try {
                InputStream is = mRes.getAssets().open(path);
                int ch;
                byte[] buffer = new byte[1024];
                while (-1 != (ch = is.read(buffer))) {
                    result.append(new String(buffer, 0, ch));
                }
            } catch (Exception e) {
                return null;
            }
            return result.toString().replaceAll("\\r\\n", "\n");
        }

//    protected final void createProgram(String vertex,String fragment){
//        mProgram= uCreateGlProgram(vertex,fragment);
//        mHPosition= GLES20.glGetAttribLocation(mProgram, "vPosition");
//        mHCoord=GLES20.glGetAttribLocation(mProgram,"vCoord");
//        mHMatrix=GLES20.glGetUniformLocation(mProgram,"vMatrix");
//        mHTexture=GLES20.glGetUniformLocation(mProgram,"vTexture");
//    }
    }
