package com.stitch.cardmanagement.data.remote

import com.stitch.cardmanagement.BuildConfig
import com.stitch.cardmanagement.data.model.request.CommonGetRequest
import com.stitch.cardmanagement.data.model.request.WidgetsSecureActivateCardRequest
import com.stitch.cardmanagement.data.model.request.WidgetsSecureCardRequest
import com.stitch.cardmanagement.data.model.request.WidgetsSecureChangePINRequest
import com.stitch.cardmanagement.data.model.request.WidgetsSecureSessionKeyRequest
import com.stitch.cardmanagement.data.model.request.WidgetsSecureSetPINRequest
import com.stitch.cardmanagement.data.model.response.Card
import com.stitch.cardmanagement.data.model.response.WidgetsSecureCardResponse
import com.stitch.cardmanagement.data.model.response.WidgetsSecureSessionKeyResponse
import com.stitch.cardmanagement.utilities.Constants
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

sealed interface ApiHelper {

    @POST(BuildConfig.API_VERSION)
    fun cardsAsync(
        @Body commonGetRequest: CommonGetRequest, @Header("Authorization") auth: String
    ): Deferred<Response<List<Card>>>

    @POST(BuildConfig.WIDGETS_SECURE_API_VERSION + Constants.APIEndPoints.WIDGETS_SECURE_SESSION_KEY)
    fun widgetSecureSessionKeyAsync(@Body widgetsSecureSessionKeyRequest: WidgetsSecureSessionKeyRequest): Deferred<Response<WidgetsSecureSessionKeyResponse>>

    @POST(BuildConfig.WIDGETS_SECURE_API_VERSION + Constants.APIEndPoints.WIDGETS_SECURE_CARD)
    fun widgetSecureCardAsync(@Body widgetsSecureCardRequest: WidgetsSecureCardRequest): Deferred<Response<WidgetsSecureCardResponse>>

    @POST(BuildConfig.WIDGETS_SECURE_API_VERSION + Constants.APIEndPoints.SECURE_WIDGETS_ACTIVATE_CARD)
    fun widgetSecureActivateCardAsync(@Body widgetsSecureActivateCardRequest: WidgetsSecureActivateCardRequest): Deferred<Response<ResponseBody>>

    @POST(BuildConfig.WIDGETS_SECURE_API_VERSION + Constants.APIEndPoints.SECURE_WIDGETS_SET_PIN)
    fun widgetSecureSetPINAsync(@Body widgetsSecureSetPINRequest: WidgetsSecureSetPINRequest): Deferred<Response<ResponseBody>>

    @POST(BuildConfig.WIDGETS_SECURE_API_VERSION + Constants.APIEndPoints.SECURE_WIDGETS_CHANGE_PIN)
    fun widgetSecureChangePINAsync(@Body widgetsSecureChangePINRequest: WidgetsSecureChangePINRequest): Deferred<Response<ResponseBody>>
}