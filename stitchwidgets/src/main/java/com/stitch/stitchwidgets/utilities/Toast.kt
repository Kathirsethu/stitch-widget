package com.stitch.stitchwidgets.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes

@SuppressLint("StaticFieldLeak")
object Toast {
    private lateinit var context: Context
    private var errorToastMessage: String? = ""

    operator fun invoke(context: Context) {
        this.context = context
    }

    fun success(message: String, length: Int = Toast.LENGTH_LONG) {
        val duration = if (message.length > 40) length else Toast.LENGTH_SHORT
        Toast.makeText(this.context, message, duration).show()
    }

    fun success(@StringRes message: Int, length: Int = Toast.LENGTH_LONG) =
        Toast.makeText(this.context, message, length).show()

    fun error(message: String, length: Int = Toast.LENGTH_LONG) {
        if (errorToastMessage != message) {
            errorToastMessage = message
            val duration = if (message.length > 40) length else Toast.LENGTH_SHORT
            Toast.makeText(this.context, message, duration).show()
            Looper.myLooper()?.let { myLooper ->
                android.os.Handler(myLooper).postDelayed({
                    errorToastMessage = ""
                }, if (message.length > 40) 3500 else 2000)
            }
        }
    }

    fun error(@StringRes message: Int, length: Int = Toast.LENGTH_LONG) =
        Toast.makeText(this.context, message, length).show()

    fun normal(message: String, length: Int = Toast.LENGTH_LONG) {
        val duration = if (message.length > 40) length else Toast.LENGTH_SHORT
        Toast.makeText(this.context, message, duration).show()
    }

    fun normal(@StringRes message: Int, length: Int = Toast.LENGTH_LONG) =
        Toast.makeText(this.context, message, length).show()
}