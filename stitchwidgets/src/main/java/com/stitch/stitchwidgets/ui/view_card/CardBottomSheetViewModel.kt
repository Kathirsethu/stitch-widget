package com.stitch.stitchwidgets.ui.view_card

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSessionKeyRequest
import com.stitch.stitchwidgets.data.model.response.Card
import com.stitch.stitchwidgets.data.remote.ApiManager

class CardBottomSheetViewModel : ViewModel() {

    var card = Card()
    var secureToken: String = ""
    var isCardNumberMaskEnabled = ObservableField(true)
    var isCardCVVMaskEnabled = ObservableField(true)
    var isCardNumberMasked = ObservableField(true)
    var isCardCVVMasked = ObservableField(true)
    lateinit var onSetPinClick: () -> Unit
    lateinit var onChangePinClick: () -> Unit
    val retryCount = ObservableField(0)

    lateinit var networkListener: () -> Boolean
    lateinit var progressBarListener: (isVisible: Boolean) -> Unit
    lateinit var logoutListener: (unAuth: Boolean) -> Unit

    private fun getWsSecureSessionKey(token: String, totalCount: Int) {
        val widgetsSecureSessionKeyRequest = WidgetsSecureSessionKeyRequest(
            token = token, deviceFingerprint = "deviceFingerPrint",
        )
        ApiManager.call(
            request = ApiManager.widgetSecureSessionKeyAsync(
                widgetsSecureSessionKeyRequest
            ),
            response = {
                if (it != null) {
                    /*getDemoWidgetsSecureCard(it.generatedKey, it.key, totalCount)*/
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

    /*private fun getDemoWidgetsSecureCard(token: String, generatedKey: String, totalCount: Int) {
        val widgetsSecureCardRequest = WidgetsSecureCardRequest(
            token = token, deviceFingerprint = deviceFingerPrint,
        )
        ApiManager.call(
            request = ApiManager.widgetSecureCardAsync(
                widgetsSecureCardRequest
            ),
            response = {
                if (it != null) {
                    val cardData = it.items
                    cardsList.add(
                        Card(
                            cardNumber = CardUtils.getCardNumber(
                                decrypt(
                                    cardData.cardNumber,
                                    generatedKey
                                )
                            ),
                            panFirst6 = cardData.panFirstSix,
                            panLast4 = cardData.panLastFour,
                            sequenceNumber = cardData.sequenceNumber.toInt(),
                            embossedName = cardData.embossedName,
                            cvv2 = decrypt(cardData.cvv2, generatedKey),
                            expiry = getCardExpiry(decrypt(cardData.expiry, generatedKey)),
                            state = "activated",
                            cardId = cardData.cardId
                        )
                    )
                    if (cardsList.size == totalCount) {
                        setCardData(generatedKey)
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
                }
            },
            networkListener = networkListener,
            progressBarListener = progressBarListener,
            logoutListener = logoutListener,
        )
    }*/
}