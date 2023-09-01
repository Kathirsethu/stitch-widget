package com.stitch.cardmanagement.utilities

import android.content.Context
import androidx.databinding.ObservableField
import com.stitch.cardmanagement.R

fun ObservableField<String>.validateCVV(showToast: Boolean = true, context: Context) =
    when (get().isNullOrEmpty()) {
        false -> false
        else -> {
            if (showToast)
                Toast.error(context.getString(R.string.invalid_cvv))
            true
        }
    }

fun ObservableField<String>.validatePIN(showToast: Boolean = true, context: Context): Boolean {
    return if ((get() ?: "").isEmpty()) {
        if (showToast)
            Toast.error(context.getString(R.string.invalid_pin))
        true
    } else {
        if (get()?.length != 4) {
            if (showToast)
                Toast.error(context.getString(R.string.invalid_pin))
            true
        } else {
            false
        }
    }
}

fun ObservableField<String>.validateConfirmPIN(
    showToast: Boolean = true,
    context: Context
): Boolean {
    return if ((get() ?: "").isEmpty()) {
        if (showToast)
            Toast.error(context.getString(R.string.invalid_confirm_pin))
        true
    } else {
        if (get()?.length != 4) {
            if (showToast)
                Toast.error(context.getString(R.string.invalid_confirm_pin))
            true
        } else {
            false
        }
    }
}

fun ObservableField<String>.validateOldPIN(showToast: Boolean = true, context: Context): Boolean {
    return if ((get() ?: "").isEmpty()) {
        if (showToast)
            Toast.error(context.getString(R.string.invalid_old_pin))
        true
    } else {
        if (get()?.length != 4) {
            if (showToast)
                Toast.error(context.getString(R.string.invalid_old_pin))
            true
        } else {
            false
        }
    }
}

fun ObservableField<String>.validateNewPIN(showToast: Boolean = true, context: Context): Boolean {
    return if ((get() ?: "").isEmpty()) {
        if (showToast)
            Toast.error(context.getString(R.string.invalid_new_pin))
        true
    } else {
        if (get()?.length != 4) {
            if (showToast)
                Toast.error(context.getString(R.string.invalid_new_pin))
            true
        } else {
            false
        }
    }
}