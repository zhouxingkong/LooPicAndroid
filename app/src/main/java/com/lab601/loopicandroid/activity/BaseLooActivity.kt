package com.lab601.loopicandroid.activity

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.lab601.loopicandroid.module.ConfigManager
import java.io.File
import java.io.IOException


open class BaseLooActivity: BaseActivity() {
    var MAX_SIZE = 2000000.0

    var photoView: SimpleDraweeView? = null

    var mediaPlayer: MediaPlayer? = null

    val soundPath = "/sdcard/Loo/sounds"
    var soundMap = mutableMapOf<String,List<File>>()
    var soundTag = mutableListOf<String>()


    var serMap = mutableMapOf<Int, Int>()
    var indexMap = mutableMapOf<Int, MutableMap<Int, Int>>()

    open fun onPageChanged(index: Int) {

    }

    fun getSer(sceneIndex: Int):Int{
        return if(serMap.containsKey(sceneIndex)) serMap[sceneIndex]!!
        else 0
    }

    fun incAndGetIndex(storyIndex: Int, sceneIndex: Int):Int{
        if(!indexMap.containsKey(storyIndex)){
            indexMap.put(storyIndex, mutableMapOf<Int, Int>())
            return 0
        }
        val map = indexMap[storyIndex]!!
        return incAndGetSer(map, sceneIndex)
    }

    fun incAndGetSer(map: MutableMap<Int, Int>, sceneIndex: Int):Int{
        val ser = if(map.containsKey(sceneIndex)) map[sceneIndex]!! + 1
        else 1

        map.put(sceneIndex, ser)
        return ser
    }

    fun showPage(sceneIndex: Int, storyIndex: Int = -1, serIndex: Int = -1) {
        val serIndex = if(serIndex<0) getSer(sceneIndex) else serIndex
        /*渐进加载图片，然而并没有什么卵用*/
        val uri = Uri.parse("${urlPic}${if (storyIndex < 0) ConfigManager.instance.startStory else storyIndex}/${sceneIndex}/${serIndex}")
        val request = ImageRequestBuilder.newBuilderWithSource(uri)
            .setProgressiveRenderingEnabled(true)
            .build()
        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .setOldController(photoView!!.controller)
            .build()
        photoView!!.controller = controller
        //        photoView.setImageURI(uri);
        onPageChanged(sceneIndex)
    }

    /*-----------音频播放------*/

    fun playSingleSound(path:File){
        try {
            mediaPlayer = mediaPlayer ?: MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.let{
                it.setDataSource(path.path)
                it.prepare()
                it.start()
            }
        } catch (e: IOException) {
            Log.e("xingkong", "playSound: 播放音频异常", e)
            e.printStackTrace()
        }
    }

    /**
     * 播放音频
     *
     * @param path
     * @param index 播放到第几个了
     */
    fun playSound(path: List<File>?, index: Int) {
//        Log.d("xingkong", "playSound: 路径:" + path);
        if (path == null || path.size < 1 || path.size < index + 1) {
            return
        }
        try {
            mediaPlayer = mediaPlayer ?: MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(path[index].path)
            if (path.size > index + 1) {
                /*播放完成回调函数*/
                mediaPlayer?.setOnCompletionListener { mp: MediaPlayer? ->
                    playSound(path, index + 1)
                }
            }
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: IOException) {
            Log.e("xingkong", "playSound: 播放音频异常", e)
            e.printStackTrace()
        }
    }

}