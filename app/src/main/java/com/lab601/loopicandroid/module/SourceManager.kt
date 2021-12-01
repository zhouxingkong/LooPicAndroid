package com.lab601.loopicandroid.module

import com.lab601.loopicandroid.module.ConfigManager
import com.lab601.loopicandroid.module.DisplayMenu
import com.lab601.loopicandroid.module.SoundTagStorage
import com.lab601.loopicandroid.module.SourceManager
import com.lab601.loopicandroid.module.InitialCallback
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.ArrayList

/**
 * 资源管理类
 */
class SourceManager private constructor() {
    var inited = false
    var displayMenus: MutableList<DisplayMenu>? = null
    var initialCallback: InitialCallback? = null

    /**
     * 读取输入文件
     *
     * @param path
     */
    fun readConfigFile(path: String?) {
        if (inited) {   //只允许初始化一次,避免出现并发问题
            return
        }
        inited = true
        try {
            val fr = FileReader(path)
            val bf = BufferedReader(fr)
            var str: String
            displayMenus = ArrayList()

            /*第一行:读取源文件路径*/while (bf.readLine().also { str = it } != null) {
                val oneDesc = DisplayMenu()
                oneDesc.fromString(str)
                displayMenus?.add(oneDesc)
            }
            bf.close()
            fr.close()

            /*成功回调*/if (initialCallback != null) {
                initialCallback!!.onSuccess()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            /*失败回调*/if (initialCallback != null) {
                initialCallback!!.onFail()
            }
        }
    }

    companion object {
        const val ROOT_PATH = "/sdcard/bb/output" //资源的总路径
        const val PICTURE_PATH = ROOT_PATH + "/imgs"
        const val SOUND_ROOT = "/sdcard/bb/sounds"
        private var mInstance: SourceManager? = null
        @JvmStatic
        val instance: SourceManager
            get() {
                if (mInstance == null) {
                    mInstance = SourceManager()
                }
                return mInstance!!
            }
    }
}