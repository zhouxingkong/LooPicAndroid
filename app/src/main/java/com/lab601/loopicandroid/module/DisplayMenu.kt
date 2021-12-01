package com.lab601.loopicandroid.module

import android.util.Log
import com.lab601.loopicandroid.module.ConfigManager
import com.lab601.loopicandroid.module.DisplayMenu
import com.lab601.loopicandroid.module.SoundTagStorage
import com.lab601.loopicandroid.module.SourceManager
import com.lab601.loopicandroid.module.InitialCallback
import java.io.File
import java.lang.Exception
import java.util.ArrayList

/**
 * 输出文本的一行配置
 */
class DisplayMenu {
    var picFileName = "#" //图片文件名
    var soundTag = "#" //声音文件标签
    var soundMode = "#" //音频播放模式
    var text = "#" //文字描述
    var soundTagList: MutableList<String> = ArrayList()
    var soundList: MutableList<File> = ArrayList()
    override fun toString(): String {
        return "$picFileName $soundTag $soundMode $text"
    }

    fun setPicFileName(picFileName: String): DisplayMenu {
        this.picFileName = picFileName
        return this
    }

    fun setSoundTag(soundTag: String): DisplayMenu {
        this.soundTag = soundTag
        return this
    }

    fun setSoundMode(soundMode: String): DisplayMenu {
        this.soundMode = soundMode
        return this
    }

    fun setText(text: String): DisplayMenu {
        this.text = text
        return this
    }


    /**
     * 从string 中还原结构内容
     *
     * @param str
     */
    fun fromString(str: String) {
        val nameSplitSpace = str.split("\\ ").toTypedArray() //空格分开不同的部分
        if (nameSplitSpace.size != 4) {   //参数个数不对直接返回空
            return
        }
        picFileName = nameSplitSpace[0]
        soundTag = nameSplitSpace[1]
        soundMode = nameSplitSpace[2]
        text = nameSplitSpace[3]

        //todo: 输入的异常情况处理

        /*生成音频列表*/if (soundMode != "#" && soundMode.length > 0 && soundTag != "#" && soundTag.length > 0) {
            var soundLoop = 0
            try {
                soundLoop = soundMode.toInt()
            } catch (e: Exception) {
                Log.e("xingkong", "fromString:解析音频个数出错", e)
            }
            val split = soundTag.split("&").toTypedArray()
            for (s in split) {
                soundTagList.add(s)
            }

            /**/for (i in 0 until soundLoop) {
                for (tag in soundTagList) {
                    val soundFile = SoundTagStorage.getNextSoundFile(tag)
                    if (soundFile != null) {    //是null就不放进去
                        soundList.add(soundFile)
                    }
                }
            }
        }
    }
}