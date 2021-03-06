package com.lab601.loopicandroid.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lab601.loopicandroid.R;
import com.lab601.loopicandroid.module.ConfigManager;
import com.lab601.loopicandroid.module.DisplayMenu;
import com.lab601.loopicandroid.module.SourceManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.lab601.loopicandroid.module.SourceManager.PICTURE_PATH;

public class MainActivity extends BaseActivity {
    SimpleDraweeView photoView;
    TextView textView;
    int currPage = 100;
    MediaPlayer mediaPlayer;
    Button preButton;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int startIndex = ConfigManager.getInstance().getStartIndex();
        currPage = startIndex;

        boolean landscape = ConfigManager.getInstance().isLandscape();
        if (landscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.activity_netpic_landscape);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_netpic_vertical);
        }



        mediaPlayer = new MediaPlayer();    //音频播放器

        /*初始化view*/
        textView = (TextView) findViewById(R.id.loo_text);

        /*设置文本字体*/
//        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/loo_font1.ttf");  // mContext为上下文
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/HanyiSentyCrayon.ttf");  // mContext为上下文
        textView.setTypeface(typeface);

        textView.setOnClickListener((view) -> {     //下一页
            currPage++;
            showPage(currPage);
        });

        preButton = (Button) findViewById(R.id.pre_pic);
        preButton.setOnClickListener((view) -> {     //下一页
            if (currPage > 0) {
                currPage--;
                showPage(currPage);
            }
        });

        photoView = (SimpleDraweeView) findViewById(R.id.photo_view);
        photoView.setBackgroundColor(Color.BLACK);


        fullScreen();
        showPage(currPage);
    }

    /**
     * 播放音频
     * @param path
     * @param index 播放到第几个了
     */
    public void playSound(List<File> path, int index) {
//        Log.d("xingkong", "playSound: 路径:" + path);
        if (path == null || path.size() < 1 || path.size() < index + 1) {
            return;
        }
        try {
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path.get(index).getPath());
            if (path.size() > index + 1) {
                /*播放完成回调函数*/
                mediaPlayer.setOnCompletionListener((mp -> {
                    Log.d("xingkong", "playSound: 音频播放完毕");
                    playSound(path, index + 1);
                }));
            }

            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("xingkong", "playSound: 播放音频异常", e);
            e.printStackTrace();
        }

    }

    public void showPage(int index) {
        DisplayMenu displayMenu = SourceManager.getInstance().getDisplayMenus().get(index);
        String fileName = displayMenu.getPicFileName();
        if (fileName.equals("#") || fileName.length() < 1) {   //没有文件，表演黑屏
            ColorDrawable colorDrawable = new ColorDrawable(
                    getResources().getColor(R.color.black));
            photoView.setImageDrawable(colorDrawable);
        } else {   //有文件，表演文件

            Uri uri = Uri.parse("file://" + PICTURE_PATH + "/" + fileName);
            photoView.setImageURI(uri);
            onPageChanged(index);
        }
    }

    /**
     * @param index
     */
    public void onPageChanged(int index) {
        Log.d("xingkong", "浏览索引:" + index);


        if (ConfigManager.getInstance().isSound()) {
            DisplayMenu displayMenu = SourceManager.getInstance().getDisplayMenus().get(index);
            List<File> soundFiles = displayMenu.getSoundList();
            if (soundFiles != null && soundFiles.size() > 0) {
                playSound(soundFiles, 0);
            } else {
                mediaPlayer.stop();
            }
        }

        /*显示文本*/
        String text = SourceManager.getInstance().getDisplayMenus().get(index).getText();
        text = text.replace("{", "<font color='#ff0000'>");
        text = text.replace("}", "</font>");
        if (text.equals("#")) {
            text = "";
        }
        textView.setText(Html.fromHtml(text, 0));
    }


}
