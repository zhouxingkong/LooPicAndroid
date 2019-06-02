package com.lab601.loopicandroid;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends BaseActivity {
    LooViewPager photoView;

    int totalPages = 0; //总共图片数
    int currPage = 100;
    File[] picFiles;

    MediaPlayer mediaPlayer;

    public void onPageChanged(int index) {
        Log.d("xingkong", "浏览索引:" + index);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**/
        String pictureRootPath = "/sdcard/PA/pics";
        File rootFile = new File(pictureRootPath);
        picFiles = rootFile.listFiles();
        totalPages = picFiles.length;

        mediaPlayer = new MediaPlayer();

        /*初始化view*/
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

    class LooPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return totalPages;
        }


        @Override
        public View instantiateItem(ViewGroup container, int position) {

            PhotoView photoView = new PhotoView(container.getContext());
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(picFiles[position]);
            } catch (FileNotFoundException e) {
                Log.d("xingkong", "文件未找到");
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            photoView.setImageBitmap(bitmap);
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
