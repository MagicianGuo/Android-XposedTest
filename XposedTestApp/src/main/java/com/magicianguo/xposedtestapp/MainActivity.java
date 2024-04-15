package com.magicianguo.xposedtestapp;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView mTvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setText("你好，世界");
        // 窗口禁止截屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

}