package com.kangwang.crame.base;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.kangwang.cramelibrary.glsurfaceview.GLCameraView;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected GLCameraView crameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen();
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        checkPermissions();
        initView();
        initListener();
    }

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    protected abstract void initListener();

    protected abstract void checkPermissions();

    protected abstract void initView();

    protected abstract int getLayout();
}
