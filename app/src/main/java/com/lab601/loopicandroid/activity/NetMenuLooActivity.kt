package com.lab601.loopicandroid.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.text.Html
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
import kotlinx.android.synthetic.main.activity_loo_net_menu.*
import kotlinx.android.synthetic.main.activity_loo_net_menu.rm_button
import kotlinx.android.synthetic.main.activity_loo_net_story.*
import java.io.File
import kotlin.random.Random

/**
 * 点播切图
 */
class NetMenuLooActivity : BaseLooActivity() {

    val WEIGHT_FOCUS = 4.0f
    val WEIGHT_NORMAL = 1.0f

    var showSceneList = false

    var currStory = 0
    var currScene = 100

//    val soundPath = "/storage/emulated/0/Loo/sounds"

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_loo_net_menu)

        initView()

        photoView = findViewById<View>(R.id.photo_view) as SimpleDraweeView
        fullScreen()
        showPage(currScene)
    }

    fun initView(){
        initRmBtn()
        initStoryList()
        initSceneList()

        initSoundList()
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
                val adapter = ArrayAdapter(this@NetMenuLooActivity, R.layout.scene_list_item, listItems)
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

    fun initRmBtn(){
        rm_button?.setOnClickListener { view: View? ->
            finish()
        }
    }

    override fun showCurrPage() {
        val imagePipeline = Fresco.getImagePipeline()
        //        imagePipeline.clearCaches();
        val uri = Uri.parse(urlPic + currScene)
        imagePipeline.evictFromCache(uri)
        showPage(currScene)
    }

    fun initSoundList(){
        val soundRoot = File(soundPath)
        soundRoot.listFiles().toList().forEachIndexed{ index,item ->
            soundTag.add(item.name)
            soundMap[item.name] = item.listFiles().toList()
        }

        val adapter = ArrayAdapter(this@NetMenuLooActivity, R.layout.scene_list_item, soundTag)
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
    override fun onPageChanged(index: Int) {
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
}