package com.stitch.stitchwidgets.utilities

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

object Utils {

    fun pxToDp(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    fun hideKeyBoard(activity: Activity) {
        val inputManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        activity.currentFocus?.let {
            inputManager.hideSoftInputFromWindow(
                it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}