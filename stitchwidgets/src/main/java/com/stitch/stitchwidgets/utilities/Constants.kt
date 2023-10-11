package com.stitch.stitchwidgets.utilities

open class Constants {

    object ViewType {

        const val VIEW_CARD = "VIEW_CARD"
        const val ACTIVATE_CARD = "ACTIVATE_CARD"
        const val SET_CARD_PIN = "SET_CARD_PIN"
        const val RESET_CARD_PIN = "RESET_CARD_PIN"
    }

    object ParcelConstants {

        const val SDK_DATA = "sdk_data"
    }

    object CardState {

        const val ACTIVATED = "activated"
        const val INVALID = "invalid"
        const val SHIPPED = "shipped"

        const val ACTIVATE_CARD_STATE = "card shipped"
    }

    object APIConstants {

        const val X_CORRELATION_ID_VALUE = "5435436"
    }

    object HTTPMethod {

        const val POST = "POST"
        const val GET = "GET"
        const val PUT = "PUT"
    }

    object APIEndPoints {

        //Card APIs
        const val CARDS = "/v1/cards/"

        //Secure Widget APIs
        const val WIDGETS_SECURE_SESSION_KEY = "/sessionkey"
        const val WIDGETS_SECURE_CARD = "/card"
        const val SECURE_WIDGETS_ACTIVATE_CARD = "/card/activation"
        const val SECURE_WIDGETS_SET_PIN = "/setpin"
        const val SECURE_WIDGETS_CHANGE_PIN = "/changepin"
    }
}