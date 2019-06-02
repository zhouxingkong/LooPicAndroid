package com.lab601.loopicandroid.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.lab601.loopicandroid.R;
import com.lab601.loopicandroid.bean.OutputDesc;
import com.lab601.loopicandroid.view.LooViewPager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    public static final String ROOT_PATH = "/sdcard/bb/output";
    public static final String PICTURE_PATH = ROOT_PATH + "/imgs";
    LooViewPager photoView;
    TextView textView;

    int totalPages = 0; //总共图片数
    int currPage = 100;

    MediaPlayer mediaPlayer;

    List<OutputDesc> outputDescs;

    public void onPageChanged(int index) {
        Log.d("xingkong", "浏览索引:" + index);


        /*显示文本*/
        String text = outputDescs.get(index).getText();
        if (!text.equals("#")) {
            text = "";
        }
        textView.setText(text);

    }

    /**
     * 读取输入文件
     *
     * @param path
     */
    public void readConfigFile(String path) {
        try {
            FileReader fr = new FileReader(path);
            BufferedReader bf = new BufferedReader(fr);
            String str;

            outputDescs = new ArrayList<>();

            /*第一行:读取源文件路径*/
            while ((str = bf.readLine()) != null) {
                OutputDesc oneDesc = new OutputDesc();
                oneDesc.fromString(str);
                outputDescs.add(oneDesc);
            }

            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*读取输入配置文件*/
        readConfigFile(ROOT_PATH + "/index.txt");

        mediaPlayer = new MediaPlayer();    //音频播放器

        /*初始化view*/
        textView = (TextView) findViewById(R.id.loo_text);
        textView.setOnClickListener((view) -> {
            int curr = photoView.getCurrentItem();
            photoView.setCurrentItem(curr + 1, true);
        });
        photoView = (LooViewPager) findViewById(R.id.photo_view);
        photoView.setBackgroundColor(Color.BLACK);
        photoView.setAdapter(new LooPagerAdapter());
        photoView.setOnClickListener((view) -> {
            Log.d("xinkong", "view点击");
//            int curr = photoView.getCurrentItem();
//            photoView.setCurrentItem(curr + 1, true);
        });
        fullScreen();
    }


    /**
     * 播放指定路径的音频
     *
     * @param path
     */
    public void playSound(String path) {
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("xingkong", "playSound: 播放音频异常", e);
            e.printStackTrace();
        }
        /*播放完成回调函数*/
        mediaPlayer.setOnCompletionListener((mp -> {

        }));
        mediaPlayer.start();
    }

    class LooPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return outputDescs.size();
        }


        @Override
        public View instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());

            OutputDesc outputDesc = outputDescs.get(position);
            String fileName = outputDesc.getPicFileName();
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
