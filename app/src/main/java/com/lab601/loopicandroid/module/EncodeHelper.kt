package com.lab601.loopicandroid.module

import java.util.*

object EncodeHelper {
    fun decodeBase64(data: String): String {
        return String(Base64.getDecoder().decode(data.toByteArray()))
    }
}