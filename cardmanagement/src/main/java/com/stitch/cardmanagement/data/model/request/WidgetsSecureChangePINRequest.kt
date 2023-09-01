package com.stitch.cardmanagement.data.model.request

import com.google.gson.annotations.SerializedName

class WidgetsSecureChangePINRequest(
    @SerializedName("existingPin")
    var existingPin: String,
    var pin: String,
    var token: String,
    @SerializedName("deviceFingerPrint")
    var deviceFingerprint: String,
)