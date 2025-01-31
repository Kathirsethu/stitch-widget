package com.stitch.stitchwidgets.data.model.response

import com.google.gson.annotations.SerializedName

data class WidgetsSecureCardResponse(
    @SerializedName("items")
    var items: Card,
    var status: String,
)