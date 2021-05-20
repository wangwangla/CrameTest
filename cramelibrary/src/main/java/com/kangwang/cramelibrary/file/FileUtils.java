package com.kangwang.cramelibrary.file;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtils {


    public static File createVideoFile() {

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "glcamera");

        if (!dir.exists()) {
            boolean isSuccess = dir.mkdirs();
            Log.i("glcamera", "文件夹创建状态--->" + isSuccess);
        }


        return new File(dir.getPath() + File.separator  + System.currentTimeMillis() + ".mp4");

    }


    public static File createSystemVideoFile(Context c){

        File dir = c.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return new File(dir,System.currentTimeMillis() + ".mp4");


    }

    public static File createImageFile() {

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "glcamera");

        if (!dir.exists()) {
            boolean isSuccess = dir.mkdirs();
            Log.i("glcamera", "文件夹创建状态--->" + isSuccess);
        }
        File file = new File(dir.getPath() + File.separator + "img_" + System.currentTimeMillis() + ".jpg");
        return file;
    }

    public static String createImageFileName() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "glcamera");
        if (!dir.exists()) {
            boolean isSuccess = dir.mkdirs();
            Log.i("glcamera", "文件夹创建状态--->" + isSuccess);
        }
        return dir.getPath() + File.separator + "img_" + System.currentTimeMillis() + ".jpg";
    }


}
