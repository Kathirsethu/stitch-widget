package com.stitch.cardmanagement.ui.reset_pin

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.stitch.cardmanagement.R
import com.stitch.cardmanagement.data.model.SDKData
import com.stitch.cardmanagement.data.model.SavedCardSettings
import com.stitch.cardmanagement.data.model.response.Card
import com.stitch.cardmanagement.databinding.FragmentResetPinSdkBinding
import com.stitch.cardmanagement.ui.CardManagementSDKFragment
import com.stitch.cardmanagement.utilities.Constants
import com.stitch.cardmanagement.utilities.Toast

open class ResetPinSDKFragment : CardManagementSDKFragment() {

    private lateinit var binding: FragmentResetPinSdkBinding

    companion object {
        lateinit var networkListener: () -> Boolean
        lateinit var progressBarListener: (isVisible: Boolean) -> Unit
        lateinit var logoutListener: (unAuth: Boolean) -> Unit
        lateinit var savedCardSettings: SavedCardSettings
        lateinit var reFetchSessionToken: (viewType: String) -> Unit

        fun newInstance(
            networkListener: () -> Boolean,
            progressBarListener: (isVisible: Boolean) -> Unit,
            logoutListener: (unAuth: Boolean) -> Unit,
            savedCardSettings: SavedCardSettings,
            reFetchSessionToken: (viewType: String) -> Unit,
        ): ResetPinSDKFragment {
            val resetPinSDKFragment = ResetPinSDKFragment()
            this.networkListener = networkListener
            this.progressBarListener = progressBarListener
            this.logoutListener = logoutListener
            this.savedCardSettings = savedCardSettings
            this.reFetchSessionToken = reFetchSessionToken
            return resetPinSDKFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_reset_pin_sdk,
                container,
                false
            )
        config()
        return binding.root
    }

    private fun config() {
        binding.viewModel = viewModel
        binding.layoutOutlined.viewModel = viewModel
        binding.layoutFilled.viewModel = viewModel
        binding.layoutStandard.viewModel = viewModel

        viewModel.networkListener = networkListener
        viewModel.progressBarListener = progressBarListener
        viewModel.logoutListener = logoutListener
        viewModel.reFetchSessionToken = reFetchSessionToken

        viewModel.viewType.set(Constants.ViewType.RESET_CARD_PIN)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setSDKData(
                arguments?.getParcelable(Constants.ParcelConstants.SDK_DATA, SDKData::class.java),
                savedCardSettings
            )
        } else {
            setSDKData(
                arguments?.getParcelable(Constants.ParcelConstants.SDK_DATA),
                savedCardSettings
            )
        }
        viewModel.onResetPINClick = {
            viewModel.retryCount.set(0)
            viewModel.getWidgetsSecureSessionKey(requireContext())
        }
        viewModel.onResetPINSuccess = {
            viewModel.getCards()
            viewModel.oldPin.set("")
            viewModel.newPin.set("")
            Toast.success(getString(R.string.pin_change_successfully))
        }
    }

    private fun setSDKData(sdkData: SDKData?, savedCardSettings: SavedCardSettings) {
        viewModel.sdkData.set(sdkData)
        viewModel.savedCardSettings.set(savedCardSettings)
        viewModel.card.set(viewModel.sdkData.get()?.card)
        viewModel.tokenType.set(viewModel.sdkData.get()?.tokenType)
        viewModel.apiToken.set(viewModel.sdkData.get()?.apiToken)
        viewModel.authToken.set(viewModel.sdkData.get()?.authToken)
        viewModel.customerNumber.set(viewModel.sdkData.get()?.customerNumber)
        viewModel.programName.set(viewModel.sdkData.get()?.programName)
        viewModel.secureToken.set(viewModel.sdkData.get()?.secureToken)
        viewModel.fingerPrint.set(viewModel.sdkData.get()?.fingerPrint)
        setFormStyleProperties()
    }

    fun setCardStyle(
        cardsList: ArrayList<Card>?,
        style: String,
        savedCardSettings: SavedCardSettings
    ) {
        viewModel.card.set(cardsList?.find { card ->
            card.cardNumber == viewModel.cardNumber.get()
        })
        viewModel.sdkData.get()?.card = viewModel.card.get()
        viewModel.savedCardSettings.set(savedCardSettings)
        viewModel.oldPin.set("")
        viewModel.newPin.set("")
        binding.layoutOutlined.root.visibility = View.GONE
        binding.layoutFilled.root.visibility = View.GONE
        binding.layoutStandard.root.visibility = View.GONE
        when (style) {
            getString(R.string.style_outlined) -> {
                binding.layoutOutlined.root.visibility = View.VISIBLE
            }

            getString(R.string.style_filled) -> {
                binding.layoutFilled.root.visibility = View.VISIBLE
            }

            getString(R.string.style_standard) -> {
                binding.layoutStandard.root.visibility = View.VISIBLE
            }
        }
        setFormStyleProperties()
    }

    private fun setFormStyleProperties() {
        if (viewModel.savedCardSettings.get()?.fontFamily != null) {
            viewModel.cardStyleFontFamily.set(viewModel.savedCardSettings.get()?.fontFamily)
        } else {
            viewModel.cardStyleFontFamily.set(R.font.euclid_flex_regular)
        }
        if (viewModel.savedCardSettings.get()?.fontColor != null) {
            viewModel.cardStyleFontColor.set(viewModel.savedCardSettings.get()?.fontColor)
        } else {
            viewModel.cardStyleFontColor.set(
                ContextCompat.getColor(requireContext(), R.color.black)
            )
        }
        if (viewModel.savedCardSettings.get()?.fontSize != null &&
            viewModel.savedCardSettings.get()?.fontSize.toString().isNotEmpty()
        ) {
            viewModel.styleFontSize.set(
                viewModel.savedCardSettings.get()?.fontSize.toString()
            )
        } else {
            viewModel.styleFontSize.set("14")
        }
        setCardData()
    }

    private fun setCardData() {
        if (viewModel.card.get() != null) {
            viewModel.showCardResetPin.set(true)
            viewModel.isCardNotActivate.set(viewModel.card.get()?.state != Constants.CardState.ACTIVATED)
            viewModel.cardNumber.set(viewModel.card.get()?.cardNumber)
        }
    }

    fun retryResetPin(sdkData: SDKData?, savedCardSettings: SavedCardSettings) {
        setSDKData(sdkData, savedCardSettings)
        viewModel.getWidgetsSecureSessionKey(requireContext())
    }
}