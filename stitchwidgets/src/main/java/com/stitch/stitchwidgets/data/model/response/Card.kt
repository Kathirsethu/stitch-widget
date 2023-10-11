package com.stitch.stitchwidgets.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Card(
    @SerializedName("creationTime")
    var creationTime: Long? = 0,
    @SerializedName("modifiedTime")
    var modifiedTime: Long? = 0,
    @SerializedName("cardNumber")
    var cardNumber: String? = "",
    @SerializedName("panFirst6")
    var panFirst6: String? = "",
    @SerializedName("panLast4")
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
    @SerializedName("customerNumber")
    var customerNumber: String? = "",
    @SerializedName("embossedName")
    var embossedName: String? = "",
    @SerializedName("programName")
    var programName: String? = "",
) : Parcelable {

    override fun toString(): String {
        return cardNumber ?: ""
    }
}