package com.stitch.stitchwidgets.ui

import android.content.Context
import android.util.Base64
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.stitch.stitchwidgets.R
import com.stitch.stitchwidgets.data.model.SDKData
import com.stitch.stitchwidgets.data.model.SavedCardSettings
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureChangePINRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSessionKeyRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSetPINRequest
import com.stitch.stitchwidgets.data.model.response.Card
import com.stitch.stitchwidgets.data.model.response.WidgetsSecureSessionKeyResponse
import com.stitch.stitchwidgets.data.remote.ApiManager
import com.stitch.stitchwidgets.utilities.Constants
import com.stitch.stitchwidgets.utilities.Toast
import com.stitch.stitchwidgets.utilities.validateConfirmPIN
import com.stitch.stitchwidgets.utilities.validateNewPIN
import com.stitch.stitchwidgets.utilities.validateOldPIN
import com.stitch.stitchwidgets.utilities.validatePIN
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


open class StitchWidgetViewModel : ViewModel() {

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
    val fingerprint = ObservableField("")
    val styleSheetType = ObservableField("")

    val oldPin = ObservableField("")
    val newPin = ObservableField("")
    val confirmChangePin = ObservableField("")
    val pin = ObservableField("")
    val confirmPin = ObservableField("")

    val showCardSetPin = ObservableField(false)
    val showCardResetPin = ObservableField(false)

    val cardStyleFontFamily = ObservableField<Int>()
    val cardStyleFontColor = ObservableField<Int>()
    val cardStyleButtonFontColor = ObservableField<Int>()
    val cardStyleButtonBackgroundColor = ObservableField<Int>()
    val styleFontSize = ObservableField("")

    val retryCount = ObservableField(0)

    lateinit var onResetPINClick: () -> Unit
    lateinit var onSetPINClick: () -> Unit
    lateinit var onResetPINSuccess: () -> Unit
    lateinit var onResetPINError: (errorCode: Int?, errorMessage: String?) -> Unit
    lateinit var onSetPINSuccess: () -> Unit

    lateinit var networkListener: () -> Boolean
    lateinit var progressBarListener: (isVisible: Boolean) -> Unit
    lateinit var logoutListener: (unAuth: Boolean) -> Unit
    lateinit var reFetchSessionToken: (viewType: String) -> Unit

    private lateinit var encryptionKey: String
    private fun pin(): String = encrypt(pin.get() ?: "", encryptionKey)
    private fun newPin(): String = encrypt(newPin.get() ?: "", encryptionKey)
    private fun oldPin(): String = encrypt(oldPin.get() ?: "", encryptionKey)

    fun getWidgetsSecureSessionKey(context: Context) {
        if (viewType.get() == Constants.ViewType.SET_CARD_PIN) {
            validateSetCardPin(context)
        }
        if (viewType.get() == Constants.ViewType.RESET_CARD_PIN) {
            validateResetCardPin(context)
        }
        val widgetsSecureSessionKeyRequest = WidgetsSecureSessionKeyRequest(
            token = secureToken.get() ?: "", deviceFingerprint = fingerprint.get() ?: "",
        )
        ApiManager.call(
            request = ApiManager.widgetSecureSessionKeyAsync(
                widgetsSecureSessionKeyRequest,
            ),
            response = {
                if (it != null) {
                    encryptionKey = it.key
                    callSetOrResetPinAPI(it)
                }
            },
            errorResponse = { errorCode, errorMessage ->
                handleSecureSessionKeyError(errorCode, errorMessage)
            },
            networkListener = networkListener,
            progressBarListener = progressBarListener,
            logoutListener = logoutListener,
        )
    }

    private fun validateSetCardPin(context: Context) {
        if (pin.validatePIN(context = context)) return
        if (confirmPin.validateConfirmPIN(context = context)) return
        if (pin.get() != confirmPin.get()) {
            Toast.error(context.getString(R.string.invalid_pin_mismatch))
            return
        }
    }

    private fun validateResetCardPin(context: Context) {
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

    private fun callSetOrResetPinAPI(widgetsSecureSessionKeyResponse: WidgetsSecureSessionKeyResponse) {
        when (viewType.get()) {

            Constants.ViewType.SET_CARD_PIN -> {
                getWidgetSecureSetPIN(widgetsSecureSessionKeyResponse.generatedKey)
            }

            Constants.ViewType.RESET_CARD_PIN -> {
                getWidgetSecureChangePIN(widgetsSecureSessionKeyResponse.generatedKey)
            }
        }
    }

    private fun handleSecureSessionKeyError(errorCode: Int?, errorMessage: String?) {
        if (errorCode == 400 &&
            (errorMessage?.contains("invalid", ignoreCase = true) == true ||
                    errorMessage?.contains("token", ignoreCase = true) == true) &&
            (retryCount.get() ?: 0) < 3
        ) {
            retryCount.set(retryCount.get()?.plus(1))
            reFetchSessionToken.invoke(viewType.get() ?: "")
        }
    }

    private fun getWidgetSecureSetPIN(token: String) {
        val widgetsSecureSetPINRequest = WidgetsSecureSetPINRequest(
            pin = pin(),
            token = token, deviceFingerprint = fingerprint.get() ?: "",
        )
        ApiManager.call(
            request = ApiManager.widgetSecureSetPINAsync(
                widgetsSecureSetPINRequest,
            ),
            response = {
                if (it != null) {
                    onSetPINSuccess.invoke()
                }
            },
            errorResponse = { errorCode, errorMessage ->
                handleSecureSetPinError(errorCode, errorMessage)
            },
            networkListener = networkListener,
            progressBarListener = progressBarListener,
            logoutListener = logoutListener,
        )
    }

    private fun handleSecureSetPinError(errorCode: Int?, errorMessage: String?) {
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
    }

    private fun getWidgetSecureChangePIN(token: String) {
        val widgetsSecureChangePINRequest = WidgetsSecureChangePINRequest(
            existingPin = oldPin(),
            pin = newPin(),
            token = token, deviceFingerprint = fingerprint.get() ?: "",
        )
        ApiManager.call(
            request = ApiManager.widgetSecureChangePINAsync(
                widgetsSecureChangePINRequest,
            ),
            response = {
                if (it != null) {
                    onResetPINSuccess.invoke()
                }
            },
            errorResponse = { errorCode, errorMessage ->
                handleSecureChangePinError(errorCode, errorMessage)
            },
            networkListener = networkListener,
            progressBarListener = progressBarListener,
            logoutListener = logoutListener,
        )
    }

    private fun handleSecureChangePinError(errorCode: Int?, errorMessage: String?) {
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

        return "$ivBase64.$encryptedText".replace("\n", "")
    }
}