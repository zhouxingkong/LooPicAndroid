package com.lab601.loopicandroid.module

import com.lab601.loopicandroid.api.LooService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class ConfigManager private constructor() {
    var isLandscape = true //横屏
    var isSound = false //静音
    var startStory = 0
    var startScene = 0
    var text: List<String>? = null
    var currSceneList:List<String>? = null  //当前选中的Scene列表

    var storyList:List<String>? = null
    var allSceneList:List<List<String>>? = null

    var url = ""
    lateinit var service: LooService


    companion object {
        private var mInstance: ConfigManager? = null
        var preloadMap = HashMap<Int, String>()
        @JvmStatic
        val instance: ConfigManager
            get() {
                if (mInstance == null) {
                    mInstance = ConfigManager()
                }
                return mInstance!!
            }
    }

    init {
        text = ArrayList()
    }

    fun initNetwork(ip:String){
        url = ip
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://${ip}:8080/")
                .build()
        service = retrofit.create(LooService::class.java)
    }
}