package com.stitch.cardmanagement.data.model.request

import com.google.gson.annotations.SerializedName
import com.stitch.cardmanagement.utilities.Constants

class CommonGetRequest(
    @SerializedName("endPoint")
    var endPoint: String,
    @SerializedName("httpMethod")
    var httpMethod: String = Constants.HTTPMethod.GET,
    var token: String,
)