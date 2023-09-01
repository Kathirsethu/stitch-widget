package com.stitch.cardmanagement.data.model.response

import com.google.gson.annotations.SerializedName

data class WidgetsSecureCardResponse(
    @SerializedName("accountNumber")
    var accountNumber: String,
    var expiry: String,
    var cvv: String,
)