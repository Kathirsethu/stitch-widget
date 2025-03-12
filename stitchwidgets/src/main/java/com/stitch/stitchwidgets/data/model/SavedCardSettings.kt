package com.stitch.stitchwidgets.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.io.File

@Parcelize
data class SavedCardSettings(
    var widgetStyle: String? = null,
    var textFieldVariant: String? = null,
    var fontFamily: Int? = null,
    var fontColor: Int? = null,
    var buttonFontColor: Int? = null,
    var buttonBackground: Int? = null,
    var fontSize: Int? = null,
    var backgroundImage: File? = null,
    var background: @RawValue Any? = null,
    var cardNumberPaddingTop: Int? = null,
    var cardNumberPaddingBottom: Int? = null,
    var cardNumberPaddingLeft: Int? = null,
    var cardNumberPaddingRight: Int? = null,
    var expiryPaddingTop: Int? = null,
    var expiryPaddingBottom: Int? = null,
    var expiryPaddingLeft: Int? = null,
    var expiryPaddingRight: Int? = null,
    var cvvPaddingTop: Int? = null,
    var cvvPaddingBottom: Int? = null,
    var cvvPaddingLeft: Int? = null,
    var cvvPaddingRight: Int? = null,
    var maskCardNumber: Boolean? = null,
    var maskCvv: Boolean? = null,
    var showEyeIcon: Boolean? = null,
) : Parcelable