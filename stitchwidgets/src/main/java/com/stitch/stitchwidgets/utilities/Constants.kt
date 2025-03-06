package com.stitch.stitchwidgets.utilities

open class Constants {

    object ViewType {

        const val SET_CARD_PIN = "SET_CARD_PIN"
        const val RESET_CARD_PIN = "RESET_CARD_PIN"
    }

    object ParcelConstants {

        const val SDK_DATA = "sdk_data"
        const val CARD_NUMBER = "card_number"
    }

    object CardState {

        const val ACTIVATED = "activated"

        const val ACTIVATE_CARD_STATE = "card shipped"
    }

    object HTTPMethod {

        const val GET = "GET"
    }

    object APIEndPoints {

        //Secure Widget APIs
        const val WIDGETS_SECURE_SESSION_KEY = "/v1/widgets/secure/sessionkey"
        const val WIDGETS_SECURE_CARD = "/v1/widgets/secure/card"
        const val SECURE_WIDGETS_SET_PIN = "/v1/widgets/secure/setpin"
        const val SECURE_WIDGETS_CHANGE_PIN = "/v1/widgets/secure/changepin"
    }
}