package com.stitch.cardmanagement.data.model.request

import com.google.gson.annotations.SerializedName
import com.stitch.cardmanagement.utilities.Constants

class WidgetsSecureActivateCardRequest(
    var state: String = Constants.CardState.ACTIVATED,
    var memo: String = Constants.CardState.ACTIVATE_CARD_STATE,
    var token: String,
    @SerializedName("deviceFingerPrint")
    var deviceFingerprint: String,
)