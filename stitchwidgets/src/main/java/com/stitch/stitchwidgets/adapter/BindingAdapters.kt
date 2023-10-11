package com.stitch.stitchwidgets.adapter

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import java.io.File

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.isGone = isGone
}

@BindingAdapter("isEnabled")
fun isEnabled(view: View, isEnabled: Boolean) {
    view.isClickable = isEnabled
    view.isFocusable = isEnabled
    view.isFocusableInTouchMode = isEnabled
}

@BindingAdapter("cardMedia")
fun cardMedia(view: ImageView, file: File?) {
    if (file != null)
        Glide.with(view.context).load(file).into(view)
}

@BindingAdapter("cardTypeImage")
fun cardTypeImage(view: ImageView, resourceId: Int) {
    view.setImageResource(resourceId)
}

@BindingAdapter("styleTILColor")
fun styleTILColor(view: TextInputLayout, resourceId: Int) {
    val states = arrayOf(
        intArrayOf(android.R.attr.state_enabled), // enabled
        intArrayOf(-android.R.attr.state_enabled), // disabled
        intArrayOf(-android.R.attr.state_checked), // unchecked
        intArrayOf(android.R.attr.state_pressed),  // pressed
        intArrayOf(android.R.attr.state_focused), // focused
        intArrayOf(-android.R.attr.state_focused)  // unfocused
    )
    val colors = intArrayOf(
        resourceId,
        resourceId,
        resourceId,
        resourceId,
        resourceId,
        resourceId
    )
    val colorStateList = ColorStateList(states, colors)
    view.hintTextColor = colorStateList
    view.defaultHintTextColor = colorStateList
    view.boxStrokeColor = resourceId
}

@BindingAdapter("styleFontSize")
fun styleFontSize(view: TextView, size: Int) {
    view.textSize = size.toFloat()
}

@BindingAdapter("styleFontFamily")
fun styleFontFamily(view: TextView, resourceId: Int) {
    view.typeface = ResourcesCompat.getFont(view.context, resourceId)
}

@BindingAdapter("styleFontColor")
fun styleFontColor(view: TextView, resourceId: Int) {
    view.setTextColor(resourceId)
}

@BindingAdapter("styleTILFontFamily")
fun styleTILFontFamily(view: TextInputLayout, resourceId: Int) {
    view.typeface = ResourcesCompat.getFont(view.context, resourceId)
}

@BindingAdapter(value = ["cardStyleTopPadding", "cardStyleBottomPadding", "cardStyleLeftPadding", "cardStyleRightPadding"])
fun cardStylePadding(view: TextView, top: Int, bottom: Int, left: Int, right: Int) {
    view.setPadding(left - right, top, right, bottom)
}

@BindingAdapter("cardStyleBackground")
fun cardStyleBackground(view: ConstraintLayout, resourceId: Any) {
    when (resourceId) {
        is Drawable -> {
            view.background = resourceId
        }

        is Double -> {
            view.setBackgroundColor(resourceId.toInt())
        }

        is Int -> {
            view.setBackgroundColor(resourceId)
        }
    }
}
