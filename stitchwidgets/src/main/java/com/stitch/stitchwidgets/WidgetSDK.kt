package com.stitch.stitchwidgets

import android.app.Application
import com.stitch.stitchwidgets.data.model.SavedCardSettings
import com.stitch.stitchwidgets.di.AppContractor
import com.stitch.stitchwidgets.utilities.CardSDKException
import com.stitch.stitchwidgets.utilities.Utils

class WidgetSDK : Application() {

    override fun onCreate() {
        if (Utils.isDeviceRooted()) {
            // Throw the custom exception immediately if a rooted device is detected
            throw CardSDKException(
                "Insecure environment detected. Please use a secure device.",
                CardSDKException.INSECURE_ENVIRONMENT
            )
        }
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