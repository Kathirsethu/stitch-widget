package com.stitch.stitchwidgets.ui

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Base64
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stitch.stitchwidgets.R
import com.stitch.stitchwidgets.data.model.SDKData
import com.stitch.stitchwidgets.data.model.SavedCardSettings
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureChangePINRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSessionKeyRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSetPINRequest
import com.stitch.stitchwidgets.data.model.response.Card
import com.stitch.stitchwidgets.data.remote.ApiManager
import com.stitch.stitchwidgets.utilities.Constants
import com.stitch.stitchwidgets.utilities.Toast
import com.stitch.stitchwidgets.utilities.validateConfirmPIN
import com.stitch.stitchwidgets.utilities.validateNewPIN
import com.stitch.stitchwidgets.utilities.validateOldPIN
import com.stitch.stitchwidgets.utilities.validatePIN
import java.io.File
import java.math.BigInteger
import java.net.InetAddress
import java.net.NetworkInterface
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Collections
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


open class CardManagementSDKViewModel : ViewModel() {

    val sdkData = ObservableField<SDKData>()
    val savedCardSettings = ObservableField<SavedCardSettings>()
    val card = ObservableField<Card>()
    val tokenType = ObservableField("")
    val apiToken = ObservableField("")
    val authToken = ObservableField("")
    val cardNumber = ObservableField("")
    val customerNumber = ObservableField("")
    val programName = ObservableField("")
    val viewType = ObservableField("")
    val secureToken = ObservableField("")
    val fingerPrint = ObservableField("")
    val style = ObservableField("")
    val isFront = MutableLiveData(true)

    val accountNumber = ObservableField("")
    val accountNumberBack = ObservableField("")
    val cardCVV = ObservableField("")
    val cardCVVBack = ObservableField("")
    val nameOnCard = ObservableField("")
    val cardExpiry = ObservableField("")
    val oldPin = ObservableField("")
    val newPin = ObservableField("")
    val confirmChangePin = ObservableField("")
    val pin = ObservableField("")
    val confirmPin = ObservableField("")
    val cardTypeImage = ObservableField(R.drawable.ic_visa)

    val showCardSetPin = ObservableField(false)
    val showCardResetPin = ObservableField(false)
    val showCardState = ObservableField(false)

    val cardStyleFontFamily = ObservableField<Int>()
    val cardStyleFontColor = ObservableField<Int>()
    val cardStyleButtonFontColor = ObservableField<Int>()
    val cardStyleButtonBackgroundColor = ObservableField<Int>()
    val styleFontSize = ObservableField("")
    val cardStyleBackground = ObservableField<Any>()
    val cardStyleNumberTopPadding = ObservableField("0")
    val cardStyleNumberBottomPadding = ObservableField("0")
    val cardStyleNumberStartPadding = ObservableField("0")
    val cardStyleNumberEndPadding = ObservableField("0")
    val cardStyleExpiryTopPadding = ObservableField("0")
    val cardStyleExpiryBottomPadding = ObservableField("0")
    val cardStyleExpiryStartPadding = ObservableField("0")
    val cardStyleExpiryEndPadding = ObservableField("0")
    val cardStyleCVVTopPadding = ObservableField("0")
    val cardStyleCVVBottomPadding = ObservableField("0")
    val cardStyleCVVStartPadding = ObservableField("0")
    val cardStyleCVVEndPadding = ObservableField("0")
    val cardMediaFile = ObservableField<File>()
    val isCardNumberMasked = ObservableField(false)
    val isCardCVVMasked = ObservableField(false)

    val isCardNotActivate = ObservableField(false)
    val isCardActivated = ObservableField(false)
    val isCardInvalid = ObservableField(false)

    val retryCount = ObservableField(0)

    lateinit var onShowMaskedCardNumberClick: () -> Unit
    lateinit var onShowMaskedCardCVVClick: () -> Unit
    lateinit var onActivateCardClick: () -> Unit
    lateinit var onResetPINClick: () -> Unit
    lateinit var onSetPINClick: () -> Unit
    lateinit var onResetPINSuccess: () -> Unit
    lateinit var onResetPINError: (errorCode: Int?, errorMessage: String?) -> Unit
    lateinit var onSetPINSuccess: () -> Unit

    lateinit var networkListener: () -> Boolean
    lateinit var progressBarListener: (isVisible: Boolean) -> Unit
    lateinit var logoutListener: (unAuth: Boolean) -> Unit
    lateinit var reFetchSessionToken: (viewType: String) -> Unit

