package com.stitch.stitchwidgets.utilities

class CardSDKException(message: String?, private val errorCode: Int) : Exception(message) {
    companion object {
        const val INSECURE_ENVIRONMENT: Int = 1001
    }

    fun getErrorCode(): Int {
        return errorCode
    }
}
