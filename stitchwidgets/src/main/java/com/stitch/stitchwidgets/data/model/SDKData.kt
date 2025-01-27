package com.stitch.stitchwidgets.data.model

import android.os.Parcelable
import com.stitch.stitchwidgets.data.model.response.Card
import kotlinx.parcelize.Parcelize

@Parcelize
data class SDKData(
    var title: String? = "",
    var card: Card? = null,
    var tokenType: String? = "",
    var apiToken: String? = "",
    var authToken: String? = "",
    var customerNumber: String? = "",
    var programName: String? = "",
    var secureToken: String? = "",
    var fingerPrint: String? = "",
) : Parcelable