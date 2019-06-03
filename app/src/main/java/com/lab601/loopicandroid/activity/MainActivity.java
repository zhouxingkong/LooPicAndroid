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



//    public static final int WHAT_PLAY_VIDEO = 10;


    LooViewPager photoView;
    TextView textView;

    int currPage = 100;

    MediaPlayer mediaPlayer;




    /**
     * @param index
     */
    public void onPageChanged(int index) {
        Log.d("xingkong", "浏览索引:" + index);

        DisplayMenu displayMenu = SourceManager.getInstance().getDisplayMenus().get(index);
        List<File> soundFiles = displayMenu.getSoundList();
        if (soundFiles != null && soundFiles.size() > 0) {
            playSound(soundFiles, 0);
        } else {
            mediaPlayer.stop();
        }

//        handler.sendMessage(handler.obtainMessage(WHAT_PLAY_VIDEO,files.get(0).getPath()));    //发送初始化信息
        /*显示文本*/
        String text = SourceManager.getInstance().getDisplayMenus().get(index).getText();
        if (text.equals("#")) {
            text = "";
        }
        textView.setText(text);


    }




    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*读取输入配置文件*/
//        readConfigFile(ROOT_PATH + "/index.txt");

        mediaPlayer = new MediaPlayer();    //音频播放器
//        mediaPlayer = MediaPlayer.create(this,R.raw.sound);

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
//    @Override
//    public void onResume() {
//
//        super.onResume();
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//    }


    /**
     * 播放指定路径的音频
     *
     * @param path
     */
    public void playSound(List<File> path, int index) {
//        Log.d("xingkong", "playSound: 路径:" + path);
        try {
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path.get(index).getPath());
            if (path.size() > index + 1) {
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
//        /*播放完成回调函数*/


    }



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
