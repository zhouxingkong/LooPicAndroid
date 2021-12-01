package com.lab601.loopicandroid.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lab601.loopicandroid.module.ConfigManager
import com.lab601.loopicandroid.activity.BaseActivity
import android.annotation.SuppressLint
import com.facebook.imagepipeline.core.ImagePipeline
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import android.content.pm.PackageManager
import android.os.Build
import android.R.attr
import com.lab601.loopicandroid.listener.PermissionListener
import android.app.Activity
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import android.widget.TextView
import android.widget.EditText
import com.lab601.loopicandroid.activity.InitActivity
import android.widget.Toast
import com.lab601.loopicandroid.R
import com.lab601.loopicandroid.tasks.GetTextTask
import com.lab601.loopicandroid.tasks.ShowChapterListTask
import com.lab601.loopicandroid.module.EncodeHelper
import android.widget.ArrayAdapter
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.content.Intent
import com.lab601.loopicandroid.activity.NetPicActivity
import com.lab601.loopicandroid.module.SourceManager
import com.lab601.loopicandroid.module.InitialCallback
import com.facebook.drawee.view.SimpleDraweeView
import android.content.pm.ActivityInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.drawee.interfaces.DraweeController
import com.lab601.loopicandroid.module.DisplayMenu
import android.text.Html
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import java.util.ArrayList

open class BaseActivity : AppCompatActivity() {
    var urlPic = "http:/192.168.43.139:8080/loopicserver/show/"
    var urlStoryList = "http:/192.168.43.139:8080/changepic/"
    var urlSceneList = "http:/192.168.43.139:8080/erasecache"
    var urlText = "http:/192.168.43.139:8080/rmpic/"

    /*-----------申请权限---------------*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        urlPic = "http:/" + ConfigManager.instance?.url + ":8080/pic/"
        urlStoryList = "http:/" + ConfigManager.instance?.url + ":8080/story/list/"
        urlStoryList = "http:/" + ConfigManager.instance?.url + ":8080/scene/list/"
        urlSceneList = "http:/" + ConfigManager.instance?.url + ":8080/text/"
        preloadImage(0)
        preloadImage(1)
        preloadImage(2)
        activity = this
    }

    @SuppressLint("HandlerLeak")
    open var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_SHOW_PIC -> {
                    showCurrPage()
                }
            }
        }
    }

    open fun showCurrPage() {}
    fun preloadImage(index: Int) {
        if (!ConfigManager.preloadMap.containsKey(index)) {
            val uri = Uri.parse(urlPic + index)
            val imagePipeline = Fresco.getImagePipeline()
            val imageRequest = ImageRequest.fromUri(uri)
            imagePipeline.prefetchToDiskCache(imageRequest, applicationContext)
            ConfigManager.preloadMap[index] = "ok"
        }
    }

    /**
     * 申请后的处理
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0) {
            val deniedList: MutableList<String> = ArrayList()
            // 遍历所有申请的权限，把被拒绝的权限放入集合
            for (i in grantResults.indices) {
                val grantResult = grantResults[i]
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    mListener!!.granted()
                } else {
                    deniedList.add(permissions[i])
                }
            }
            if (!deniedList.isEmpty()) {
                mListener!!.denied(deniedList)
            }
        }
    }

    fun fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        val uiOptions = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        val isImmersiveModeEnabled = isImmersiveModeEnabled
        if (isImmersiveModeEnabled) {
            Log.i("TEST", "Turning immersive mode mode off. ")
        } else {
            Log.i("TEST", "Turning immersive mode mode on.")
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        window.decorView.systemUiVisibility = newUiOptions
        //END_INCLUDE (set_ui_flags)
    }

    private val isImmersiveModeEnabled: Boolean
        private get() = attr.uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == attr.uiOptions

    companion object {
        const val MESSAGE_SHOW_PIC = 10000
        const val MESSAGE_CLEAN = 10001
        private var mListener: PermissionListener? = null
        private var activity: Activity? = null
        fun requestRuntimePermissions(
            permissions: Array<String>, listener: PermissionListener?
        ) {
            mListener = listener
            val permissionList: MutableList<String> = ArrayList()
            // 遍历每一个申请的权限，把没有通过的权限放在集合中
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(activity!!, permission) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    permissionList.add(permission)
                } else {
                    mListener!!.granted()
                }
            }
            // 申请权限
            if (!permissionList.isEmpty()) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    permissionList.toTypedArray(), 1
                )
            }
        }
    }
}