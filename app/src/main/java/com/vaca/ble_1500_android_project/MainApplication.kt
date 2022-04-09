package com.vaca.ble_1500_android_project

import android.app.Application
import com.vaca.ble_1500_android_project.utils.PathUtil


class MainApplication : Application() {

    companion object {

        lateinit var application: Application
    }


    override fun onCreate() {
        application = this
        super.onCreate()
        PathUtil.initVar(this)

    }


}