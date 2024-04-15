package com.magicianguo.xposedtestapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private final Paint mPaintBg = new Paint();
    private final Paint mPaintLine = new Paint();
    private final Path mPath = new Path();

    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintBg.setColor(Color.rgb(0x33, 0x88, 0xFF));
        mPaintBg.setStyle(Paint.Style.FILL);
        mPaintLine.setColor(Color.WHITE);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(getResources().getDimension(R.dimen.sv_line_width));
        getHolder().addCallback(this);
        // 禁止截屏
        setSecure(true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Canvas canvas = holder.lockCanvas();
        // 绘制整个背景
        canvas.drawRect(new Rect(0, 0, width, height), mPaintBg);
        // 绘制“Z”
        mPath.reset();
        mPath.moveTo(width * 0.25F, width * 0.25F);
        mPath.lineTo(width * 0.75F, width * 0.25F);
        mPath.lineTo(width * 0.25F, width * 0.75F);
        mPath.lineTo(width * 0.75F, width * 0.75F);
        canvas.drawPath(mPath, mPaintLine);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}