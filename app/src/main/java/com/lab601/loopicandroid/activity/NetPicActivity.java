package com.lab601.loopicandroid.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lab601.loopicandroid.R;
import com.lab601.loopicandroid.module.ConfigManager;
import com.lab601.loopicandroid.module.DisplayMenu;
import com.lab601.loopicandroid.module.SourceManager;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NetPicActivity extends BaseActivity {
    public double MAX_SIZE = 2000000.0;

    SimpleDraweeView photoView;
    TextView textView;
    Button changeButton;
    Button preButton;

    int currPage = 100;
    MediaPlayer mediaPlayer;

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
            setContentView(R.layout.activity_main_vertical);
        }

        /*初始化view*/
        textView = (TextView) findViewById(R.id.loo_text);
        /*设置文本字体*/
//        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/loo_font1.ttf");  // mContext为上下文
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/HanyiSentyCrayon.ttf");  // mContext为上下文
        textView.setTypeface(typeface);
        textView.setOnClickListener((view) -> { //下一张图
            currPage++;
            showPage(currPage);
        });
        preButton = (Button) findViewById(R.id.pre_pic);
        preButton.setOnClickListener((view) -> {   //上一张图
            if (currPage > 0) {
                currPage--;
                showPage(currPage);
            }

        });

        changeButton = (Button) findViewById(R.id.change_pic);
        changeButton.setOnClickListener((view) -> {
            Thread netThread = new Thread() {
                @Override
                public void run() {
                    try {
                        String urlStr = urlChange + currPage;
                        URL url = new URL(urlStr);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");//设置请求方式为POST
                        connection.connect();//连接
                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            handler.sendEmptyMessage(MESSAGE_SHOW_PIC);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            netThread.start();

        });
        photoView = (SimpleDraweeView) findViewById(R.id.photo_view);

        fullScreen();
        showPage(currPage);
    }

    @Override
    public void showCurrPage() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//        imagePipeline.clearCaches();
        Uri uri = Uri.parse(urlPre + currPage);
        imagePipeline.evictFromCache(uri);
        showPage(currPage);
    }

    public void showPage(int index) {
        /*渐进加载图片，然而并没有什么卵用*/
        Uri uri = Uri.parse(urlPre + index);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(photoView.getController())
                .build();
        photoView.setController(controller);
//        photoView.setImageURI(uri);
        onPageChanged(index);

        //预加载
        preloadImage(currPage + 1);
        preloadImage(currPage + 2);
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
//        String text = SourceManager.getInstance().getDisplayMenus().get(index).getText();
        List<String> textList = ConfigManager.getInstance().getText();
        if (textList != null && textList.size() > currPage) {
            String text = decode(textList.get(currPage));
            text = text.replace("{", "<font color='#ff0000'>");
            text = text.replace("}", "</font>");
            if (text.equals("#")) {
                text = "";
            }
            textView.setText(Html.fromHtml(text, 0));
        } else {
            textView.setText(Html.fromHtml("", 0));
        }


    }

    /**
     * 客户端解密
     *
     * @param in
     * @return
     */
    public String decode(String in) {
        byte[] byteArray = in.getBytes();
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] -= 1;
        }
        return new String(byteArray);
    }

    /**
     * 播放音频
     *
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

}
