package com.lab601.loopicandroid.activity

import android.os.Bundle
import com.lab601.loopicandroid.module.ConfigManager
import android.annotation.SuppressLint
import com.facebook.drawee.backends.pipeline.Fresco
import android.os.Build
import com.lab601.loopicandroid.listener.PermissionListener
import com.lab601.loopicandroid.R
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_init.*
import java.lang.NumberFormatException
import java.util.stream.Collectors

class InitActivity : BaseActivity() {

    @SuppressLint("HandlerLeak")
    override var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                WHAT_INIT_SUCCESS -> {
                    init_stat?.text = "初始化成功"
                }
                WHAT_INIT_FAIL -> {
                    init_stat?.text = "初始化失败"
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
        ConfigManager.initNetwork(this.getString(R.string.ip))

        refresh?.setOnClickListener {
            ConfigManager.initNetwork(server_ip?.text.toString())

            showStoryList()
            showSceneList(ConfigManager.startStory)
            initTextData(ConfigManager.startStory)
        }

        initView()

        checkPermission()
    }

    fun initView(){
        showStoryList()
        initStartBtn()
        sound_on?.setOnClickListener { v: View? ->    //静音按钮
            if (ConfigManager.isSound) {
                sound_on?.text = "声音:关闭"
                ConfigManager.isSound = false
            } else {
                sound_on?.text = "声音:开启"
                ConfigManager.isSound = true
            }
        }

        SourceManager.instance.initialCallback = object : InitialCallback {
            override fun onSuccess() {
                handler.sendEmptyMessage(WHAT_INIT_SUCCESS)
            }

            override fun onFail() {
                handler.sendEmptyMessage(WHAT_INIT_FAIL)
            }
        }

    }


    fun showStoryList(){
        val data1 = ConfigManager.service.getAllSceneList()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe ({
                    ConfigManager.allSceneList = it
                },{
                    Log.e("xingkong","${it}")
                })

        val data2 = ConfigManager.service.getStoryList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ({
                    var data = it
                    data = data.stream().map {
                        it.substring(it.lastIndexOf("\\")+1,it.indexOf("."))
                    }.map { s: String? -> EncodeHelper.decodeBase64(s?:"") }
                            .collect(Collectors.toList())

                    ConfigManager.storyList = data

                    val adapter = ArrayAdapter(
                            this@InitActivity, android.R.layout.simple_list_item_1, data
                    )
                    story_list?.adapter = adapter
                },{
                    Log.e("xingkong","${it}")
                })

        story_list?.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            ConfigManager.startStory = position
            start_index?.text = "故事:${ConfigManager.startStory};场景${ConfigManager.startScene}"

            showSceneList(position)
            initTextData(position)
        }
    }

    fun showSceneList(scene:Int){
        ConfigManager.service.getSceneList(scene)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    Log.d("xingkong","scene=${it}")

                    var data = it
                    data = data.stream().map { s: String? -> EncodeHelper.decodeBase64(s?:"") }
                            .collect(Collectors.toList())
                    ConfigManager.currSceneList = data //传入Scene到配置
                    val adapter = ArrayAdapter(
                            this@InitActivity, android.R.layout.simple_list_item_1, data
                    )
                    scene_list?.adapter = adapter
                },{
                    Log.e("xingkong","${it}")
                })

        scene_list?.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            ConfigManager.startScene = position
            start_index?.text = "故事:${ConfigManager.startStory};场景${ConfigManager.startScene}"
        }
    }

    fun initTextData(storyId:Int){
        ConfigManager.service.getStoryText(storyId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                ConfigManager.text = it
            },{
                Log.e("xingkong","${it}")
            })
    }

    fun initStartBtn(){
        start_net_loo_story?.setOnClickListener { view: View? ->
            setIp()
            val intent = Intent()
            intent.setClass(this@InitActivity, NetStoryLooActivity::class.java)
            startActivity(intent)
        }
        start_net_loo_menu?.setOnClickListener { view: View? ->
            setIp()
            val intent = Intent()
            intent.setClass(this@InitActivity, NetMenuLooActivity::class.java)
            startActivity(intent)
        }
    }

    fun setIp(){
        var ip = ""
        try {
            ip = server_ip?.text.toString()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        if (ip.length > 5) {
//                ConfigManager.instance.url = ip
            ConfigManager.initNetwork(ip)
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