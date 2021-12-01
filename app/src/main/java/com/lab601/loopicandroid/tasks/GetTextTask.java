package com.lab601.loopicandroid.tasks;

import android.os.Handler;

import com.lab601.loopicandroid.module.ConfigManager;
import com.lab601.loopicandroid.module.json.JacksonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class GetTextTask extends Thread {
    private String getTextUrl = "http:/192.168.43.139:8080/text/xingkong1313113";
    private Handler handler;
    private int storyId;

    public GetTextTask(Handler handler,int storyId) {
        this.handler = handler;
        this.storyId = storyId;
    }

    @Override
    public void run() {
        getTextUrl = "http:/" + ConfigManager.getInstance().getUrl() + ":8080/text/"+storyId;

        try {
            URL url = new URL(getTextUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");//设置请求方式为POST
            connection.connect();//连接
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
//                handler.sendEmptyMessage(MESSAGE_SHOW_PIC);
                //得到响应流
                InputStream inputStream = connection.getInputStream();
                List object = null;
                try {
                    object = JacksonMapper.getInstance().readValue(inputStream, List.class);
                    ConfigManager.getInstance().setText(object);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
