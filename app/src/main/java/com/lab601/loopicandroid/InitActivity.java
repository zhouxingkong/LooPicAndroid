package com.lab601.loopicandroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.List;

public class InitActivity extends BaseActivity {

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION"};
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        startButton = (Button) findViewById(R.id.start_loo);
        startButton.setOnClickListener((view) -> {
            Intent intent = new Intent();
            intent.setClass(InitActivity.this, MainActivity.class);
            startActivity(intent);
        });

        /*申请权限*/
        if (Build.VERSION.SDK_INT >= 23) {//判断当前系统是不是Android6.0
            requestRuntimePermissions(PERMISSIONS_STORAGE, new PermissionListener() {
                @Override
                public void granted() {
                    //权限申请通过
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
                            Log.d("xingkong", "申请权限通过 ");
//                            CustomToast.INSTANCE.showToast(SDK_WebApp.this, "没有文件读写权限,请检        查是否打开！");
                        }
                    }
                }
            });
        }
    }
}
