package com.kangwang.cramelibrary.crame;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CameraFocusView extends AppCompatImageView {
    String TAG = getClass().getName();
    //显示的圆宽显示的位置
    public float currnetX = 40;
    public float currentY = 50;

    Paint paint;
    //半径大小
    float radis = 100;
    //透明度
    int alpha = 255;
    boolean isAlpha = false;
    private ValueAnimator scaleAnimator,colorAnimator;

    public CameraFocusView(Context context) {
        super(context);
        init();
    }

    public CameraFocusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraFocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        scaleAnimator = new ValueAnimator();
        scaleAnimator.setFloatValues(80, 70, 90);
        scaleAnimator.setDuration(500);
        scaleAnimator.setInterpolator(new LinearInterpolator());
        //伸缩动画效果
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radis = (float) animation.getAnimatedValue();
                invalidate();
                if (radis == 90){
                    isAlpha = true;
                    colorAnimator.start();
                }
            }
        });

        colorAnimator = new ValueAnimator();
        colorAnimator.setIntValues(255,0);
        colorAnimator.setDuration(3000);
        colorAnimator.setInterpolator(new LinearInterpolator());
        //显示透明度动画效果
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        //初始化不显示
        setVisibility(GONE);
    }

    //开始现在圆框
    public void startAnimator() {
        isAlpha = false;
        if (scaleAnimator != null) {
            Log.e(TAG, "startAnimator: scaleAnimator != null");
            setVisibility(VISIBLE);
            scaleAnimator.start();
        } else {
            Log.e(TAG, "startAnimator: scaleAnimator == null");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画笔设置
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);//画笔属性是空心圆
        paint.setStrokeWidth(2);//设置画笔粗细
        // 抗锯齿
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setAlpha(255);
        if (isAlpha){
            if (alpha > 0 && alpha < 155){
                paint.setAlpha(155);
            }else if (alpha == 0){
                setVisibility(GONE);
            }
        }
        canvas.drawCircle(currnetX, currentY, radis, paint);

    }
}