    fun getWidgetsSecureSessionKey(context: Context) {
        if (viewType.get() == Constants.ViewType.SET_CARD_PIN) {
            if (pin.validatePIN(context = context)) return
            if (confirmPin.validateConfirmPIN(context = context)) return
            if (pin.get() != confirmPin.get()) {
                Toast.error(context.getString(R.string.invalid_pin_mismatch))
                return
            }
        }
        if (viewType.get() == Constants.ViewType.RESET_CARD_PIN) {
            if (oldPin.validateOldPIN(context = context)) return
            if (newPin.validateNewPIN(context = context)) return
            if (confirmChangePin.validateConfirmPIN(context = context)) return
            if (oldPin.get() == newPin.get()) {
                Toast.error(context.getString(R.string.invalid_change_pin_mismatch))
                return
            }
            if (newPin.get() != confirmChangePin.get()) {
                Toast.error(context.getString(R.string.invalid_pin_mismatch))
                return
            }
        }
        val widgetsSecureSessionKeyRequest = WidgetsSecureSessionKeyRequest(
            token = secureToken.get() ?: "", deviceFingerprint = fingerPrint.get() ?: "",
        )
        ApiManager.call(
            toast = false,
            request = ApiManager.widgetSecureSessionKeyAsync(widgetsSecureSessionKeyRequest),
            response = {
                if (it != null) {
                    when (viewType.get()) {

                        Constants.ViewType.SET_CARD_PIN -> {
                            getWidgetSecureSetPIN(it.key, it.generatedKey)
                        }

                        Constants.ViewType.RESET_CARD_PIN -> {
                            getWidgetSecureChangePIN(it.key, it.generatedKey)
                        }
                    }
                }
            },
            errorResponse = { errorCode, errorMessage ->
                if (errorCode == 400 &&
                    (errorMessage?.contains("invalid", ignoreCase = true) == true ||
                            errorMessage?.contains("token", ignoreCase = true) == true) &&
                    (retryCount.get() ?: 0) < 3
                ) {
                    retryCount.set(retryCount.get()?.plus(1))
                    reFetchSessionToken.invoke(viewType.get() ?: "")
                }
            },
            networkListener = networkListener,
            progressBarListener = progressBarListener,
            logoutListener = logoutListener,
        )
    }

    private fun getWidgetSecureSetPIN(key: String, token: String) {
        val widgetsSecureSetPINRequest = WidgetsSecureSetPINRequest(
            pin = encrypt(pin.get() ?: "", key).replace("\n", ""),
            token = token, deviceFingerprint = fingerPrint.get() ?: "",
        )
        ApiManager.call(
            toast = false,
            request = ApiManager.widgetSecureSetPINAsync(widgetsSecureSetPINRequest),
            response = {
                if (it != null) {
                    onSetPINSuccess.invoke()
                }
            },
            errorResponse = { errorCode, errorMessage ->
                if (errorCode == 400 &&
                    (errorMessage?.contains("invalid", ignoreCase = true) == true ||
                            errorMessage?.contains("token", ignoreCase = true) == true) &&
                    (retryCount.get() ?: 0) < 3
                ) {
                    retryCount.set(retryCount.get()?.plus(1))
                    reFetchSessionToken.invoke(viewType.get() ?: "")
                } else {
                    Toast.error(errorMessage ?: "")
                }
            },
            networkListener = networkListener,
            progressBarListener = progressBarListener,
            logoutListener = logoutListener,
        )
    }

    private fun getWidgetSecureChangePIN(key: String, token: String) {
        val widgetsSecureChangePINRequest = WidgetsSecureChangePINRequest(
            existingPin = encrypt(oldPin.get() ?: "", key).replace("\n", ""),
            pin = encrypt(newPin.get() ?: "", key).replace("\n", ""),
            token = token, deviceFingerprint = fingerPrint.get() ?: "",
        )
        ApiManager.call(
            toast = false,
            request = ApiManager.widgetSecureChangePINAsync(widgetsSecureChangePINRequest),
            response = {
                if (it != null) {
                    onResetPINSuccess.invoke()
                }
            },
            errorResponse = { errorCode, errorMessage ->
                if (errorCode == 400 &&
                    (errorMessage?.contains("invalid", ignoreCase = true) == true ||
                            errorMessage?.contains("token", ignoreCase = true) == true) &&
                    (retryCount.get() ?: 0) < 3
                ) {
                    retryCount.set(retryCount.get()?.plus(1))
                    reFetchSessionToken.invoke(viewType.get() ?: "")
                } else {
                    Toast.error(errorMessage ?: "")
                }
                onResetPINError.invoke(errorCode, errorMessage)
            },
            networkListener = networkListener,
            progressBarListener = progressBarListener,
            logoutListener = logoutListener,
        )
    }

    private fun decrypt(encryptedText: String?, key: String): String {
        val keyBytes = Base64.decode(key, Base64.DEFAULT)
        val secretKey: SecretKey = SecretKeySpec(keyBytes, "AES")
        val parts = encryptedText?.split("\\.".toRegex())?.dropLastWhile { it.isEmpty() }
            ?.toTypedArray()
        require(parts?.size == 2) { "Invalid input format" }
        //decoding iv
        val ivBytes: ByteArray = Base64.decode(parts?.get(0), Base64.DEFAULT)
        //decoding data
        val encryptedBytes: ByteArray = Base64.decode(parts?.get(1), Base64.DEFAULT)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val ivParameterSpec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        val cipherText = cipher.doFinal(encryptedBytes)

        return String(cipherText)
    }

    private fun encrypt(pin: String, key: String): String {
        val keyBytes = Base64.decode(key, Base64.DEFAULT)
        val secretKey: SecretKey = SecretKeySpec(keyBytes, "AES")

        val ivBytes = ByteArray(12)
        SecureRandom().nextBytes(ivBytes)
        val ivBase64 = Base64.encodeToString(ivBytes, Base64.DEFAULT)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val ivParameterSpec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)

        val encryptedBytes = cipher.doFinal(pin.toByteArray())
        val encryptedText = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)

        return "$ivBase64.$encryptedText"
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
}