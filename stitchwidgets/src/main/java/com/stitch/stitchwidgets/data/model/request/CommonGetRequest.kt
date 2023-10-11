package com.stitch.stitchwidgets.data.model.request

import com.google.gson.annotations.SerializedName
import com.stitch.stitchwidgets.utilities.Constants

class CommonGetRequest(
    @SerializedName("endPoint")
    var endPoint: String,
    @SerializedName("httpMethod")
    var httpMethod: String = Constants.HTTPMethod.GET,
    var token: String,
)