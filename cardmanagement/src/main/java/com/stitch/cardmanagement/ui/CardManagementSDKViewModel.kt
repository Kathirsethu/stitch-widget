package com.stitch.cardmanagement.ui

import android.content.Context
import android.util.Base64
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stitch.cardmanagement.R
import com.stitch.cardmanagement.data.model.SDKData
import com.stitch.cardmanagement.data.model.SavedCardSettings
import com.stitch.cardmanagement.data.model.request.CommonGetRequest
import com.stitch.cardmanagement.data.model.request.WidgetsSecureActivateCardRequest
import com.stitch.cardmanagement.data.model.request.WidgetsSecureCardRequest
import com.stitch.cardmanagement.data.model.request.WidgetsSecureChangePINRequest
import com.stitch.cardmanagement.data.model.request.WidgetsSecureSessionKeyRequest
import com.stitch.cardmanagement.data.model.request.WidgetsSecureSetPINRequest
import com.stitch.cardmanagement.data.model.response.Card
import com.stitch.cardmanagement.data.remote.ApiManager
import com.stitch.cardmanagement.utilities.CardUtils
import com.stitch.cardmanagement.utilities.Constants
import com.stitch.cardmanagement.utilities.Toast
import com.stitch.cardmanagement.utilities.validateCVV
import com.stitch.cardmanagement.utilities.validateConfirmPIN
import com.stitch.cardmanagement.utilities.validateNewPIN
import com.stitch.cardmanagement.utilities.validateOldPIN
import com.stitch.cardmanagement.utilities.validatePIN
import java.io.File
import java.security.SecureRandom
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
    val pin = ObservableField("")
    val confirmPin = ObservableField("")
    val cardTypeImage = ObservableField(R.drawable.ic_visa)

    val showCardSetPin = ObservableField(false)
    val showCardResetPin = ObservableField(false)
    val showCardState = ObservableField(false)

    val cardStyleFontFamily = ObservableField<Int>()
    val cardStyleFontColor = ObservableField<Int>()
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
    lateinit var onActivateCardSuccess: () -> Unit
    lateinit var onResetPINSuccess: () -> Unit
    lateinit var onSetPINSuccess: () -> Unit

    lateinit var networkListener: () -> Boolean
    lateinit var progressBarListener: (isVisible: Boolean) -> Unit
    lateinit var logoutListener: (unAuth: Boolean) -> Unit
    lateinit var reFetchSessionToken: (viewType: String) -> Unit

    fun getCards() {
        val cardsRequest = CommonGetRequest(
            endPoint = Constants.APIEndPoints.CARDS + customerNumber.get(),
            token = "${tokenType.get()} ${apiToken.get()}",
        )
        ApiManager.call(
            progress = false,
            toast = false,
            request = ApiManager.cardsAsync(cardsRequest, "${tokenType.get()} ${authToken.get()}"),
            response = { cardList ->
                cardList?.let {
                    card.set(it.find { card ->
                        card.cardNumber == cardNumber.get()
                    })
                    if (card.get() != null) {
                        sdkData.get()?.card = card.get()
                        isCardActivated.set(card.get()?.state == Constants.CardState.ACTIVATED)
                        isCardInvalid.set(card.get()?.state == Constants.CardState.INVALID)
                        isCardNotActivate.set(card.get()?.state != Constants.CardState.ACTIVATED)
                        showCardState.set(true)
                        showCardSetPin.set(true)
                        showCardResetPin.set(true)
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
            if (oldPin.get() == newPin.get()) {
                Toast.error(context.getString(R.string.invalid_change_pin_mismatch))
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
                        Constants.ViewType.VIEW_CARD -> {
                            getWidgetsSecureCard(it.generatedKey)
                        }

                        Constants.ViewType.ACTIVATE_CARD -> {
                            getWidgetSecureActivateCard(context)
                        }

                        Constants.ViewType.SET_CARD_PIN -> {
                            getWidgetSecureSetPIN(it.generatedKey)
                        }

                        Constants.ViewType.RESET_CARD_PIN -> {
                            getWidgetSecureChangePIN(it.generatedKey)
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

    private fun getWidgetsSecureCard(generatedKey: String) {
        val widgetsSecureCardRequest = WidgetsSecureCardRequest(
            token = secureToken.get() ?: "", deviceFingerprint = fingerPrint.get() ?: "",
        )
        ApiManager.call(
            toast = false,
            request = ApiManager.widgetSecureCardAsync(widgetsSecureCardRequest),
            response = {
                if (it != null) {
                    accountNumber.set(
                        CardUtils.getCardNumber(decrypt(it.accountNumber, generatedKey))
                    )
                    cardExpiry.set(
                        CardUtils.getCardExpiry(decrypt(it.expiry, generatedKey))
                    )
                    cardCVV.set(decrypt(it.cvv, generatedKey))
                    cardTypeImage.set(
                        CardUtils.getCardType(accountNumber.get() ?: "")
                    )
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

    private fun getWidgetSecureActivateCard(context: Context) {
        if (cardCVV.validateCVV(context = context)) return
        val widgetsSecureActivateCardRequest =
            WidgetsSecureActivateCardRequest(
                deviceFingerprint = fingerPrint.get() ?: "",
                token = secureToken.get() ?: ""
            )
        ApiManager.call(
            toast = false,
            request = ApiManager.widgetSecureActivateCardAsync(widgetsSecureActivateCardRequest),
            response = {
                if (it != null) {
                    onActivateCardSuccess.invoke()
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

    private fun getWidgetSecureSetPIN(key: String) {
        val widgetsSecureSetPINRequest = WidgetsSecureSetPINRequest(
            pin = encrypt(pin.get() ?: "", key).replace("\n", ""),
            token = secureToken.get() ?: "", deviceFingerprint = fingerPrint.get() ?: "",
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

    private fun getWidgetSecureChangePIN(key: String) {
        val widgetsSecureChangePINRequest = WidgetsSecureChangePINRequest(
            existingPin = encrypt(oldPin.get() ?: "", key).replace("\n", ""),
            pin = encrypt(newPin.get() ?: "", key).replace("\n", ""),
            token = secureToken.get() ?: "", deviceFingerprint = fingerPrint.get() ?: "",
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

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        val cipherText = cipher.doFinal(encryptedBytes)

        return String(cipherText)
    }

    private fun encrypt(pin: String, key: String): String {
        val keyBytes = Base64.decode(key, Base64.DEFAULT)
        val secretKey: SecretKey = SecretKeySpec(keyBytes, "AES")

        val ivBytes = ByteArray(16)
        SecureRandom().nextBytes(ivBytes)
        val ivBase64 = Base64.encodeToString(ivBytes, Base64.DEFAULT)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)

        val encryptedBytes = cipher.doFinal(pin.toByteArray())
        val encryptedText = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)

        return "$ivBase64.$encryptedText"
    }
}