package com.stitch.stitchwidgets.data.model.request

import com.google.gson.annotations.SerializedName

class WidgetsSecureSetPINRequest(
    var pin: String,
    var token: String,
    @SerializedName("deviceFingerPrint")
    var deviceFingerprint: String,
)