package com.stitch.cardmanagement.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.io.File

@Parcelize
data class SavedCardSettings(
    var widgetStyle: String? = null,
    var fontFamily: Int? = null,
    var fontColor: Int? = null,
    var fontSize: Int? = null,
    var bgImageFile: File? = null,
    var background: @RawValue Any? = null,
    var numberPadding: Int? = null,
    var expiryPadding: Int? = null,
    var cvvPadding: Int? = null,
    var isCardNumberMasked: Boolean? = null,
    var isCardCVVMasked: Boolean? = null,
) : Parcelable