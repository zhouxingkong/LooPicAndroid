package com.lab601.loopicandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    LooViewPager photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photoView = (LooViewPager) findViewById(R.id.photo_view);
        photoView.setBackgroundColor(R.color.black);
        photoView.setAdapter(new LooPagerAdapter());
        photoView.setOnClickListener((view) -> {
            int curr = photoView.getCurrentItem();
            photoView.setCurrentItem(curr + 1, true);
        });
    }

    class LooPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
//            photoView.setImageResource(sDrawables[position]);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream("/sdcard/PA/001.jpg");
            } catch (FileNotFoundException e) {
                Log.d("xingkong", "文件未找到");
                e.printStackTrace();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(fis);
//            Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),bitmap , null,null));
            photoView.setImageBitmap(bitmap);
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
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
