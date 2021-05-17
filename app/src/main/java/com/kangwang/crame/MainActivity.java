package com.kangwang.crame;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kangwang.cramelibrary.CameraUtils;
import com.kangwang.cramelibrary.FileUtils;
import com.kangwang.cramelibrary.FilteredBitmapCallback;
import com.kangwang.cramelibrary.GLCameraView;
import com.kangwang.cramelibrary.ImageCallback;
import com.kangwang.cramelibrary.method1.CrameView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    GLCameraView crameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        crameView = findViewById(R.id.surface);
        checkPermissions();
        if (PermissionUtils.permissionsChecking(
                this,
                new String[] {
                        Manifest.permission.CAMERA
                })) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkPermissions() {
        PermissionUtils.requestPermissions(this, new String[] {
                Manifest.permission.CAMERA,
        }, 0);
    }

//    public void picture(View view) {
//        crameView.takePicture(new ImageCallback(){
//            @Override
//            public void onData(byte[] data,kkk kkk) {
//                byte[] temp = new byte[data.length];
//                for (int i = 0; i < data.length; i++) {
//                    temp[i] = data[i];
//                }
//                kkk.run1();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //创建路径
//                        File newfile = FileUtils.createImageFile();
//                        FileOutputStream fos;
//                        try {
//                            fos = new FileOutputStream(newfile);
//                            fos.write(temp);
//                            fos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        //如果直接保存，你会发现照片是旋转了的。所以需要做处理
//                        rotateImageView(CameraUtils.cameraId, CameraUtils.orientation, newfile.getAbsolutePath());
//
//                    }
//                }).start();
//            }
//        });
//    }
    /**
     * 旋转图片
     *
     * @param cameraId    前置还是后置
     * @param orientation 拍照时传感器方向
     * @param path        图片路径
     */
    private void rotateImageView(int cameraId, int orientation, String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Matrix matrix = new Matrix();
        //0是后置
        if (cameraId == 0) {
            if (orientation == 90) {
                matrix.postRotate(90);
            }
        }
        //1是前置
        if (cameraId == 1) {
            //顺时针旋转270度
            matrix.postRotate(270);
        }
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        File file = new File(path);
        //重新写入文件
        try {
            // 写入文件
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            //默认jpg
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            resizedBitmap.recycle();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void picture(View view) {
        crameView.takePicture(new FilteredBitmapCallback(){
            @Override
            public void onData(Bitmap bitmap) {
//                File file = FileUtils.createImageFile();
//                //重新写入文件
//                try {
//                    // 写入文件
//                    FileOutputStream fos;
//                    fos = new FileOutputStream(file);
//                    //默认jpg
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                    fos.flush();
//                    fos.close();
//                    bitmap.recycle();
//
//                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                            Uri.fromFile(file)));
//
////                    Toast.makeText(GLCameraActivity.this, "finished", Toast.LENGTH_SHORT).show();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    public void switchCame(View view) {
        crameView.switchCame();
    }


    public void style1(View view) {
        crameView.changeStyle1();
    }

    public void style2(View view) {

    }

    public void style3(View view) {
    }

    public void style4(View view) {
    }

    public void style5(View view) {
    }

    public void style6(View view) {
    }
}