package com.stitch.cardmanagement.utilities

sealed interface Networking {

    sealed interface InternalHttpCode {
        companion object {
            const val SUCCESS = 200
            const val BAD_REQUEST = 400
            const val NOT_FOUND = 404
            const val UNAUTHORIZED_ACCESS = 401
            const val TIMEOUT_ERROR = 408
            const val UN_PROCESSABLE_ENTITY = 422
            const val TOO_MANY_REQUEST = 429
            const val INTERNAL_SERVER_ERROR = 500
            const val SERVICE_UNAVAILABLE = 502
            const val BAD_GATEWAY = 503
        }
    }

    sealed interface HttpErrorMessage {
        companion object {
            const val UNAUTHORIZED_ACCESS = "Your session has expired. Please Login."
            const val INTERNAL_SERVER_ERROR = "OOPS!!! Something went wrong"
            const val TIMEOUT_ERROR = "OOPS!!! Please Try again Later"
            const val NOT_FOUND = "OOPS!!! Not found"
            const val SERVICE_UNAVAILABLE =
                "Sorry, our servers are busy right now. Please try again in few mins."
            const val NO_NETWORK_FOUND = "No Network Found"
            const val CONNECT_ERROR = "OOPS!!! Please check your internet connection"
        }
    }
}