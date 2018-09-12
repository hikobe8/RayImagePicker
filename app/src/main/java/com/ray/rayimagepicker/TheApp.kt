package com.ray.rayimagepicker

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/***
 *  Author : ryu18356@gmail.com
 *  Create at 2018-09-12 10:54
 *  description :
 */
class TheApp: Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }

}