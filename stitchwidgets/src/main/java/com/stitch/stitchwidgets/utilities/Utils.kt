package com.stitch.stitchwidgets.utilities

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.inputmethod.InputMethodManager
import com.stitch.stitchwidgets.WidgetSDK
import java.io.File

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

    fun isDeviceRooted(): Boolean {
        return isRootedBySuBinary() || isRootedByRootManagementApps() || isRootedByTestKeys() || isRootedByWritableSystem()
    }

    private fun isRootedBySuBinary(): Boolean {
        val paths = arrayOf(
            "/system/xbin/su",
            "/system/bin/su",
            "/data/local/bin/su",
            "/data/local/su"
        )
        for (path in paths) {
            val file = File(path)
            if (file.exists()) {
                return true
            }
        }
        return false
    }

    private fun isRootedByRootManagementApps(): Boolean {
        val packages = arrayOf(
            "eu.chainfire.supersu",
            "com.noshufou.android.su"
        )

        for (packageName in packages) {
            try {
                val info = WidgetSDK.appContractor.context.packageManager.getApplicationInfo(
                    packageName,
                    0
                )
                if (info != null) {
                    return true
                }
            } catch (e: PackageManager.NameNotFoundException) {
                // App not found, continue checking
            }
        }
        return false
    }

    private fun isRootedByTestKeys(): Boolean {
        val buildTags = android.os.Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun isRootedByWritableSystem(): Boolean {
        val file = File("/system/app/Superuser.apk")
        return file.exists()
    }
}