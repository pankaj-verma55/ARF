package com.example.airavatresearchfoundation

import android.app.Application
import com.example.airavatresearchfoundation.di.AppComponent
import com.example.airavatresearchfoundation.di.DaggerAppComponent


class AiravatApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.create()
    }
}