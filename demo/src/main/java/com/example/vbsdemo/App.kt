package com.example.vbsdemo

import android.app.Application
import com.fr71.vbs.VbsHelper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        VbsHelper.init(this)
    }
}