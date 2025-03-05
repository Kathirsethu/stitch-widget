package com.stitch.stitchwidgets.data.remote

import com.stitch.stitchwidgets.data.model.request.WidgetsSecureCardRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureChangePINRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSessionKeyRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSetPINRequest
import com.stitch.stitchwidgets.data.model.response.WidgetsSecureCardResponse
import com.stitch.stitchwidgets.data.model.response.WidgetsSecureSessionKeyResponse
import com.stitch.stitchwidgets.utilities.Constants
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

sealed interface ApiHelper {

    @POST(Constants.APIEndPoints.WIDGETS_SECURE_SESSION_KEY)
    fun widgetSecureSessionKeyAsync(
        @Url baseUrl: String,
        @Body widgetsSecureSessionKeyRequest: WidgetsSecureSessionKeyRequest
    ): Deferred<Response<WidgetsSecureSessionKeyResponse>>

    @POST(Constants.APIEndPoints.WIDGETS_SECURE_CARD)
    fun widgetSecureCardAsync(
        @Url baseUrl: String,
        @Body widgetsSecureCardRequest: WidgetsSecureCardRequest
    ): Deferred<Response<WidgetsSecureCardResponse>>

    @POST(Constants.APIEndPoints.SECURE_WIDGETS_SET_PIN)
    fun widgetSecureSetPINAsync(
        @Url baseUrl: String,
        @Body widgetsSecureSetPINRequest: WidgetsSecureSetPINRequest
    ): Deferred<Response<ResponseBody>>

    @POST(Constants.APIEndPoints.SECURE_WIDGETS_CHANGE_PIN)
    fun widgetSecureChangePINAsync(
        @Url baseUrl: String,
        @Body widgetsSecureChangePINRequest: WidgetsSecureChangePINRequest
    ): Deferred<Response<ResponseBody>>
}