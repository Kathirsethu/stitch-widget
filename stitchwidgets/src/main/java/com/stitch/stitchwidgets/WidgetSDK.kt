package com.stitch.stitchwidgets

import android.app.Application
import com.stitch.stitchwidgets.di.AppContractor

class WidgetSDK : Application() {

    override fun onCreate() {
        super.onCreate()
        contractor = AppContractor(this)
    }

    companion object {
        private lateinit var contractor: AppContractor
        lateinit var baseUrl: String

        val appContractor by lazy {
            contractor
        }
    }
}