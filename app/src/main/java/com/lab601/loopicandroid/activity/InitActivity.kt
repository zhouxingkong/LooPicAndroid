package com.lab601.loopicandroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lab601.loopicandroid.module.ConfigManager
import com.lab601.loopicandroid.activity.BaseActivity
import android.annotation.SuppressLint
import com.facebook.imagepipeline.core.ImagePipeline
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import android.content.pm.PackageManager
import android.os.Build
import android.R.attr
import com.lab601.loopicandroid.listener.PermissionListener
import android.app.Activity
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import com.lab601.loopicandroid.activity.InitActivity
import com.lab601.loopicandroid.R
import com.lab601.loopicandroid.tasks.GetTextTask
import com.lab601.loopicandroid.tasks.ShowChapterListTask
import com.lab601.loopicandroid.module.EncodeHelper
import android.widget.AdapterView.OnItemClickListener
import android.content.Intent
import com.lab601.loopicandroid.activity.NetPicActivity
import com.lab601.loopicandroid.module.SourceManager
import com.lab601.loopicandroid.module.InitialCallback
import com.facebook.drawee.view.SimpleDraweeView
import android.content.pm.ActivityInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.drawee.interfaces.DraweeController
import com.lab601.loopicandroid.module.DisplayMenu
import android.text.Html
import android.media.MediaPlayer.OnCompletionListener
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import java.lang.Exception
import java.lang.NumberFormatException
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors

class InitActivity : BaseActivity() {
    var startButton: Button? = null
    var startNetPicButton: Button? = null
    var statTextView: TextView? = null
    var landscapeButton: Button? = null
    var soundButton: Button? = null
    var clearCacheButton: Button? = null
    var chapterList: ListView? = null
    var initPosText: TextView? = null
    var ipEdit: EditText? = null

    @SuppressLint("HandlerLeak")
    override var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                WHAT_INIT_SUCCESS -> {
                    statTextView!!.text = "初始化成功"
                }
                WHAT_INIT_FAIL -> {
                    statTextView!!.text = "初始化失败"
                }
                BaseActivity.Companion.MESSAGE_CLEAN -> {
                    doClean()
                    Toast.makeText(baseContext, "清理完成", Toast.LENGTH_SHORT).show()
                }
                else -> {
                }
            }
        }
    }

    fun doClean() {
        val imagePipeline = Fresco.getImagePipeline()
        imagePipeline.clearCaches()
        ConfigManager.preloadMap.clear()
        preloadImage(0)
        preloadImage(1)
        preloadImage(2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)
        ConfigManager.instance?.url = this.getString(R.string.ip)
        val getTextTask = GetTextTask(handler)
        getTextTask.start()
        statTextView = findViewById<View>(R.id.init_stat) as TextView
        chapterList = findViewById<View>(R.id.chapter_list) as ListView
        @SuppressLint("StaticFieldLeak") val showChapterListTask: ShowChapterListTask =
            object : ShowChapterListTask() {
                override fun showChapterList(data: List<String>) {
                    var data = data
                    data = data.stream().map { s: String? -> EncodeHelper.decodeBase64(s?:"") }
                        .collect(Collectors.toList())
                    val adapter = ArrayAdapter(
                        this@InitActivity, android.R.layout.simple_list_item_1, data
                    )
                    chapterList!!.adapter = adapter
                }
            }
        showChapterListTask.execute()
        chapterList!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            ConfigManager.instance.startIndex = position
            initPosText!!.text = "起始位置:$position"
        }
        startNetPicButton = findViewById<View>(R.id.start_net_loo) as Button
        startNetPicButton!!.setOnClickListener { view: View? ->
//            int startPos = 0;
            var ip = ""
            try {
//                String startPosStr = initPosEdit.getText().toString();
//                startPos = Integer.parseInt(startPosStr);
                ip = ipEdit!!.text.toString()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            //            ConfigManager.getInstance().setStartIndex(startPos);
            if (ip.length > 5) {
                ConfigManager.instance.url = ip
            }
            val intent = Intent()
            intent.setClass(this@InitActivity, NetPicActivity::class.java)
            startActivity(intent)
        }
        initPosText = findViewById<View>(R.id.start_index) as TextView
        ipEdit = findViewById<View>(R.id.server_ip) as EditText
        soundButton = findViewById<View>(R.id.sound_on) as Button
        landscapeButton = findViewById<View>(R.id.landscape_on) as Button
        clearCacheButton = findViewById<View>(R.id.clear_cache) as Button
        clearCacheButton!!.setOnClickListener { v: View? ->    //静音按钮
            val netThread: Thread = object : Thread() {
                override fun run() {
                    try {
                        val url = URL(urlSceneList)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "POST" //设置请求方式为POST
                        connection.connect() //连接
                        val responseCode = connection.responseCode
                        if (responseCode == 200) {
                            handler.sendEmptyMessage(BaseActivity.Companion.MESSAGE_CLEAN)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            netThread.start()
        }
        soundButton!!.setOnClickListener { v: View? ->    //静音按钮
            if (ConfigManager.instance.isSound) {
                soundButton!!.text = "声音:关闭"
                ConfigManager.instance.isSound = false
            } else {
                soundButton!!.text = "声音:开启"
                ConfigManager.instance.isSound = true
            }
        }
        landscapeButton!!.setOnClickListener { v: View? ->    //设置横竖屏
            if (ConfigManager.instance.isLandscape) {
                landscapeButton!!.text = "竖屏显示"
                ConfigManager.instance.isLandscape = false
            } else {
                landscapeButton!!.text = "横屏显示"
                ConfigManager.instance.isLandscape = true
            }
        }


        /*设置回调*/SourceManager.instance.initialCallback = object : InitialCallback() {
            override fun onSuccess() {
                handler.sendEmptyMessage(WHAT_INIT_SUCCESS)
            }

            override fun onFail() {
                handler.sendEmptyMessage(WHAT_INIT_FAIL)
            }
        }

        /*申请权限*/if (Build.VERSION.SDK_INT >= 23) { //判断当前系统是不是Android6.0
            BaseActivity.Companion.requestRuntimePermissions(
                PERMISSIONS_STORAGE,
                object : PermissionListener {
                    override fun granted() {
                        //权限申请通过
                        Log.d("xingkong", "denied:权限通过", null)
                        val thread: Thread = object : Thread() {
                            override fun run() {
                                SourceManager.instance
                                    .readConfigFile(SourceManager.ROOT_PATH + "/index.txt")
                            }
                        }
                        thread.start()
                    }

                    override fun denied(deniedList: List<String?>?) {
                        deniedList?:return
                        //权限申请未通过
                        for (denied in deniedList) {
                            if (denied == "android.permission.ACCESS_FINE_LOCATION") {
                                Log.e("xingkong", "denied:申请权限失败", null)
                                //                            CustomToast.INSTANCE.showToast(SDK_WebApp.this, "定位失败，请检查是否打开定位权限！");
                            } else {
                                Log.e("xingkong", "denied:申请权限失败", null)
                                //                            CustomToast.INSTANCE.showToast(SDK_WebApp.this, "没有文件读写权限,请检        查是否打开！");
                            }
                        }
                    }
                })
        }
    }

    companion object {
        private const val WHAT_INIT_SUCCESS = 10
        private const val WHAT_INIT_FAIL = 11
        private val PERMISSIONS_STORAGE = arrayOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION"
        )
    }
}