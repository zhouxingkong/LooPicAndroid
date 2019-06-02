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
import com.lab601.loopicandroid.Utils;
import com.lab601.loopicandroid.bean.OutputDesc;
import com.lab601.loopicandroid.view.LooViewPager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    public static final String ROOT_PATH = "/sdcard/bb/output";
    public static final String PICTURE_PATH = ROOT_PATH + "/imgs";
    public static final String SOUND_ROOT = "/sdcard/bb/sounds";

    public static final int WHAT_PLAY_VIDEO = 10;


    LooViewPager photoView;
    TextView textView;

    int totalPages = 0; //总共图片数
    int currPage = 100;

    MediaPlayer mediaPlayer;

    List<OutputDesc> outputDescs;

//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {  //这个是发送过来的消息
//            switch (msg.what) {
//                case WHAT_PLAY_VIDEO: {
//                    playSound((String) msg.obj);
//                    break;
//                }
//            }
//        }
//    };

    /**
     * @param index
     */
    public void onPageChanged(int index) {
        Log.d("xingkong", "浏览索引:" + index);




        /*播放音频*/
        String soundTag = outputDescs.get(index).getSoundTag();
        String soundMode = outputDescs.get(index).getSoundMode();
        //todo: 添加更高级的标签匹配机制
        if (!soundTag.equals("#") && soundTag.length() > 0) {
            soundTag = soundTag.replace("-", "/");
            File soundDir = new File(SOUND_ROOT + "/" + soundTag);
//            Log.d(TAG, "onPageChanged: 音频路径:"+SOUND_ROOT+"/"+soundTag);
            final File[] soundFiles = soundDir.listFiles();
            List<File> files = new ArrayList<File>(Arrays.asList(soundFiles));
            if (files.size() > 1) {
                Utils.shuffle(files);
            }
            if (files.size() > 0) {
                switch (soundMode) {
                    case "1": {
                        playSound(files, 0, 1);
                        break;
                    }
                    case "2": {
                        playSound(files, 0, files.size() + 10);
                        break;
                    }
                    default: {
                        break;
                    }
                }

//                handler.sendMessage(handler.obtainMessage(WHAT_PLAY_VIDEO,files.get(0).getPath()));    //发送初始化信息
            }
        } else {
            mediaPlayer.stop();
        }


//        handler.sendMessage(handler.obtainMessage(WHAT_PLAY_VIDEO,files.get(0).getPath()));    //发送初始化信息
        /*显示文本*/
        String text = outputDescs.get(index).getText();
        if (text.equals("#")) {
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


    /**
     * 播放指定路径的音频
     *
     * @param path
     */
    public void playSound(List<File> path, int index, int totalNum) {
//        Log.d("xingkong", "playSound: 路径:" + path);
        try {
//            File file = new File(path);
//            FileInputStream fis = new FileInputStream(file);
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.stop();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path.get(index).getPath());
            if (totalNum > index + 1) {
                mediaPlayer.setOnCompletionListener((mp -> {
                    Log.d("xingkong", "playSound: 音频播放完毕");
                    playSound(path, (index + 1) % path.size(), totalNum);
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
