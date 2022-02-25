package com.lab601.loopicandroid.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Html
import android.view.View
import com.facebook.drawee.view.SimpleDraweeView
import com.lab601.loopicandroid.R
import com.lab601.loopicandroid.module.ConfigManager
import com.lab601.loopicandroid.module.EncodeHelper
import com.lab601.loopicandroid.module.SourceManager
import kotlinx.android.synthetic.main.activity_loo_net_story.*
import kotlinx.android.synthetic.main.activity_loo_net_story.rm_button

class NetStoryLooActivity :BaseLooActivity(){

    var currStory = 0
    var currScene = 100

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currStory = ConfigManager.instance.startStory
        currScene = ConfigManager.instance.startScene
//        val landscape = ConfigManager.instance.isLandscape
//        if (landscape) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_loo_net_story)
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
            finish()
        }
    }

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

        //预加载
        preloadImage(currScene + 1)
        preloadImage(currScene + 2)
    }
}