package com.stitch.stitchwidgets.utilities

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import com.stitch.stitchwidgets.R
import java.io.File
import java.math.BigInteger
import java.net.InetAddress
import java.net.NetworkInterface
import java.security.MessageDigest
import java.util.Collections

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

    fun isDeviceRooted(context: Context): Boolean {
        val isDeviceRooted =
            isRootedBySuBinary() || isRootedByRootManagementApps(context) || isRootedByTestKeys() || isRootedByWritableSystem()
        try {
            if (!isDeviceRooted) {
                // Throw the custom exception immediately if a rooted device is detected
                throw CardSDKException(
                    context.getString(R.string.error_rooted_device),
                    CardSDKException.INSECURE_ENVIRONMENT
                )
            }
        } catch (e: CardSDKException) {
            e.printStackTrace()
            Toast.error(context.getString(R.string.error_rooted_device))
        }
        return false
    }

    fun deviceFingerprint(context: Context): String {
        val strIPAddress: String = getIPAddress()
        val modelName = Build.MODEL
        val device = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val androidVersion = Build.VERSION.RELEASE
        val deviceFingerprint = "$strIPAddress : $modelName : $device : $androidVersion"
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(deviceFingerprint.toByteArray())).toString(16)
            .padStart(32, '0')
    }

    private fun getIPAddress(): String {
        try {
            val interfaces: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            for (inter in interfaces) {
                val addresses: List<InetAddress> = Collections.list(inter.inetAddresses)
                for (address in addresses) {
                    if (!address.isLoopbackAddress) {
                        val sAddress = address.hostAddress
                        val isIPv4 = (sAddress?.indexOf(':') ?: 0) < 0
                        if (isIPv4) return sAddress ?: ""
                    }
                }
            }
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }
        return ""
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

    private fun isRootedByRootManagementApps(context: Context): Boolean {
        val packages = arrayOf(
            "eu.chainfire.supersu",
            "com.noshufou.android.su"
        )

        for (packageName in packages) {
            try {
                val info = context.packageManager.getApplicationInfo(
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