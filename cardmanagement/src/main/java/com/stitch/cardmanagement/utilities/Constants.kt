package com.stitch.cardmanagement.utilities

open class Constants {

    object ViewType {

        const val VIEW_CARD = "VIEW_CARD"
        const val ACTIVATE_CARD = "ACTIVATE_CARD"
        const val SET_CARD_PIN = "SET_CARD_PIN"
        const val RESET_CARD_PIN = "RESET_CARD_PIN"
    }

    object ParcelConstants {

        const val SDK_DATA = "sdk_data"

        const val TITLE = "title"
        const val CARDS = "cards"
        const val STYLE_SHEET = "style_sheet"
    }

    object CardState {

        const val ACTIVATED = "activated"
        const val INVALID = "invalid"
        const val SHIPPED = "shipped"

        const val ACTIVATE_CARD_STATE = "card shipped"
    }

    object APIConstants {

        const val COUNT = 100
        const val START_INDEX = 0
        const val VERIFIED = "verified"
        const val PAGE_ID = "testpageId"
        const val X_CORRELATION_ID_VALUE = "5435436"
        const val RANDOM_FINGER_PRINT =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    }

    object HTTPMethod {

        const val POST = "POST"
        const val GET = "GET"
        const val PUT = "PUT"
    }

    object APIEndPoints {

        const val AUTH_AUTHORIZE_V2 = "/oauth/v2/authorize"
        const val AUTH_TOKEN = "/oauth/token"
        const val AUTH_TOKEN_V2 = "/oauth/v2/token"
        const val USER_INFO = "/oidc/v1/userinfo"

        const val LOGIN = "/v1/auth/login"
        const val CUSTOMER_SEARCH = "/v1/customers/search"
        const val CREATE_CUSTOMERS = "/v1/customers"
        const val GENDERS = "v1/common/genders"
        const val PHONE_TYPES = "v1/common/phone/types"
        const val EMAIL_TYPES = "v1/common/email/types"
        const val ADDRESS_TYPES = "v1/common/address/types"
        const val COUNTRIES = "/v1/common/countries"

        //Customer APIs
        const val CUSTOMER_WALLET = "/v1/wallets/"

        const val CUSTOMERS = "/v1/customers/"

        const val CUSTOMER_CARDS = "/cards"

        const val CUSTOMER_BLOCKS = "/v1/customerblocks/"

        const val CARD_BLOCKS = "/v1/cardblocks/"

        const val HISTORY = "/history"

        //Card APIs
        const val CARDS = "/v1/cards/"

        const val CARD_PROFILES = "/v1/cardprofiles/"

        const val WALLET_TRANSFER = "/v1/wallets/"
        const val WALLET_TRANSFER_AVAILABLE_CURRENCIES = "/availableCurrencies"

        const val CARDS_PAN = "/pan"
        const val CARDS_CVV2 = "/cvv2"

        const val EXTERNAL_REFERENCES = "v1/externalreferences/"
        const val EXTERNAL_REFERENCES_LIST = "/list"

        const val CREATE_CARD = "/v1/cards"

        const val ACTIVATE_CARD_STATE = "/state"

        //Secure Widget APIs
        const val WIDGETS_SECURE_TOKEN = "/token"
        const val WIDGETS_SECURE_SESSION_KEY = "/sessionkey"
        const val WIDGETS_SECURE_CARD = "/card"
        const val SECURE_WIDGETS_ACTIVATE_CARD = "/card/activation"
        const val SECURE_WIDGETS_SET_PIN = "/setpin"
        const val SECURE_WIDGETS_CHANGE_PIN = "/changepin"
    }
}