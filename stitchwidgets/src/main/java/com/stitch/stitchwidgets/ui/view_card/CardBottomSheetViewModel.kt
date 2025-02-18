package com.stitch.stitchwidgets.ui.view_card

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Base64
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureCardRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSessionKeyRequest
import com.stitch.stitchwidgets.data.model.response.Card
import com.stitch.stitchwidgets.data.remote.ApiManager
import com.stitch.stitchwidgets.utilities.CardUtils
import java.math.BigInteger
import java.net.InetAddress
import java.net.NetworkInterface
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CardBottomSheetViewModel : ViewModel() {

    var card = Card()
    var cardNumber: String = ""
    var secureToken: String = ""
    var isCardNumberMaskEnabled = ObservableField(true)
    var isCardCVVMaskEnabled = ObservableField(true)
    var isCardNumberMasked = ObservableField(true)
    var isCardCVVMasked = ObservableField(true)
    var isCardNumberEye = ObservableField(true)
    var isCVVEye = ObservableField(true)
    private val retryCount = ObservableField(0)
    lateinit var setCardData: () -> Unit

    lateinit var networkListener: () -> Boolean
    lateinit var progressBarListener: (isVisible: Boolean) -> Unit
    lateinit var logoutListener: (unAuth: Boolean) -> Unit

    fun deviceFingerPrint(context: Context): String {
        val strIPAddress: String = getIPAddress()
        val modelName = Build.MODEL
        val device = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val androidVersion = Build.VERSION.RELEASE
        val deviceFingerPrint = "$strIPAddress : $modelName : $device : $androidVersion"
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(deviceFingerPrint.toByteArray())).toString(16)
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

    fun getWidgetSecureSessionKey(deviceFingerPrint: String) {
        val widgetsSecureSessionKeyRequest = WidgetsSecureSessionKeyRequest(
            token = secureToken, deviceFingerprint = deviceFingerPrint,
        )
        ApiManager.call(
            request = ApiManager.widgetSecureSessionKeyAsync(
                widgetsSecureSessionKeyRequest
            ),
            response = {
                if (it != null) {
                    getWidgetsSecureCard(it.generatedKey, it.key, deviceFingerPrint)
                }
            },
            errorResponse = { errorCode, errorMessage ->
                if (errorCode == 400 &&
                    (errorMessage?.contains("invalid", ignoreCase = true) == true ||
                            errorMessage?.contains("token", ignoreCase = true) == true) &&
                    (retryCount.get() ?: 0) < 3
                ) {
                    retryCount.set(retryCount.get()?.plus(1))
                }
            },
            networkListener = networkListener,
            progressBarListener = progressBarListener,
            logoutListener = logoutListener,
        )
    }

    private fun getWidgetsSecureCard(
        token: String,
        generatedKey: String,
        deviceFingerPrint: String
    ) {
        val widgetsSecureCardRequest = WidgetsSecureCardRequest(
            token = token, deviceFingerprint = deviceFingerPrint
        )
        ApiManager.call(
            request = ApiManager.widgetSecureCardAsync(
                widgetsSecureCardRequest
            ),
            response = {
                if (it != null) {
                    card = (Card(
                        cardNumber = CardUtils.getCardNumber(it.items.cardId ?: ""),
                        cvv2 = decrypt(it.items.cvv2, generatedKey),
                        expiry = getCardExpiry(decrypt(it.items.expiry, generatedKey)),
                        state = "activated"
                    ))
                    setCardData.invoke()
                }
            },
            errorResponse = { errorCode, errorMessage ->
                if (errorCode == 400 &&
                    (errorMessage?.contains("invalid", ignoreCase = true) == true ||
                            errorMessage?.contains("token", ignoreCase = true) == true) &&
                    (retryCount.get() ?: 0) < 3
                ) {
                    retryCount.set(retryCount.get()?.plus(1))
                }
            },
            networkListener = networkListener,
            progressBarListener = progressBarListener,
            logoutListener = logoutListener,
        )
    }

    private fun getCardExpiry(cardExpiry: String): String {
        val expiryDate = SimpleDateFormat("yyMM", Locale.getDefault()).parse(cardExpiry)
        val displayDate = SimpleDateFormat("MM/yy", Locale.getDefault())
        return expiryDate?.let { displayDate.format(it) } ?: ""
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
}