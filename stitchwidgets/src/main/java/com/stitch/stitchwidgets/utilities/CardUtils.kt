package com.stitch.stitchwidgets.utilities

import com.stitch.stitchwidgets.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

object CardUtils {

    fun getCardNumber(cardNumber: String): String {
        return cardNumber.substring(0, 4) + " " +
                cardNumber.substring(4, 8) + " " +
                cardNumber.substring(8, 12) + " " +
                cardNumber.substring(12, 16)
    }

    fun getCardExpiry(cardExpiry: String): String {
        val expiryDate = SimpleDateFormat("yyyyMM", Locale.getDefault()).parse(cardExpiry)
        val displayDate = SimpleDateFormat("MM/yy", Locale.getDefault())
        return expiryDate?.let { displayDate.format(it) } ?: ""
    }

    fun getCardType(cardNumber: String): Int {
        var cardImage = 0
        val listOfPattern = ArrayList<String>()
        val ptVisa = "^4[0-9]$"
        listOfPattern.add(ptVisa)
        val ptMasterCard = "^5[1-5]$"
        listOfPattern.add(ptMasterCard)
        val ptDiscover = "^6(?:011|5[0-9]{2})$"
        listOfPattern.add(ptDiscover)
        val ptAmeExp = "^3[47]$"
        listOfPattern.add(ptAmeExp)
        val imageArray =
            arrayOf(
                R.drawable.ic_visa,
                R.drawable.ic_master_card,
                R.drawable.ic_discover,
                R.drawable.ic_american_express
            )
        if (cardNumber.length >= 2) {
            for (i in listOfPattern.indices) {
                if (Pattern.compile(listOfPattern[i]).matcher(cardNumber.substring(0, 2))
                        .matches()
                ) {
                    cardImage = imageArray[i]
                }
            }
        }
        return cardImage
    }
}