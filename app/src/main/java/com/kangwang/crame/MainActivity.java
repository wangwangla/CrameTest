package com.kangwang.crame;

import androidx.annotation.RequiresApi;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.kangwang.crame.base.BaseActivity;
import com.kangwang.cramelibrary.FilteredBitmapCallback;
import com.kangwang.cramelibrary.glsurfaceview.GLCameraView;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends BaseActivity {
    private TextView textView1;
    private Button openType;
    private Button fanzhuan;

    @Override
    protected void initView() {

        crameView = findViewById(R.id.surface);
        openType = findViewById(R.id.openType);
//        fanzhuan = findViewById(R.id.fanzhuan);
    }

    @Override
    public String[] getPermissions() {
        return new String[]{
                Manifest.permission.CAMERA
        };
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initListener() {
        openType.setOnClickListener(this::onClick);
//        fanzhuan.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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


    public void picture(View view) {
        crameView.takePicture(new FilteredBitmapCallback(){
            @Override
            public void onData(Bitmap bitmap) {
            }
        });
    }

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
}