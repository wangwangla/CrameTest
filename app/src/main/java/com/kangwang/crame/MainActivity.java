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

import com.kangwang.cramelibrary.FilteredBitmapCallback;
import com.kangwang.cramelibrary.glsurfaceview.GLCameraView;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends BaseActivity{

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;
    private TextView textView7;
    private Button openType;
    private Button fanzhuan;

    @Override
    protected void initView() {
        crameView = findViewById(R.id.surface);
        openType = findViewById(R.id.openType);
        textView1 = findViewById(R.id.lvjing_1);
        textView2 = findViewById(R.id.lvjing_2);
        textView3 = findViewById(R.id.lvjing_3);
        textView4 = findViewById(R.id.lvjing_4);
        textView5 = findViewById(R.id.lvjing_5);
        textView6 = findViewById(R.id.lvjing_6);
        textView7 = findViewById(R.id.lvjing_7);
        fanzhuan = findViewById(R.id.fanzhuan);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @RequiresApi(api = 30)
    public void openType() {
        View viewById = findViewById(R.id.beat_type);
        int width = getDisplay().getWidth();
        int screenWidth = width;//获取屏幕宽度
        if (viewById.getVisibility() == View.GONE){
            viewById.setVisibility(View.VISIBLE);

            Animation translateAnimation = new TranslateAnimation(screenWidth - 50,0, 0, 0);//设置平移的起点和终点
            translateAnimation.setDuration(100);//动画持续的时间为10s
            translateAnimation.setFillEnabled(true);//使其可以填充效果从而不回到原地
            translateAnimation.setFillAfter(true);//不回到起始位置
//如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
            viewById.setAnimation(translateAnimation);//给imageView添加的动画效果
            translateAnimation.startNow();//动画开始执行 放在最后即可
        }else {
            Animation translateAnimation = new TranslateAnimation(0,screenWidth, 0, 0);//设置平移的起点和终点
            translateAnimation.setDuration(100);//动画持续的时间为10s
            translateAnimation.setFillEnabled(true);//使其可以填充效果从而不回到原地
            translateAnimation.setFillAfter(true);//不回到起始位置
//如果不添加setFillEnabled和setFillAfter则动画执行结束后会自动回到远点
            viewById.setAnimation(translateAnimation);//给imageView添加的动画效果
            translateAnimation.startNow();//动画开始执行 放在最后即可
            viewById.setVisibility(View.GONE);
        }
    }

    public void type(int index){

    }

    @Override
    protected void initListener() {
        openType.setOnClickListener(this::onClick);
        textView1.setOnClickListener(this::onClick);
        textView2.setOnClickListener(this::onClick);
        textView3.setOnClickListener(this::onClick);
        textView4.setOnClickListener(this::onClick);
        textView5.setOnClickListener(this::onClick);
        textView6.setOnClickListener(this::onClick);
        textView7.setOnClickListener(this::onClick);
        fanzhuan.setOnClickListener(this::onClick);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.openType:
                openType();
                break;
            case R.id.beat_type:
                break;
            case R.id.type_1:
                type(1);
                break;
            case R.id.type_2:
                type(2);
                break;
            case R.id.type_3:
                type(3);
                break;
            case R.id.type_4:
                type(4);
                break;
            case R.id.type_5:
                type(5);
                break;
            case R.id.type_6:
                type(6);
                break;
            case R.id.lvjing_1:
                crameView.changeStyle1(1);
                break;
            case R.id.lvjing_2:
                crameView.changeStyle1(2);
                break;
            case R.id.lvjing_3:
                crameView.changeStyle1(3);
                break;
            case R.id.lvjing_4:
                crameView.changeStyle1(4);
                break;
            case R.id.lvjing_5:
                crameView.changeStyle1(1);
                break;
            case R.id.lvjing_6:
                crameView.changeStyle1(1);
                break;
            case R.id.lvjing_7:
                crameView.changeStyle1(1);
                break;
            case R.id.fanzhuan:
                crameView.switchCame();
                break;

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

    @Override
    protected void checkPermissions() {
        PermissionUtils.requestPermissions(this, new String[] {
                Manifest.permission.CAMERA,
        }, 0);
        if (PermissionUtils.permissionsChecking(this, new String[] {Manifest.permission.CAMERA})){}
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
//

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