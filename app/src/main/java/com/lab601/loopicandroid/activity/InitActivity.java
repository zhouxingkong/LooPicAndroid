package com.lab601.loopicandroid.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.lab601.loopicandroid.R;
import com.lab601.loopicandroid.listener.PermissionListener;
import com.lab601.loopicandroid.module.InitialCallback;
import com.lab601.loopicandroid.module.SourceManager;

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
    TextView statTextView;

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
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        statTextView = (TextView) findViewById(R.id.init_stat);
        startButton = (Button) findViewById(R.id.start_loo);
        startButton.setOnClickListener((view) -> {
            Intent intent = new Intent();
            intent.setClass(InitActivity.this, MainActivity.class);
            startActivity(intent);
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
