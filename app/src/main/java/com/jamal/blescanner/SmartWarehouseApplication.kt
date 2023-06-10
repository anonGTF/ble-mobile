package com.jamal.blescanner

import android.app.Application
import com.clj.fastble.BleManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartWarehouseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        BleManager.getInstance().init(instance)
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(1, 5000)
            .setSplitWriteNum(20)
            .setConnectOverTime(10000)
            .setOperateTimeout(5000)
    }

    companion object {
        lateinit var instance: SmartWarehouseApplication
            private set
    }
}