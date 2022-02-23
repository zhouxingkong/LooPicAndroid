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
import com.lab601.loopicandroid.module.SourceManager
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

    var serMap = mutableMapOf<Int, Int>()
    var indexMap = mutableMapOf<Int, MutableMap<Int, Int>>()

    val WEIGHT_FOCUS = 4.0f
    val WEIGHT_NORMAL = 1.0f

    var mediaPlayer: MediaPlayer? = null
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currStory = ConfigManager.instance.startStory
        currScene = ConfigManager.instance.startScene
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

    var storySelect = -1
    fun initStoryList(){
        if(ConfigManager.instance.allSceneList!!.size<1) storyContainer1?.visibility = View.GONE else story_1?.text = ConfigManager.instance.storyList!![0]
        if(ConfigManager.instance.allSceneList!!.size<2) storyContainer2?.visibility = View.GONE else story_2?.text = ConfigManager.instance.storyList!![1]
        if(ConfigManager.instance.allSceneList!!.size<3) storyContainer3?.visibility = View.GONE else story_3?.text = ConfigManager.instance.storyList!![2]
        if(ConfigManager.instance.allSceneList!!.size<4) storyContainer4?.visibility = View.GONE else story_4?.text = ConfigManager.instance.storyList!![3]
        if(ConfigManager.instance.allSceneList!!.size<5) storyContainer5?.visibility = View.GONE else story_5?.text = ConfigManager.instance.storyList!![4]
        if(ConfigManager.instance.allSceneList!!.size<6) storyContainer6?.visibility = View.GONE else story_6?.text = ConfigManager.instance.storyList!![5]
        story_1?.setOnClickListener {
            storyContainer1?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, if(storySelect==0) WEIGHT_NORMAL else WEIGHT_FOCUS))
            storyContainer2?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer3?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer4?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer5?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer6?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storySelect = if(storySelect==0) -1 else 0
        }
        story_2?.setOnClickListener {
            storyContainer1?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer2?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, if(storySelect==1) WEIGHT_NORMAL else WEIGHT_FOCUS))
            storyContainer3?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer4?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer5?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer6?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storySelect = if(storySelect==1) -1 else 1
        }
        story_3?.setOnClickListener {
            storyContainer1?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer2?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer3?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, if(storySelect==2) WEIGHT_NORMAL else WEIGHT_FOCUS))
            storyContainer4?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer5?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer6?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storySelect = if(storySelect==2) -1 else 2
        }
        story_4?.setOnClickListener {
            storyContainer1?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer2?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer3?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer4?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, if(storySelect==3) WEIGHT_NORMAL else WEIGHT_FOCUS))
            storyContainer5?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer6?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storySelect = if(storySelect==3) -1 else 3
        }
        story_5?.setOnClickListener {
            storyContainer1?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer2?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer3?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer4?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer5?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, if(storySelect==4) WEIGHT_NORMAL else WEIGHT_FOCUS))
            storyContainer6?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storySelect = if(storySelect==4) -1 else 4
        }
        story_6?.setOnClickListener {
            storyContainer1?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer2?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer3?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer4?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer5?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, WEIGHT_NORMAL))
            storyContainer6?.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, if(storySelect==5) WEIGHT_NORMAL else WEIGHT_FOCUS))
            storySelect = if(storySelect==5) -1 else 5
        }
    }

    fun initSceneList(){
        if(ConfigManager.instance.allSceneList!!.size>0){
            val listItems = ConfigManager.instance.allSceneList!![0]
            val adapter = ArrayAdapter(this@NetPicActivity, R.layout.scene_list_item, listItems)
            index_list1?.adapter = adapter
            index_list1?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                incAndGetIndex(0, position)
                showPage(position, 0)
            }
        }
        if(ConfigManager.instance.allSceneList!!.size>1){
            val listItems = ConfigManager.instance.allSceneList!![1]
            val adapter = ArrayAdapter(this@NetPicActivity, R.layout.scene_list_item, listItems)
            index_list2?.adapter = adapter
            index_list2?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                incAndGetIndex(1, position)
                showPage(position, 1)
            }
        }
        if(ConfigManager.instance.allSceneList!!.size>2){
            val listItems = ConfigManager.instance.allSceneList!![2]
            val adapter = ArrayAdapter(this@NetPicActivity, R.layout.scene_list_item, listItems)
            index_list3?.adapter = adapter
            index_list3?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                incAndGetIndex(2, position)
                showPage(position, 2)
            }
        }
        if(ConfigManager.instance.allSceneList!!.size>3){
            val listItems = ConfigManager.instance.allSceneList!![3]
            val adapter = ArrayAdapter(this@NetPicActivity, R.layout.scene_list_item, listItems)
            index_list4?.adapter = adapter
            index_list4?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                incAndGetIndex(3, position)
                showPage(position, 3)
            }
        }
        if(ConfigManager.instance.allSceneList!!.size>4){
            val listItems = ConfigManager.instance.allSceneList!![4]
            val adapter = ArrayAdapter(this@NetPicActivity, R.layout.scene_list_item, listItems)
            index_list5?.adapter = adapter
            index_list5?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                incAndGetIndex(4, position)
                showPage(position, 4)
            }
        }
        if(ConfigManager.instance.allSceneList!!.size>5){
            val listItems = ConfigManager.instance.allSceneList!![5]
            val adapter = ArrayAdapter(this@NetPicActivity, R.layout.scene_list_item, listItems)
            index_list6?.adapter = adapter
            index_list6?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                incAndGetIndex(5, position)
                showPage(position, 5)
            }
        }

        if(!showSceneList) {
            story_index_container?.visibility = View.GONE
            sceneListUI?.visibility = View.GONE
        }
    }

    fun doShowSceneList(storyIndex: Int){
        val l = ConfigManager.instance.allSceneList!![storyIndex]
        val adapter = ArrayAdapter(
                this@NetPicActivity, R.layout.scene_list_item, l
        )
        sceneListUI!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
//            incAndGetSer(serMap,position)
            val ser = incAndGetIndex(currStory, position)
            showPage(position, currStory, ser)
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
            incAndGetSer(serMap, currScene)
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

    fun showPage(sceneIndex: Int, storyIndex: Int = -1, serIndex: Int = -1) {

        val serIndex = if(serIndex<0) getSer(sceneIndex) else serIndex
        /*渐进加载图片，然而并没有什么卵用*/
        val uri = Uri.parse("${urlPic}${if (storyIndex < 0) ConfigManager.instance.startStory else storyIndex}/${sceneIndex}/${serIndex}")
        Log.d("xingkong", "uri=${urlPic}${if (storyIndex < 0) ConfigManager.instance.startStory else storyIndex}/${sceneIndex}/${serIndex}")
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
            Log.d("xingkong", "1")
            return 0
        }
        val map = indexMap[storyIndex]!!
        return incAndGetSer(map, sceneIndex)
    }

    fun incAndGetSer(map: MutableMap<Int, Int>, sceneIndex: Int):Int{
        val ser = if(map.containsKey(sceneIndex)) map[sceneIndex]!! + 1
            else 1
        Log.d("xingkong", "ser=${ser}")

        map.put(sceneIndex, ser)
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