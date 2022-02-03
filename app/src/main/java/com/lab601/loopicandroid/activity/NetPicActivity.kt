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
import kotlinx.android.synthetic.main.activity_netpic_landscape.*
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

    var story1: TextView? = null
    var story2: TextView? = null
    var story3: TextView? = null
    var story4: TextView? = null
    var story5: TextView? = null
    var story6: TextView? = null

    var sceneList :List<String>? = null
    var currStory = 0
    var currScene = 100

    var serMap = mutableMapOf<Int,Int>()
    var indexMap = mutableMapOf<Int,MutableMap<Int,Int>>()


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
        initStoryList()
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

    fun initStoryList(){
        if(ConfigManager.instance.allSceneList!!.size<2) story_2?.visibility = View.GONE
        if(ConfigManager.instance.allSceneList!!.size<3) story_3?.visibility = View.GONE
        if(ConfigManager.instance.allSceneList!!.size<4) story_4?.visibility = View.GONE
        if(ConfigManager.instance.allSceneList!!.size<5) story_5?.visibility = View.GONE
        if(ConfigManager.instance.allSceneList!!.size<6) story_6?.visibility = View.GONE

        story_1?.setOnClickListener {
            currStory = 0
            doShowSceneList(currStory)
        }
        story_2?.setOnClickListener {
            currStory = 1
            doShowSceneList(currStory)
        }
        story_3?.setOnClickListener {
            currStory = 2
            doShowSceneList(currStory)
        }
        story_4?.setOnClickListener {
            currStory = 3
            doShowSceneList(currStory)
        }
        story_5?.setOnClickListener {
            currStory = 4
            doShowSceneList(currStory)
        }
        story_6?.setOnClickListener {
            currStory = 5
            doShowSceneList(currStory)
        }
    }

    fun initSceneList(){
        sceneList = ConfigManager.instance.currSceneList
        sceneListUI = findViewById<View>(R.id.index_list) as ListView

        val adapter = ArrayAdapter(
                this@NetPicActivity, android.R.layout.simple_list_item_1, sceneList
        )
        sceneListUI!!.adapter = adapter

        sceneListUI!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
//            incAndGetSer(serMap,position)
            incAndGetIndex(currStory,position)
            showPage(position,currStory)
        }
        if(!showSceneList) sceneListUI?.visibility = View.GONE
    }

    fun doShowSceneList(storyIndex:Int){
        val l = ConfigManager.instance.allSceneList!![storyIndex]
        val adapter = ArrayAdapter(
                this@NetPicActivity, android.R.layout.simple_list_item_1, l
        )
        sceneListUI!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
//            incAndGetSer(serMap,position)
            val ser = incAndGetIndex(currStory,position)
            showPage(position,currStory,ser)
        }
        sceneListUI!!.adapter = adapter
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
            incAndGetSer(serMap,currScene)
            showPage(currScene)
        }
    }
    fun initRmBtn(){
        rmButton = findViewById<View>(R.id.rm_button) as Button
        rmButton!!.setOnClickListener { view: View? ->
            if(showSceneList){
                sceneListUI?.visibility = View.GONE
                textView?.visibility = View.VISIBLE
                loo_bottom_container?.visibility = View.VISIBLE
                story_index_container?.visibility = View.GONE
                showSceneList = false
            }
            else{
                sceneListUI?.visibility = View.VISIBLE
                story_index_container?.visibility = View.VISIBLE
                textView?.visibility = View.GONE
                loo_bottom_container?.visibility = View.GONE
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

    fun showPage(sceneIndex: Int,storyIndex:Int = -1,serIndex:Int = -1) {

        val serIndex = if(serIndex<0) getSer(sceneIndex) else serIndex
        /*渐进加载图片，然而并没有什么卵用*/
        val uri = Uri.parse("${urlPic}${if(storyIndex<0) ConfigManager.instance.startStory else storyIndex}/${sceneIndex}/${serIndex}")
        Log.d("xingkong","uri=${urlPic}${if(storyIndex<0) ConfigManager.instance.startStory else storyIndex}/${sceneIndex}/${serIndex}")
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

    fun incAndGetIndex(storyIndex:Int,sceneIndex:Int):Int{
        if(!indexMap.containsKey(storyIndex)){
            indexMap.put(storyIndex,mutableMapOf<Int,Int>())
            Log.d("xingkong","1")
            return 0
        }
        val map = indexMap[storyIndex]!!
        return incAndGetSer(map,sceneIndex)
    }

    fun incAndGetSer(map:MutableMap<Int,Int>,sceneIndex:Int):Int{
        val ser = if(map.containsKey(sceneIndex)) map[sceneIndex]!! + 1
            else 1
        Log.d("xingkong","ser=${ser}")

        map.put(sceneIndex,ser)
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