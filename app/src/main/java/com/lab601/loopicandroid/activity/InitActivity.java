package com.lab601.loopicandroid.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.lab601.loopicandroid.R;
import com.lab601.loopicandroid.listener.PermissionListener;
import com.lab601.loopicandroid.module.ConfigManager;
import com.lab601.loopicandroid.module.InitialCallback;
import com.lab601.loopicandroid.module.SourceManager;
import com.lab601.loopicandroid.tasks.GetTextTask;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.lab601.loopicandroid.module.SourceManager.ROOT_PATH;

public class InitActivity extends BaseActivity {

    private static final int WHAT_INIT_SUCCESS = 10;
    private static final int WHAT_INIT_FAIL = 11;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION"};
    Button startButton;
    Button startNetPicButton;
    TextView statTextView;

    Button landscapeButton;
    Button soundButton;
    Button clearCacheButton;
    EditText initPosEdit;
    EditText ipEdit;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_INIT_SUCCESS: {
                    statTextView.setText("初始化成功");
                    break;
                }
                case WHAT_INIT_FAIL: {
                    statTextView.setText("初始化失败");
                    break;
                }
                case MESSAGE_CLEAN: {
                    doClean();
                    Toast.makeText(getBaseContext(), "清理完成", Toast.LENGTH_SHORT).show();
                    break;
                }
                default:
                    break;
            }
        }

    };

    public void doClean() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
        ConfigManager.preloadMap.clear();

        preloadImage(0);
        preloadImage(1);
        preloadImage(2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        GetTextTask getTextTask = new GetTextTask(handler);
        getTextTask.start();

        statTextView = (TextView) findViewById(R.id.init_stat);
        startButton = (Button) findViewById(R.id.start_loo);
        startButton.setOnClickListener((view) -> {
            int startPos = 0;
            try {
                String startPosStr = initPosEdit.getText().toString();
                startPos = Integer.parseInt(startPosStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            ConfigManager.getInstance().setStartIndex(startPos);

            Intent intent = new Intent();
            intent.setClass(InitActivity.this, MainActivity.class);
            startActivity(intent);
        });

        startNetPicButton = (Button) findViewById(R.id.start_net_loo);
        startNetPicButton.setOnClickListener((view) -> {
            int startPos = 0;
            String ip = "";
            try {
                String startPosStr = initPosEdit.getText().toString();
                startPos = Integer.parseInt(startPosStr);
                ip = ipEdit.getText().toString();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            ConfigManager.getInstance().setStartIndex(startPos);
            if (ip.length() > 5) {
                ConfigManager.getInstance().setUrl(ip);
            }

            Intent intent = new Intent();
            intent.setClass(InitActivity.this, NetPicActivity.class);
            startActivity(intent);
        });

        initPosEdit = (EditText) findViewById(R.id.start_index);
        ipEdit = (EditText) findViewById(R.id.server_ip);

        soundButton = (Button) findViewById(R.id.sound_on);
        landscapeButton = (Button) findViewById(R.id.landscape_on);
        clearCacheButton = (Button) findViewById(R.id.clear_cache);


        clearCacheButton.setOnClickListener(v -> {   //静音按钮
            Thread netThread = new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(urlClear);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");//设置请求方式为POST
                        connection.connect();//连接
                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            handler.sendEmptyMessage(MESSAGE_CLEAN);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            netThread.start();

        });

        soundButton.setOnClickListener(v -> {   //静音按钮
            if (ConfigManager.getInstance().isSound()) {
                soundButton.setText("声音:关闭");
                ConfigManager.getInstance().setSound(false);
            } else {
                soundButton.setText("声音:开启");
                ConfigManager.getInstance().setSound(true);
            }
        });

        landscapeButton.setOnClickListener(v -> {   //设置横竖屏
            if (ConfigManager.getInstance().isLandscape()) {
                landscapeButton.setText("竖屏显示");
                ConfigManager.getInstance().setLandscape(false);
            } else {
                landscapeButton.setText("横屏显示");
                ConfigManager.getInstance().setLandscape(true);
            }
        });



        /*设置回调*/
        SourceManager.getInstance().setInitialCallback(new InitialCallback() {
            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(WHAT_INIT_SUCCESS);
            }

            @Override
            public void onFail() {
                handler.sendEmptyMessage(WHAT_INIT_FAIL);
            }
        });

        /*申请权限*/
        if (Build.VERSION.SDK_INT >= 23) {//判断当前系统是不是Android6.0
            requestRuntimePermissions(PERMISSIONS_STORAGE, new PermissionListener() {
                @Override
                public void granted() {
                    //权限申请通过
                    Log.d("xingkong", "denied:权限通过", null);
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            SourceManager.getInstance().readConfigFile(ROOT_PATH + "/index.txt");
                        }
                    };
                    thread.start();
                }

                @Override
                public void denied(List<String> deniedList) {
                    //权限申请未通过
                    for (String denied : deniedList) {
                        if (denied.equals("android.permission.ACCESS_FINE_LOCATION")) {
                            Log.e("xingkong", "denied:申请权限失败", null);
                            ;
//                            CustomToast.INSTANCE.showToast(SDK_WebApp.this, "定位失败，请检查是否打开定位权限！");
                        } else {
                            Log.e("xingkong", "denied:申请权限失败", null);
//                            CustomToast.INSTANCE.showToast(SDK_WebApp.this, "没有文件读写权限,请检        查是否打开！");
                        }
                    }
                }
            });
        }


    }

}
