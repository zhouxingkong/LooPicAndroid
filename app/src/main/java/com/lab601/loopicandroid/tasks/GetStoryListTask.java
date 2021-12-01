package com.lab601.loopicandroid.tasks;

import android.os.AsyncTask;

import com.lab601.loopicandroid.module.ConfigManager;
import com.lab601.loopicandroid.module.json.JacksonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


/**
 * 显示章节列表
 */
public class GetStoryListTask extends AsyncTask {
    private String getChapterUrl = "http:/192.168.43.139:8080/text/chapterList";

    public void showChapterList(List<String> data) {

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        getChapterUrl = "http:/" + ConfigManager.getInstance().getUrl() + ":8080/text/chapterList";

        try {
            URL url = new URL(getChapterUrl);
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
//                    ConfigManager.getInstance().setText(object);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
//        String[] data = {"暹罗猫", "布偶猫", "折耳猫", "短毛猫", "波斯猫", "蓝猫", "森林猫", "孟买猫","缅因猫","埃及猫","伯曼猫","缅甸猫","新加坡猫","美国短尾猫","巴厘猫"};
        showChapterList((List) o);
    }
}
