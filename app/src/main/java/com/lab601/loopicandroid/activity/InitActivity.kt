package com.lab601.loopicandroid.activity

import android.os.Bundle
import com.lab601.loopicandroid.module.ConfigManager
import android.annotation.SuppressLint
import com.facebook.drawee.backends.pipeline.Fresco
import android.os.Build
import com.lab601.loopicandroid.listener.PermissionListener
import com.lab601.loopicandroid.R
import com.lab601.loopicandroid.tasks.GetStoryListTask
import com.lab601.loopicandroid.module.EncodeHelper
import android.widget.AdapterView.OnItemClickListener
import android.content.Intent
import com.lab601.loopicandroid.module.SourceManager
import com.lab601.loopicandroid.module.InitialCallback
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import com.lab601.loopicandroid.tasks.GetSceneListTask
import com.lab601.loopicandroid.tasks.GetTextTask
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
    var refreshButton: Button? = null
    var clearCacheButton: Button? = null
    var stroyList: ListView? = null
    var sceneList: ListView? = null
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

        statTextView = findViewById<View>(R.id.init_stat) as TextView
        stroyList = findViewById<View>(R.id.story_list) as ListView
        sceneList = findViewById<View>(R.id.scene_list) as ListView

        initPosText = findViewById<View>(R.id.start_index) as TextView
        ipEdit = findViewById<View>(R.id.server_ip) as EditText
        soundButton = findViewById<View>(R.id.sound_on) as Button
        landscapeButton = findViewById<View>(R.id.landscape_on) as Button
        clearCacheButton = findViewById<View>(R.id.clear_cache) as Button
        startNetPicButton = findViewById<View>(R.id.start_net_loo) as Button
        refreshButton = findViewById<View>(R.id.refresh) as Button
        refreshButton?.setOnClickListener {
            ConfigManager.instance.url = ipEdit!!.text.toString()

            showStoryList()
            showSceneList(ConfigManager.instance.startStory)
            initTextData(ConfigManager.instance.startStory)
        }

        initView()

        checkPermission()
    }

    fun initView(){
        showStoryList()
        initStartBtn()
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

        SourceManager.instance.initialCallback = object : InitialCallback() {
            override fun onSuccess() {
                handler.sendEmptyMessage(WHAT_INIT_SUCCESS)
            }

            override fun onFail() {
                handler.sendEmptyMessage(WHAT_INIT_FAIL)
            }
        }
        initClearCache()

    }


    fun showStoryList(){
        @SuppressLint("StaticFieldLeak")
        val getStoryListTask: GetStoryListTask = object : GetStoryListTask() {
                override fun showChapterList(data: List<String>) {
                    var data = data
                    data = data.stream().map { s: String? -> EncodeHelper.decodeBase64(s?:"") }
                        .collect(Collectors.toList())
                    val adapter = ArrayAdapter(
                        this@InitActivity, android.R.layout.simple_list_item_1, data
                    )
                    stroyList!!.adapter = adapter
                }
            }
        getStoryListTask.execute()

        stroyList!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            ConfigManager.instance.startStory = position
            initPosText!!.text = "故事:${ConfigManager.instance.startStory};场景${ConfigManager.instance.startScene}"

            showSceneList(position)
            initTextData(position)
        }
    }

    fun showSceneList(scene:Int){
        @SuppressLint("StaticFieldLeak")
        val getSceneTask = object: GetSceneListTask(scene){
            override fun showChapterList(data: List<String>) {
                var data = data
                data = data.stream().map { s: String? -> EncodeHelper.decodeBase64(s?:"") }
                    .collect(Collectors.toList())
                ConfigManager.instance.currSceneList = data //传入Scene到配置
                val adapter = ArrayAdapter(
                    this@InitActivity, android.R.layout.simple_list_item_1, data
                )
                sceneList!!.adapter = adapter
            }
        }
        getSceneTask.execute()
        sceneList!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            ConfigManager.instance.startScene = position
            initPosText!!.text = "故事:${ConfigManager.instance.startStory};场景${ConfigManager.instance.startScene}"
        }
    }

    fun initTextData(storyId:Int){
        val getTextTask = GetTextTask(handler,storyId)
        getTextTask.start()
    }

    fun initStartBtn(){
        startNetPicButton!!.setOnClickListener { view: View? ->
            var ip = ""
            try {
                ip = ipEdit!!.text.toString()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            if (ip.length > 5) {
                ConfigManager.instance.url = ip
            }
            val intent = Intent()
            intent.setClass(this@InitActivity, NetPicActivity::class.java)
            startActivity(intent)
        }
    }

    fun initClearCache(){
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
    }

    fun checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) { //判断当前系统是不是Android6.0
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