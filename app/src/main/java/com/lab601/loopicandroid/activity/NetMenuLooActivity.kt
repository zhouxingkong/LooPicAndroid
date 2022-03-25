package com.lab601.loopicandroid.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.LinearLayout
import androidx.core.view.children
import com.facebook.drawee.view.SimpleDraweeView
import com.lab601.loopicandroid.R
import com.lab601.loopicandroid.module.ConfigManager
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

//    val soundPath = "/storage/emulated/0/Loo/sounds"
    lateinit var inflater:LayoutInflater

    var storySelect = -1
    val storyCheckList = mutableListOf<Boolean>()
    val storyContainerList = mutableListOf<LinearLayout>()
    val storyIndexTab = mutableListOf<TextView>()
    val sceneIndexList = mutableListOf<ListView>()

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_loo_net_menu)

        inflater = LayoutInflater.from(this)
        initView()

        photoView = findViewById<View>(R.id.photo_view) as SimpleDraweeView
        fullScreen()
//        showPage(currScene)
    }

    fun initView(){
        initStoryList()

        initSoundList()
    }

    var lastSelectStory = -1
    var lastSelectScene = -1
    fun initStoryList(){
        ConfigManager.allSceneList?:return
        ConfigManager.allSceneList!!.forEachIndexed { index,item ->
            /*tab初始化*/
            storyCheckList.add(false)
            val tabParent = inflater.inflate(R.layout.story_tab_item,story_tab_container,true) as? ViewGroup ?:return@forEachIndexed
            val tabTitle = tabParent.children.last() as? TextView ?: return@forEachIndexed
            tabTitle.text = ConfigManager.storyList!![index]
            tabTitle.setOnClickListener {
                if(storyCheckList[index]){
                    storyCheckList[index] = false
                    tabTitle.setTextColor(resources.getColor(R.color.white))
                    storyContainerList[index].visibility = View.GONE
                }else{
                    storyCheckList[index] = true
                    tabTitle.setTextColor(resources.getColor(R.color.colorAccent))
                    storyContainerList[index].visibility = View.VISIBLE
                }
            }

            /*故事列表初始化*/
            val parent = inflater.inflate(R.layout.menu_story_list_item,story_index_container,true) as? ViewGroup ?:return@forEachIndexed
            val rootView = parent.children.last() as? LinearLayout ?: return@forEachIndexed
            val storyIndex = rootView.findViewById<TextView>(R.id.storyTitle)
            val sceneListView = rootView.findViewById<ListView>(R.id.storySceneList)
            storyIndexTab.add(index,storyIndex)
            sceneIndexList.add(index,sceneListView)
            storyContainerList.add(index,rootView)

            storyIndex.text = ConfigManager.storyList!![index]
            storyIndex.setOnClickListener {
                storyContainerList.forEachIndexed { i, linearLayout ->
                    linearLayout.setLayoutParams(
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            0,
                            if(i!=index || storySelect==index) WEIGHT_NORMAL else WEIGHT_FOCUS))
                }
                storySelect = if(storySelect==index) -1 else index
            }

            val listItems = ConfigManager.allSceneList!![index]
            val adapter = ArrayAdapter(this@NetMenuLooActivity, R.layout.scene_list_item, listItems)
            sceneListView?.adapter = adapter
            sceneListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val ser = if(ConfigManager.isFastMenu && lastSelectStory == index && lastSelectScene == position) getIndex(index, position)
                    else incAndGetIndex(index, position)
                showPage(position, index, ser)
            }

            rootView.visibility = View.GONE
        }

    }

    fun initRmBtn(){
//        rm_button?.setOnClickListener { view: View? ->
//            finish()
//        }
    }

    fun initSoundList(){
        val soundRoot = File(soundPath)
        soundRoot.listFiles().toList().forEachIndexed{ index,item ->
            soundTag.add(item.name)
            soundMap[item.name] = item.listFiles().toList()
        }

//        val adapter = ArrayAdapter(this@NetMenuLooActivity, R.layout.scene_list_item, soundTag)
//        soundList?.adapter = adapter
//        soundList?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
//            val tagName = soundTag[position]
//            val soundFileList = soundMap[tagName]
//            val rand = Random.nextInt(0, Int.MAX_VALUE)
//            playSingleSound(soundFileList!![rand % soundFileList.size])
//        }

        soundRoot.listFiles().toList().forEachIndexed{ index,item ->
            val parent = inflater.inflate(R.layout.menu_sound_list_item,soundContainer,true) as? ViewGroup ?: return@forEachIndexed
            val soundText = parent.children.last() as? TextView
            soundText?.text = item.name
            soundText?.setOnClickListener {
                val tagName = soundTag[index]
                val soundFileList = soundMap[tagName] ?: return@setOnClickListener
                if(soundFileList.isEmpty()) return@setOnClickListener
                val rand = Random.nextInt(0, Int.MAX_VALUE)
                playSingleSound(soundFileList[rand % soundFileList.size])
            }
        }
    }

    /**
     * @param index
     */
    override fun onPageChanged(index: Int) {

    }
}