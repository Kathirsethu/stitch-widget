package com.stitch.cardmanagement.data.model.response

import com.google.gson.annotations.SerializedName

data class WidgetsSecureSessionKeyResponse(
    @SerializedName("generatedKey")
    var generatedKey: String,
)