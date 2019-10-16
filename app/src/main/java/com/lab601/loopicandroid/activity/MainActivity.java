package com.lab601.loopicandroid.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.lab601.loopicandroid.R;
import com.lab601.loopicandroid.module.ConfigManager;
import com.lab601.loopicandroid.module.DisplayMenu;
import com.lab601.loopicandroid.module.SourceManager;
import com.lab601.loopicandroid.view.LooViewPager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static com.lab601.loopicandroid.module.SourceManager.PICTURE_PATH;

public class MainActivity extends BaseActivity {
    public double MAX_SIZE = 2000000.0;

    LooViewPager photoView;
    TextView textView;

    int currPage = 100;

    MediaPlayer mediaPlayer;


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




    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean landscape = ConfigManager.getInstance().isLandscape();
        if (landscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.activity_main_landscape);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_main_vertical);
        }



        mediaPlayer = new MediaPlayer();    //音频播放器

        /*初始化view*/
        textView = (TextView) findViewById(R.id.loo_text);

        /*设置文本字体*/
//        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/loo_font1.ttf");  // mContext为上下文
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/HanyiSentyCrayon.ttf");  // mContext为上下文
        textView.setTypeface(typeface);

        textView.setOnClickListener((view) -> {
            int curr = photoView.getCurrentItem();
            photoView.setCurrentItem(curr + 1, true);
        });
        photoView = (LooViewPager) findViewById(R.id.photo_view);
        photoView.setBackgroundColor(Color.BLACK);
        photoView.setAdapter(new LooPagerAdapter());
        int startIndex = ConfigManager.getInstance().getStartIndex();
        if (startIndex > 0) {
            photoView.setCurrentItem(startIndex, false);
        }

        fullScreen();
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


    /**
     * 图片翻页插件适配器
     */
    class LooPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return SourceManager.getInstance().getDisplayMenus().size();
        }


        @Override
        public View instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());

            DisplayMenu displayMenu = SourceManager.getInstance().getDisplayMenus().get(position);
            String fileName = displayMenu.getPicFileName();
            if (fileName.equals("#") || fileName.length() < 1) {   //没有文件，表演黑屏
                ColorDrawable colorDrawable = new ColorDrawable(
                        getResources().getColor(R.color.black));
                photoView.setImageDrawable(colorDrawable);
            } else {   //有文件，表演文件
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(PICTURE_PATH + "/" + fileName);
                } catch (FileNotFoundException e) {
                    Log.d("xingkong", "文件未找到");
                    e.printStackTrace();
                }

                Bitmap bitmap = BitmapFactory.decodeStream(fis);

                //解决过大的bitmap
                int w = bitmap.getWidth();//get width
                int h = bitmap.getHeight();//get height
                long size = w * h * 4;
                if (size > 8000000) {
                    double ratio = Math.sqrt(((double) size) / 8000000.0);
                    System.out.println(ratio);
                    w = (int) ((double) w / ratio);
                    h = (int) ((double) h / ratio);
                    bitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);
                }

                photoView.setImageBitmap(bitmap);
            }

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }

        /**
         * 图片切换监听
         *
         * @param container
         * @param position
         * @param object
         */
        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

            if (currPage != position) {
                onPageChanged(position);
            }
            currPage = position;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }





}
