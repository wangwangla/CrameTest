package com.kangwang.crame;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.kangwang.crame.render.CameraRender;

public class MainActivity extends AppCompatActivity {
    CameraRender cameraRender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GLSurfaceView glSurfaceView = findViewById(R.id.surface);
        glSurfaceView.setEGLContextClientVersion(2);
        cameraRender = new CameraRender(this);
        glSurfaceView.setRenderer(cameraRender);
        View viewById = findViewById(R.id.sixbinine);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float scale =9.0F/16;
                cameraRender.changeSize(scale);
            }
        });
        findViewById(R.id.threebifour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float scale = 3.0F/4F;
                cameraRender.changeSize(scale);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraRender.doStopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraRender.doStartPreview();
    }
}