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
        initRmBtn()
        initStoryList()
        initSceneList()

        initSoundList()
    }

    var storySelect = -1
    fun initStoryList(){
        ConfigManager.allSceneList?:return
        val storyContainerList = listOf(storyContainer1,storyContainer2,storyContainer3,storyContainer4,storyContainer5,storyContainer6)
        val storyIndexTab = listOf(story_1,story_2,story_3,story_4,story_5,story_6)
        storyIndexTab.forEachIndexed { index, view ->
            if(ConfigManager.allSceneList!!.size < index+1) storyContainerList[index]?.visibility = View.GONE
            else storyIndexTab[index]?.text = ConfigManager.storyList!![0]
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
        ConfigManager.allSceneList?:return
        val sceneIndexList = listOf(index_list1,index_list2,index_list3,index_list4,index_list5,index_list6)
        sceneIndexList.forEachIndexed { index, listView ->
            if(ConfigManager.allSceneList!!.size > index){
                val listItems = ConfigManager.allSceneList!![index]
                val adapter = ArrayAdapter(this@NetMenuLooActivity, R.layout.scene_list_item, listItems)
                listView?.adapter = adapter
                listView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val ser = incAndGetIndex(index, position)
                    showPage(position, index, ser)
                }
            }
        }
    }

    fun initRmBtn(){
        rm_button?.setOnClickListener { view: View? ->
            finish()
        }
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

        soundRoot.listFiles().toList().forEachIndexed{ index,item ->
            val parent = inflater.inflate(R.layout.menu_sound_list_item,soundContainer,true) as? ViewGroup ?: return@forEachIndexed
            val soundText = parent.children.last() as? TextView
            soundText?.text = item.name
            soundText?.setOnClickListener {
                val tagName = soundTag[index]
                val soundFileList = soundMap[tagName] ?: return@setOnClickListener
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