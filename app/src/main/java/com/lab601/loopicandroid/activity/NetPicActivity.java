package com.lab601.loopicandroid.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lab601.loopicandroid.R;
import com.lab601.loopicandroid.module.ConfigManager;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetPicActivity extends BaseActivity {
    public double MAX_SIZE = 2000000.0;

    SimpleDraweeView photoView;
    TextView textView;
    Button changeButton;
    Button preButton;
    String urlPre = "http:/192.168.1.107:8080/loopicserver/show/";

    int currPage = 100;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int startIndex = ConfigManager.getInstance().getStartIndex();
        currPage = startIndex;
        urlPre = "http:/192.168.1." + ConfigManager.getInstance().getUrl() + ":8080/loopicserver/show/";


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

            try {
                String urlStr = "https://www.baidu.com/";
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//设置请求方式为POST
                connection.setDoOutput(true);//允许写出
                connection.setDoInput(true);//允许读入
                connection.setUseCaches(false);//不使用缓存
                connection.connect();//连接
                int responseCode = connection.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }


        });
        photoView = (SimpleDraweeView) findViewById(R.id.photo_view);
//        photoView.setImageUrl(urlPre + currPage);

        fullScreen();
        showPage(currPage);
    }

    public void showPage(int index) {
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
    }

}
