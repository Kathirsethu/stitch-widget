package com.stitch.stitchwidgets.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    @SerializedName("creationTime")
    var creationTime: String? = "",
    @SerializedName("modifiedTime")
    var modifiedTime: String? = "",
    @SerializedName("cardNumber")
    var cardNumber: String? = "",
    @SerializedName("cardId")
    var cardId: String? = "",
    @SerializedName("panFirstSix")
    var panFirst6: String? = "",
    @SerializedName("panLastFour")
    var panLast4: String? = "",
    var type: String? = "",
    var state: String? = "",
    @SerializedName("sequenceNumber")
    var sequenceNumber: Int? = 0,
    @SerializedName("cardProfileName")
    var cardProfileName: String? = "",
    @SerializedName("pinFailCount")
    var pinFailCount: Int? = 0,
    @SerializedName("reissue")
    var reissue: Boolean? = false,
    var expiry: String? = "",
    @SerializedName("customerId")
    var customerNumber: String? = "",
    @SerializedName("embossedName")
    var embossedName: String? = "",
    @SerializedName("programName")
    var programName: String? = "",
    var cvv2: String? = "",
) : Parcelable {

    override fun toString(): String {
        return cardId ?: ""
    }
}