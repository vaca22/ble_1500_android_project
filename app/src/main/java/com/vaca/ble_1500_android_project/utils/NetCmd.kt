package com.vaca.ble_1500_android_project.utils

import okhttp3.OkHttpClient
import okhttp3.Request

object NetCmd {
    private val client = OkHttpClient();
    private val url="http://139.9.206.3:8000/fuck5.bin"
    fun getFile(): ByteArray? {
        val request: Request = Request.Builder().url(url).build()
        client.newCall(request).execute().use {
            return (it.body?.bytes())
        }
    }
}