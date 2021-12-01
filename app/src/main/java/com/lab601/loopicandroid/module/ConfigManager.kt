package com.lab601.loopicandroid.module

import java.util.*

class ConfigManager private constructor() {
    var isLandscape = true //横屏
    var isSound = false //静音
    var startStory = 0
    var startScene = 0
    var text: List<String>? = null
    var url = ""


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
}