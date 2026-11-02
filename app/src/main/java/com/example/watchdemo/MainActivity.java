package com.example.watchdemo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 第一阶段的最小可运行页面：不依赖 AndroidX / Kotlin / Compose，
 * 目的是先验证「云端编译 → 装表 → 启动 → 看日志」这条链路。
 * 圆屏安全区：内容全部居中，左右留 12dp，避免被圆角裁掉。
 */
public class MainActivity extends Activity {

    private int count = 0;
    private TextView counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final float density = getResources().getDisplayMetrics().density;
        final int pad = (int) (12 * density);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(this);
        title.setText("Hello Watch 5");
        title.setTextColor(Color.WHITE);
        title.setTextSize(20f);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView info = new TextView(this);
        info.setText(Build.MANUFACTURER + " " + Build.MODEL + "\nAPI " + Build.VERSION.SDK_INT
                + " / Android " + Build.VERSION.RELEASE);
        info.setTextColor(Color.LTGRAY);
        info.setTextSize(11f);
        info.setGravity(Gravity.CENTER);
        info.setPadding(0, (int) (6 * density), 0, (int) (6 * density));
        root.addView(info);

        counter = new TextView(this);
        counter.setText("点击 0 次");
        counter.setTextColor(Color.WHITE);
        counter.setTextSize(14f);
        counter.setGravity(Gravity.CENTER);
        root.addView(counter);

        Button button = new Button(this);
        button.setText("点我");
        button.setOnClickListener(v -> {
            count++;
            counter.setText("点击 " + count + " 次");
        });
        root.addView(button);

        setContentView(root);
    }
}
