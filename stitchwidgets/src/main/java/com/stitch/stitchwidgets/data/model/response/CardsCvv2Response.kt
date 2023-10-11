package com.stitch.stitchwidgets.data.model.response

import com.google.gson.annotations.SerializedName

data class CardsCvv2Response(
    @SerializedName("secureCvv2")
    var secureCvv2: String,
)