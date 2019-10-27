package com.lab601.loopicandroid.view.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebImage implements SmartImage {
    private static final int CONNECT_TIMEOUT = 5000000;
    private static final int READ_TIMEOUT = 10000000;
    private static WebImageCache webImageCache;
    public double MAX_SIZE = 2000000.0;
    private String url;

    public WebImage(String url) {
        this.url = url;
    }

    public static void removeFromCache(String url) {
        if (webImageCache != null) {
            webImageCache.remove(url);
        }
    }

    public Bitmap getBitmap(Context context) {
        // Don't leak context
//        if(webImageCache == null) {
//            webImageCache = new WebImageCache(context);
//        }

        // Try getting bitmap from cache first
        Bitmap bitmap = null;
        if (url != null) {
//            bitmap = webImageCache.get(url);
//            if(bitmap == null) {
            bitmap = getBitmapFromUrl(url);
//                if(bitmap != null){
////                    webImageCache.put(url, bitmap);
//                }
//            }
        }

        return bitmap;
    }

    private Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;

        try {
//            URLConnection conn = new URL(url).openConnection();
//            conn.setConnectTimeout(CONNECT_TIMEOUT);
//            conn.setReadTimeout(READ_TIMEOUT);

            URL imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();

            bitmap = BitmapFactory.decodeStream(is);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
