package com.lab601.loopicandroid.activity

import android.os.Bundle
import com.lab601.loopicandroid.module.ConfigManager
import android.annotation.SuppressLint
import com.facebook.drawee.backends.pipeline.Fresco
import com.lab601.loopicandroid.R
import com.lab601.loopicandroid.module.SourceManager
import com.facebook.drawee.view.SimpleDraweeView
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.drawee.interfaces.DraweeController
import android.text.Html
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.*
import java.io.File
import java.io.IOException

class NetPicActivity : BaseActivity() {
    var MAX_SIZE = 2000000.0
    var photoView: SimpleDraweeView? = null
    var textView: TextView? = null
    var changeButton: Button? = null
    var preButton: Button? = null
    var rmButton: Button? = null
    var sceneListUI: ListView? = null
    var showSceneList = false

    var sceneList :List<String>? = null
    var currScene = 100
    var serMap = mutableMapOf<Int,Int>()


    var mediaPlayer: MediaPlayer? = null
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startIndex = ConfigManager.instance.startScene
        currScene = startIndex
        val landscape = ConfigManager.instance.isLandscape
        if (landscape) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            setContentView(R.layout.activity_netpic_landscape)
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setContentView(R.layout.activity_main_vertical)
        }

        initView()

        photoView = findViewById<View>(R.id.photo_view) as SimpleDraweeView
        fullScreen()
        showPage(currScene)
    }

    fun initView(){
        initText()
        initPreBtn()
        initRmBtn()
        initChangeBtn()
        initSceneList()
    }

    fun initText(){
        /*初始化view*/textView = findViewById<View>(R.id.loo_text) as TextView
        /*设置文本字体*/
//        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/loo_font1.ttf");  // mContext为上下文
//        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/HanyiSentyCrayon.ttf");  // mContext为上下文
//        textView.setTypeface(typeface);
        textView!!.setOnClickListener { view: View? ->  //下一张图
            currScene++
            showPage(currScene)
        }
        if(showSceneList) textView?.visibility = View.GONE
    }

    fun initSceneList(){
        sceneList = ConfigManager.instance.currSceneList
        sceneListUI = findViewById<View>(R.id.index_list) as ListView

        val adapter = ArrayAdapter(
                this@NetPicActivity, android.R.layout.simple_list_item_1, sceneList
        )
        sceneListUI!!.adapter = adapter

        sceneListUI!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            incAndGetSer(position)
            showPage(position)
        }
        if(!showSceneList) sceneListUI?.visibility = View.GONE
    }

    fun initPreBtn(){
        preButton = findViewById<View>(R.id.pre_pic) as Button
        preButton!!.setOnClickListener { view: View? ->    //上一张图
            if (currScene > 0) {
                currScene--
                showPage(currScene)
            }
        }
    }

    fun initChangeBtn(){
        changeButton = findViewById<View>(R.id.change_pic) as Button
        changeButton!!.setOnClickListener { view: View? ->
            incAndGetSer(currScene)
            showPage(currScene)
        }
    }
    fun initRmBtn(){
        rmButton = findViewById<View>(R.id.rm_button) as Button
        rmButton!!.setOnClickListener { view: View? ->
            if(showSceneList){
                sceneListUI?.visibility = View.GONE
                textView?.visibility = View.VISIBLE
                showSceneList = false
            }
            else{
                sceneListUI?.visibility = View.VISIBLE
                textView?.visibility = View.GONE
                showSceneList = true
            }
        }
    }

    override fun showCurrPage() {
        val imagePipeline = Fresco.getImagePipeline()
        //        imagePipeline.clearCaches();
        val uri = Uri.parse(urlPic + currScene)
        imagePipeline.evictFromCache(uri)
        showPage(currScene)
    }

    fun showPage(sceneIndex: Int) {
        val serIndex = getSer(sceneIndex)
        /*渐进加载图片，然而并没有什么卵用*/
        val uri = Uri.parse("${urlPic}${ConfigManager.instance.startStory}/${sceneIndex}/${serIndex}")
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

        //预加载
        preloadImage(currScene + 1)
        preloadImage(currScene + 2)
    }

    fun getSer(sceneIndex:Int):Int{
        return if(serMap.containsKey(sceneIndex)) serMap[sceneIndex]!!
        else 0
    }

    fun incAndGetSer(sceneIndex:Int):Int{
        val ser = getSer(sceneIndex)+1
        serMap[sceneIndex] = ser
        return ser
    }

    /**
     * @param index
     */
    fun onPageChanged(index: Int) {
        Log.d("xingkong", "浏览索引:$index")
        if (ConfigManager.instance.isSound) {
            val displayMenu = SourceManager.instance.displayMenus?.get(index)
            val soundFiles = displayMenu?.soundList
            if (soundFiles != null && soundFiles.size > 0) {
                playSound(soundFiles, 0)
            } else {
                mediaPlayer!!.stop()
            }
        }

        /*显示文本*/
//        String text = SourceManager.getInstance().getDisplayMenus().get(index).getText();
        val textList = ConfigManager.instance.text
        if (textList != null && textList.size > currScene) {
            var text = decodeBase64(textList[currScene])
            text = text.replace("{", "<font color='#ff0000'>")
            text = text.replace("}", "</font>")
            if (text == "#") {
                text = ""
            }
            textView!!.text = Html.fromHtml(text, 0)
        } else {
            textView!!.text = Html.fromHtml("", 0)
        }
    }

    /**
     * 客户端解密
     *
     * @param in
     * @return
     */
//    fun decode(`in`: String): String {
//        val byteArray = `in`.toByteArray()
//        for (i in byteArray.indices) {
//            (byteArray[i] -= 1).toByte()
//        }
//        return String(byteArray)
//    }

    fun decodeBase64(data: String): String {
        return data
//        return String(Base64.getDecoder().decode(data.toByteArray()))
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
            mediaPlayer!!.stop()
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(path[index].path)
            if (path.size > index + 1) {
                /*播放完成回调函数*/
                mediaPlayer!!.setOnCompletionListener { mp: MediaPlayer? ->
                    Log.d("xingkong", "playSound: 音频播放完毕")
                    playSound(path, index + 1)
                }
            }
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
        } catch (e: IOException) {
            Log.e("xingkong", "playSound: 播放音频异常", e)
            e.printStackTrace()
        }
    }
}