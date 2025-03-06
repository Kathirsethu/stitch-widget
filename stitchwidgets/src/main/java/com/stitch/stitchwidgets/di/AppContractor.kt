package com.stitch.stitchwidgets.di

import android.content.Context
import com.stitch.stitchwidgets.WidgetSDK

class AppContractor(app: WidgetSDK) {

    /* Application Level Context */
    val context: Context by lazy {
        app.applicationContext
    }
}