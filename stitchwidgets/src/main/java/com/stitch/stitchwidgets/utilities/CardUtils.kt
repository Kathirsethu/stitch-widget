package com.stitch.stitchwidgets.utilities

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

object CardUtils {

    fun getCardNumber(cardNumber: String): String {
        return cardNumber.substring(0, 4) + " " +
                cardNumber.substring(4, 8) + " " +
                cardNumber.substring(8, 12) + " " +
                cardNumber.substring(12, 16)
    }

    fun TextView.setWidgetTypeFace(context: Context, actualFont: Int?, defaultFont: Int) {
        try {
            this.typeface = ResourcesCompat.getFont(context, actualFont ?: defaultFont)
        } catch (e: Exception) {
            e.printStackTrace()
            this.typeface = ResourcesCompat.getFont(context, defaultFont)
        }
    }

    fun TextView.setWidgetTextColor(context: Context, actualColor: Int?, defaultColor: Int) {
        try {
            this.setTextColor(ContextCompat.getColor(context, actualColor ?: defaultColor))
        } catch (e: Exception) {
            e.printStackTrace()
            this.setTextColor(ContextCompat.getColor(context, defaultColor))
        }
    }

    fun TextView.setWidgetFontSize(
        actualFontSize: Int?,
        defaultFontSize: Float
    ) {
        this.textSize = actualFontSize?.toFloat() ?: defaultFontSize
    }

    fun getWidgetPadding(actualPadding: Int?, defaultPadding: String): String {
        return actualPadding?.toString() ?: defaultPadding
    }
}