package com.stitch.stitchwidgets.data.model

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap

data class BaseResponse(
    var message: String? = "",
    @SerializedName("error_code")
    var errorCode: Int? = 0,
    var status: Int? = 0,
    var errors: LinkedTreeMap<String, Any>? = null,
) {
    fun errors() = errors?.values.orEmpty()
        .joinToString("\n") { it.toString().replace("[", "").replace("]", "") }
        .trim()
        .ifEmpty { "Error" }
}
