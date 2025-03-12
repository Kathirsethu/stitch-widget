package com.stitch.stitchwidgets.data.remote

import com.stitch.stitchwidgets.data.model.request.WidgetsSecureCardRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureChangePINRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSessionKeyRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSetPINRequest
import com.stitch.stitchwidgets.data.model.response.WidgetsSecureCardResponse
import com.stitch.stitchwidgets.data.model.response.WidgetsSecureSessionKeyResponse
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

sealed interface ApiHelper {

    @POST
    fun widgetSecureSessionKeyAsync(
        @Url versionedPath: String,
        @Body widgetsSecureSessionKeyRequest: WidgetsSecureSessionKeyRequest
    ): Deferred<Response<WidgetsSecureSessionKeyResponse>>

    @POST
    fun widgetSecureCardAsync(
        @Url versionedPath: String,
        @Body widgetsSecureCardRequest: WidgetsSecureCardRequest
    ): Deferred<Response<WidgetsSecureCardResponse>>

    @POST
    fun widgetSecureSetPINAsync(
        @Url versionedPath: String,
        @Body widgetsSecureSetPINRequest: WidgetsSecureSetPINRequest
    ): Deferred<Response<ResponseBody>>

    @POST
    fun widgetSecureChangePINAsync(
        @Url versionedPath: String,
        @Body widgetsSecureChangePINRequest: WidgetsSecureChangePINRequest
    ): Deferred<Response<ResponseBody>>
}