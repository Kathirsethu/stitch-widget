package com.stitch.stitchwidgets.utilities

class CardSDKException(message: String?, private val errorCode: Int) : Exception(message) {
    companion object {
        const val INSECURE_ENVIRONMENT: Int = 1001
        const val INSECURE_ENVIRONMENT_MESSAGE: String =
            "Insecure environment detected. Please use a secure device."
    }

    fun getErrorCode(): Int {
        return errorCode
    }
}
