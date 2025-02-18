package com.stitch.stitchwidgets.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.io.File

@Parcelize
data class SavedCardSettings(
    var widgetStyle: String? = null,
    var fontFamily: Int? = null,
    var fontColor: Int? = null,
    var buttonFontColor: Int? = null,
    var buttonBackgroundColor: Int? = null,
    var fontSize: Int? = null,
    var bgImageFile: File? = null,
    var background: @RawValue Any? = null,
    var numberTopPadding: Int? = null,
    var numberBottomPadding: Int? = null,
    var numberStartPadding: Int? = null,
    var numberEndPadding: Int? = null,
    var expiryTopPadding: Int? = null,
    var expiryBottomPadding: Int? = null,
    var expiryStartPadding: Int? = null,
    var expiryEndPadding: Int? = null,
    var cvvTopPadding: Int? = null,
    var cvvBottomPadding: Int? = null,
    var cvvStartPadding: Int? = null,
    var cvvEndPadding: Int? = null,
    var isCardNumberMasked: Boolean? = null,
    var isCardCVVMasked: Boolean? = null,
    var isEyeButton: Boolean? = null,
) : Parcelable