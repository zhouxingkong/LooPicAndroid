package com.lab601.loopicandroid.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.LinearLayout
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.lab601.loopicandroid.R
import com.lab601.loopicandroid.module.ConfigManager
import com.lab601.loopicandroid.module.EncodeHelper
import com.lab601.loopicandroid.module.SourceManager
import kotlinx.android.synthetic.main.activity_netpic_landscape.*
import java.io.File
import java.io.IOException
import kotlin.random.Random


class NetPicActivity : BaseActivity() {
    var MAX_SIZE = 2000000.0
    val WEIGHT_FOCUS = 4.0f
    val WEIGHT_NORMAL = 1.0f

    var photoView: SimpleDraweeView? = null
    var sceneListUI: ListView? = null
    var showSceneList = false

    var currStory = 0
    var currScene = 100

    var serMap = mutableMapOf<Int, Int>()
    var indexMap = mutableMapOf<Int, MutableMap<Int, Int>>()

//    val soundPath = "/storage/emulated/0/Loo/sounds"
    val soundPath = "/sdcard/Loo/sounds"
    var soundMap = mutableMapOf<String,List<File>>()
    var soundTag = mutableListOf<String>()

    var mediaPlayer: MediaPlayer? = null
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currStory = ConfigManager.instance.startStory
        currScene = ConfigManager.instance.startScene
//        val landscape = ConfigManager.instance.isLandscape
//        if (landscape) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            setContentView(R.layout.activity_netpic_landscape)
//        } else {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            setContentView(R.layout.activity_main_vertical)
//        }

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

        initSoundList()
    }

    fun initText(){
        /*设置文本字体*/
//        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/loo_font1.ttf");  // mContext为上下文
//        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "fonts/HanyiSentyCrayon.ttf");  // mContext为上下文
//        textView.setTypeface(typeface);
        loo_text?.setOnClickListener { view: View? ->  //下一张图
            currScene++
            showPage(currScene)
        }
        if(showSceneList) loo_text?.visibility = View.GONE
    }

    var storySelect = -1
    fun initStoryList(){
        val storyContainerList = listOf(storyContainer1,storyContainer2,storyContainer3,storyContainer4,storyContainer5,storyContainer6)
        val storyIndexTab = listOf(story_1,story_2,story_3,story_4,story_5,story_6)
        storyIndexTab.forEachIndexed { index, view ->
            if(ConfigManager.instance.allSceneList!!.size < index+1) storyContainerList[index]?.visibility = View.GONE
            else storyIndexTab[index]?.text = ConfigManager.instance.storyList!![0]
            storyIndexTab[index]?.setOnClickListener {
                storyContainerList.forEachIndexed { i, linearLayout ->
                    linearLayout?.setLayoutParams(
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0,
                            if(i!=index || storySelect==index) WEIGHT_NORMAL else WEIGHT_FOCUS))
                }
                storySelect = if(storySelect==index) -1 else index
            }
        }
    }

    fun initSceneList(){
        val sceneIndexList = listOf(index_list1,index_list2,index_list3,index_list4,index_list5,index_list6)
        sceneIndexList.forEachIndexed { index, listView ->
            if(ConfigManager.instance.allSceneList!!.size > index){
                val listItems = ConfigManager.instance.allSceneList!![index]
                val adapter = ArrayAdapter(this@NetPicActivity, R.layout.scene_list_item, listItems)
                listView?.adapter = adapter
                listView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val ser = incAndGetIndex(index, position)
                    showPage(position, index, ser)
                }
            }
        }

        if(!showSceneList) {
            story_index_container?.visibility = View.GONE
        }
    }

    fun initPreBtn(){
        pre_pic?.setOnClickListener { view: View? ->    //上一张图
            if (currScene > 0) {
                currScene--
                showPage(currScene)
            }
        }
    }

    fun initChangeBtn(){
        change_pic?.setOnClickListener { view: View? ->
            incAndGetSer(serMap, currScene)
            showPage(currScene)
        }
    }
    fun initRmBtn(){
        rm_button?.setOnClickListener { view: View? ->
            if(showSceneList){
                loo_text?.visibility = View.VISIBLE
                loo_bottom_container?.visibility = View.VISIBLE
                story_index_container?.visibility = View.GONE
                showSceneList = false
            }
            else{
                story_index_container?.visibility = View.VISIBLE
                loo_text?.visibility = View.GONE
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

        //预加载
        preloadImage(currScene + 1)
        preloadImage(currScene + 2)
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

    fun initSoundList(){
        val soundRoot = File(soundPath)
        soundRoot.listFiles().toList().forEachIndexed{ index,item ->
            soundTag.add(item.name)
            soundMap[item.name] = item.listFiles().toList()
        }

        val adapter = ArrayAdapter(this@NetPicActivity, R.layout.scene_list_item, soundTag)
        soundList?.adapter = adapter
        soundList?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val tagName = soundTag[position]
            val soundFileList = soundMap[tagName]
            val rand = Random.nextInt(0, Int.MAX_VALUE)
            playSingleSound(soundFileList!![rand % soundFileList.size])
        }
    }

    /**
     * @param index
     */
    fun onPageChanged(index: Int) {
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
            var text = EncodeHelper.decodeBase64(textList[currScene])
            text = text.replace("{", "<font color='#ff0000'>")
            text = text.replace("}", "</font>")
            if (text == "#") {
                text = ""
            }
            loo_text?.text = Html.fromHtml(text, 0)
        } else {
            loo_text?.text = Html.fromHtml("", 0)
        }
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