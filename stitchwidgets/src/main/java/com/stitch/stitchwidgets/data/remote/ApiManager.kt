package com.stitch.stitchwidgets.data.remote

import com.google.gson.Gson
import com.stitch.stitchwidgets.WidgetSDK
import com.stitch.stitchwidgets.data.model.BaseResponse
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureCardRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureChangePINRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSessionKeyRequest
import com.stitch.stitchwidgets.data.model.request.WidgetsSecureSetPINRequest
import com.stitch.stitchwidgets.data.model.response.WidgetsSecureCardResponse
import com.stitch.stitchwidgets.data.model.response.WidgetsSecureSessionKeyResponse
import com.stitch.stitchwidgets.utilities.Constants
import com.stitch.stitchwidgets.utilities.Toast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.stitch.stitchwidgets.data.remote.RetrofitFactory.apiWidget as ApiWidget
import com.stitch.stitchwidgets.utilities.Networking.HttpErrorMessage as HttpMsg
import com.stitch.stitchwidgets.utilities.Networking.InternalHttpCode.Companion as HttpCode

object ApiManager : ApiHelper {

    @Suppress("UNCHECKED_CAST")
    fun <T> call(
        request: Deferred<Response<T>>,
        progress: Boolean = true,
        toast: Boolean = true,
        response: (T?) -> Unit = ::println,
        errorResponse: (Int?, String?) -> Unit,
        networkListener: () -> Boolean,
        progressBarListener: (isVisible: Boolean) -> Unit,
        logoutListener: (unAuth: Boolean) -> Unit,
    ): Job {
        callScope = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                try {
                    // Network Check
                    if (networkListener.invoke().not()) return@withContext
                    // Original Process
                    if (progress) progressBarListener.invoke(true)
                    with(request.await()) {
                        body()?.let {
                            when (it) {
                                is BaseResponse -> {
                                    if (toast) Toast.error(it.message ?: "")
                                    response(it)
                                }

                                else -> response(it)
                            }
                        }
                        errorBody()?.let {
                            val errorMessage: BaseResponse? = Gson().fromJson(
                                it.string(),
                                BaseResponse::class.java
                            )
                            errorResponse(code(), errorMessage?.message)
                            when (code()) {
                                HttpCode.INTERNAL_SERVER_ERROR, HttpCode.SERVICE_UNAVAILABLE, HttpCode.BAD_GATEWAY -> errorToast(
                                    HttpMsg.SERVICE_UNAVAILABLE
                                )

                                HttpCode.TIMEOUT_ERROR, HttpCode.TOO_MANY_REQUEST -> errorToast(
                                    HttpMsg.TIMEOUT_ERROR
                                )

                                HttpCode.NOT_FOUND -> errorToast(HttpMsg.NOT_FOUND)
                                HttpCode.UNAUTHORIZED_ACCESS -> {
                                    logoutListener.invoke(true)
                                    errorToast(HttpMsg.UNAUTHORIZED_ACCESS)
                                }

                                else -> {
                                    Gson().fromJson(
                                        it.string(),
                                        BaseResponse::class.java
                                    )?.let { baseResponse ->
                                        if (toast) Toast.error(
                                            baseResponse.message ?: baseResponse.errors()
                                        )
                                        try {
                                            response(baseResponse as T)
                                        } catch (e: ClassCastException) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }
                            response(null)
                        }
                    }
                } catch (e: Exception) {
                    when (e) {
                        is SocketTimeoutException -> errorToast(HttpMsg.TIMEOUT_ERROR)
                        is UnknownHostException -> errorToast(HttpMsg.INTERNAL_SERVER_ERROR + " Please contact admin...")
                        is ConnectException -> errorToast(HttpMsg.CONNECT_ERROR)
                        is CancellationException -> {}
//                        else -> errorToast(e.message.toString())
                    }
                }
                if (progress) progressBarListener.invoke(false)
            }
        }
        return callScope
    }

    private var callScope: Job = Job()
    fun clear() {
        callScope.cancelChildren()
        callScope.cancel()
    }

    private fun errorToast(error: String) = Toast.error(error)

    override fun widgetSecureSessionKeyAsync(
        versionedPath: String,
        widgetsSecureSessionKeyRequest: WidgetsSecureSessionKeyRequest
    ): Deferred<Response<WidgetsSecureSessionKeyResponse>> =
        ApiWidget.widgetSecureSessionKeyAsync(
            versionedPath,
            widgetsSecureSessionKeyRequest
        )

    override fun widgetSecureCardAsync(
        versionedPath: String,
        widgetsSecureCardRequest: WidgetsSecureCardRequest
    ): Deferred<Response<WidgetsSecureCardResponse>> =
        ApiWidget.widgetSecureCardAsync(
            versionedPath,
            widgetsSecureCardRequest
        )

    override fun widgetSecureSetPINAsync(
        versionedPath: String,
        widgetsSecureSetPINRequest: WidgetsSecureSetPINRequest
    ): Deferred<Response<ResponseBody>> =
        ApiWidget.widgetSecureSetPINAsync(
            versionedPath,
            widgetsSecureSetPINRequest
        )

    override fun widgetSecureChangePINAsync(
        versionedPath: String,
        widgetsSecureChangePINRequest: WidgetsSecureChangePINRequest
    ): Deferred<Response<ResponseBody>> =
        ApiWidget.widgetSecureChangePINAsync(
            versionedPath,
            widgetsSecureChangePINRequest
        )
}