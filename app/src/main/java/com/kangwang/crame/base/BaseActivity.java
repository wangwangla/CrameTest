package com.kangwang.crame.base;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kangwang.crame.PermissionUtils;
import com.kangwang.cramelibrary.glsurfaceview.GLCameraView;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected GLCameraView crameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen();
        super.onCreate(savedInstanceState);
        checkPermissions();
    }

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    protected abstract void initListener();

    protected void checkPermissions() {
        String[] permissions = getPermissions();
        if (permissions==null||permissions.length<=0)return;
        PermissionUtils.requestPermissions(this, permissions, 100);
        if (PermissionUtils.permissionsChecking(this, permissions)){
            setContentView(getLayout());
            initView();
            initListener();
        }
    }

    public String[] getPermissions(){
        return null;
    }

    protected abstract void initView();

    protected abstract int getLayout();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            if (PermissionUtils.permissionsChecking(this,getPermissions())){
                setContentView(getLayout());
                initView();
                initListener();
            }
        }
    }
}
