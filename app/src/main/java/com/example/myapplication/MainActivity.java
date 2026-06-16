package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout row1;
    private LinearLayout row2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        row1 = findViewById(R.id.row1);
        row2 = findViewById(R.id.row2);

        populateRow1();
        populateRow2();
    }

    private void populateRow1() {
        List<AppModel> apps = getSquareApps();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (AppModel app : apps) {
            View itemView = inflater.inflate(R.layout.item_square_app, row1, false);
            ImageView ivIcon = itemView.findViewById(R.id.ivIcon);

            ivIcon.setImageBitmap(getReflectionPic(app.getIconResId()));

            row1.addView(itemView);
        }
    }

    private void populateRow2() {
        List<AppModel> apps = getRectApps();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (AppModel app : apps) {
            View itemView = inflater.inflate(R.layout.item_rect_app, row2, false);
            ImageView ivIcon = itemView.findViewById(R.id.ivIcon);

            ivIcon.setImageResource(app.getIconResId());

            setupFocusAndClick(itemView);
            row2.addView(itemView);
        }
    }

    private void setupFocusAndClick(View itemView) {
        itemView.setOnFocusChangeListener((v, hasFocus) -> {
            float scale = hasFocus ? 1.1f : 1.0f;
            v.animate().scaleX(scale).scaleY(scale).setDuration(150).start();
        });

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AppListActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 生成带倒影的图片
     * @param imgId 原图资源id
     * @return 原图+倒影的合成Bitmap
     */
    private Bitmap getReflectionPic(int imgId) {
        // 读取原图
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgId);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 原图与倒影间距
        int space = 2;

        // 取原图下半部分，垂直翻转作为倒影
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        Bitmap reflectionPic = Bitmap.createBitmap(bitmap, 0, height * 4 / 5, width, height / 6, matrix, false);

        // 创建合成画布：宽度=原图宽，高度=原图高 + 半高倒影 + 渐变空间
        Bitmap result = Bitmap.createBitmap(width, height + height / 7, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        // 画原图
        canvas.drawBitmap(bitmap, 0, 0, null);
        // 画倒影（在原图下方，间距space）
        canvas.drawBitmap(reflectionPic, 0, height + space, null);

        // 倒影渐变遮罩
        Paint paint = new Paint();
        LinearGradient linearGradient = new LinearGradient(0, height + space, 0,
                result.getHeight(), Color.BLACK, Color.TRANSPARENT, Shader.TileMode.MIRROR);
        paint.setShader(linearGradient);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, height, width, result.getHeight() + space, paint);

        return result;
    }

    private List<AppModel> getSquareApps() {
        List<AppModel> list = new ArrayList<>();
        list.add(new AppModel("NETFLIX", R.drawable.netflix, AppModel.Type.SQUARE));
        list.add(new AppModel("YouTube", R.drawable.youtube, AppModel.Type.SQUARE));
        list.add(new AppModel("Google Play", R.drawable.google_play, AppModel.Type.SQUARE));
        list.add(new AppModel("Chrome", R.drawable.chrome, AppModel.Type.SQUARE));
        return list;
    }

    private List<AppModel> getRectApps() {
        List<AppModel> list = new ArrayList<>();
        list.add(new AppModel("Keystone", R.drawable.keystone, AppModel.Type.RECT));
        list.add(new AppModel("Miracast", R.drawable.miracast, AppModel.Type.RECT));
        list.add(new AppModel("Signal Source", R.drawable.signal_source, AppModel.Type.RECT));
        list.add(new AppModel("My Apps", R.drawable.my_apps, AppModel.Type.RECT));
        list.add(new AppModel("Settings", R.drawable.settings, AppModel.Type.RECT));
        return list;
    }
}
