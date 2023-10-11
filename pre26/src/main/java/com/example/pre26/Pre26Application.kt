package com.example.pre26

import android.app.Application

class Pre26Application : Application() {
    override fun onCreate() {
        super.onCreate()
        DIUtils.init()
    }
}