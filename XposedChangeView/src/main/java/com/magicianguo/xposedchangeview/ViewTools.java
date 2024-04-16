package com.magicianguo.xposedchangeview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.provider.Settings;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.jetbrains.annotations.Nullable;

public class ViewTools {
    public static View createView(Activity activity, @Nullable SurfaceView surfaceView, Class<?> clsWebViewActivity) {
        // 获取dpi，便于使用dp为单位的尺寸
        int dpi = activity.getResources().getConfiguration().densityDpi;

        LinearLayout linearLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams linearlayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearlayoutParams.bottomMargin = 20 * dpi / 160;
        linearLayout.setLayoutParams(linearlayoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        // 使用dp为单位
        int padding = 10 * dpi / 160;
        linearLayout.setPadding(padding, padding, padding, padding);
        linearLayout.setBackgroundColor(Color.rgb(0xEB, 0xA8, 0xFF));

        Button btn1 = new Button(activity);
        btn1.setText("打开设置页");
        btn1.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivity(intent);
        });
        linearLayout.addView(btn1);

        Button btn2 = new Button(activity);
        btn2.setText("重新绘制SurfaceView");
        btn2.setAllCaps(false);
        btn2.setOnClickListener(v -> {
            if (surfaceView != null) {
                int width = surfaceView.getMeasuredWidth();
                int height = surfaceView.getMeasuredHeight();
                SurfaceHolder holder = surfaceView.getHolder();
                Canvas canvas = holder.lockCanvas();
                Paint paintBg = new Paint();
                paintBg.setColor(Color.rgb(0x1D, 0xED, 0x99));
                paintBg.setStyle(Paint.Style.FILL);
                canvas.drawRect(new Rect(0, 0, width, height), paintBg);

                // 绘制“A”
                Path path = new Path();
                path.moveTo(width * 0.5F, height * 0.2F);
                path.lineTo(width * 0.3F, height * 0.8F);
                path.moveTo(width * 0.5F, height * 0.2F);
                path.lineTo(width * 0.7F, height * 0.8F);
                path.moveTo(width * 0.4F, height * 0.5F);
                path.lineTo(width * 0.6F, height * 0.5F);
                Paint paintLine = new Paint();
                paintLine.setColor(Color.WHITE);
                paintLine.setStyle(Paint.Style.STROKE);
                paintLine.setStrokeWidth((5 * dpi) / 160F);
                canvas.drawPath(path, paintLine);
                holder.unlockCanvasAndPost(canvas);
            }
        });
        linearLayout.addView(btn2);

        Button btn3 = new Button(activity);
        btn3.setText("打开WebViewActivity");
        btn3.setAllCaps(false);
        btn3.setOnClickListener(v -> {
            Intent intent = new Intent(activity, clsWebViewActivity);
            activity.startActivity(intent);
        });
        linearLayout.addView(btn3);

        return linearLayout;
    }
}
