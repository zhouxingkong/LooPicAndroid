package com.lab601.loopicandroid.listener

interface PermissionListener {
    fun granted()
    fun denied(deniedList: List<String?>?)
}