package com.stitch.stitchwidgets.data.model.request

import com.google.gson.annotations.SerializedName

class WidgetsSecureChangePINRequest(
    @SerializedName("old_pin")
    var existingPin: String,
    @SerializedName("new_pin")
    var pin: String,
    var token: String,
    @SerializedName("device_fingerprint")
    var deviceFingerprint: String,
)