package com.lab601.loopicandroid.utils

import java.util.*

object ListUtil {
    // 打乱列表实现方法2
    fun <T> shuffle(list: List<T?>) {
        val size = list.size
        val random = Random()
        for (i in 0 until size) {
            // 获取随机位置
            val randomPos = random.nextInt(size)

            // 当前元素与随机元素交换
            Collections.swap(list, i, randomPos)
        }
    }
}