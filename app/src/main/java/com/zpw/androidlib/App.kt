package com.zpw.androidlib

import android.app.Application
import com.zpw.base.db.AppDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.init(this)
    }
}