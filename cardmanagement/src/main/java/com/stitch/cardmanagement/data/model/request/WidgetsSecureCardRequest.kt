package com.stitch.cardmanagement.data.model.request

import com.google.gson.annotations.SerializedName

class WidgetsSecureCardRequest(
    var token: String,
    @SerializedName("deviceFingerPrint")
    var deviceFingerprint: String,
)