package com.stitch.stitchwidgets

import android.app.Application
import com.stitch.stitchwidgets.data.model.SavedCardSettings
import com.stitch.stitchwidgets.di.AppContractor

class WidgetSDK : Application() {

    override fun onCreate() {
        super.onCreate()
        contractor = AppContractor(this)
    }

    companion object {
        private lateinit var contractor: AppContractor
        lateinit var baseUrl: String
        lateinit var viewCardSettings: SavedCardSettings
        lateinit var setPinSettings: SavedCardSettings
        lateinit var resetPinSettings: SavedCardSettings

        val appContractor by lazy {
            contractor
        }
    }
}