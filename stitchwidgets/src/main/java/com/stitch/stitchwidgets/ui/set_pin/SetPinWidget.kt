package com.stitch.stitchwidgets.ui.set_pin

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.stitch.stitchwidgets.R
import com.stitch.stitchwidgets.WidgetSDK
import com.stitch.stitchwidgets.data.model.SDKData
import com.stitch.stitchwidgets.data.model.SavedCardSettings
import com.stitch.stitchwidgets.databinding.WidgetSetPinBinding
import com.stitch.stitchwidgets.ui.StitchWidget
import com.stitch.stitchwidgets.utilities.Constants
import com.stitch.stitchwidgets.utilities.Toast
import com.stitch.stitchwidgets.utilities.Utils

open class SetPinWidget : StitchWidget() {

    private lateinit var binding: WidgetSetPinBinding
    private lateinit var savedCardSettings: SavedCardSettings

    companion object {
        lateinit var networkListener: () -> Boolean
        lateinit var progressBarListener: (isVisible: Boolean) -> Unit
        lateinit var logoutListener: (unAuth: Boolean) -> Unit
        lateinit var reFetchSessionToken: (viewType: String) -> Unit
        lateinit var onSetPinSuccess: () -> Unit

        fun newInstance(
            networkListener: () -> Boolean,
            progressBarListener: (isVisible: Boolean) -> Unit,
            logoutListener: (unAuth: Boolean) -> Unit,
            reFetchSessionToken: (viewType: String) -> Unit,
            onSetPinSuccess: () -> Unit,
        ): SetPinWidget {
            val setPinWidget = SetPinWidget()
            this.networkListener = networkListener
            this.progressBarListener = progressBarListener
            this.logoutListener = logoutListener
            this.reFetchSessionToken = reFetchSessionToken
            this.onSetPinSuccess = onSetPinSuccess
            return setPinWidget
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedCardSettings = WidgetSDK.setPinSettings
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.widget_set_pin,
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

        viewModel.viewType.set(Constants.ViewType.SET_CARD_PIN)
        if (!arguments?.getString(Constants.ParcelConstants.CARD_NUMBER).isNullOrEmpty())
            viewModel.cardNumber.set(arguments?.getString(Constants.ParcelConstants.CARD_NUMBER))
        viewModel.showCardSetPin.set(true)
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
        viewModel.onSetPINClick = {
            viewModel.retryCount.set(0)
            viewModel.getWidgetsSecureSessionKey(requireContext())
        }
        viewModel.onSetPINSuccess = {
            viewModel.pin.set("")
            viewModel.confirmPin.set("")
            Toast.success(getString(R.string.pin_set_successfully))
            onSetPinSuccess.invoke()
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
        viewModel.fingerprint.set(Utils.getDeviceFingerprint(requireContext()))
        setFormStyleProperties()
    }

    private fun setFormStyleProperties() {
        if (viewModel.savedCardSettings.get()?.textFieldVariant != null) {
            viewModel.styleSheetType.set(viewModel.savedCardSettings.get()?.textFieldVariant)
        } else {
            viewModel.styleSheetType.set(getString(R.string.style_outlined))
        }
        if (viewModel.savedCardSettings.get()?.fontFamily != null) {
            viewModel.cardStyleFontFamily.set(viewModel.savedCardSettings.get()?.fontFamily)
        } else {
            viewModel.cardStyleFontFamily.set(R.font.inter_regular)
        }
        if (viewModel.savedCardSettings.get()?.fontColor != null) {
            viewModel.cardStyleFontColor.set(viewModel.savedCardSettings.get()?.fontColor)
        } else {
            viewModel.cardStyleFontColor.set(
                ContextCompat.getColor(requireContext(), R.color.text_color)
            )
        }
        if (viewModel.savedCardSettings.get()?.buttonFontColor != null) {
            viewModel.cardStyleButtonFontColor.set(viewModel.savedCardSettings.get()?.buttonFontColor)
        } else {
            viewModel.cardStyleButtonFontColor.set(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
        }
        if (viewModel.savedCardSettings.get()?.buttonBackground != null) {
            viewModel.cardStyleButtonBackgroundColor.set(viewModel.savedCardSettings.get()?.buttonBackground)
        } else {
            viewModel.cardStyleButtonBackgroundColor.set(
                ContextCompat.getColor(requireContext(), R.color.colorBase)
            )
        }
        if (viewModel.savedCardSettings.get()?.fontSize != null &&
            viewModel.savedCardSettings.get()?.fontSize.toString().isNotEmpty()
        ) {
            viewModel.styleFontSize.set(
                viewModel.savedCardSettings.get()?.fontSize.toString()
            )
        } else {
            viewModel.styleFontSize.set("16")
        }
        binding.layoutOutlined.root.visibility = View.GONE
        binding.layoutFilled.root.visibility = View.GONE
        binding.layoutStandard.root.visibility = View.GONE
        setCardStyle(viewModel.styleSheetType.get() ?: getString(R.string.style_outlined))
    }

    private fun setCardStyle(
        style: String,
    ) {
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
        setCardData()
    }

    private fun setCardData() {
        if (viewModel.card.get() != null) {
            viewModel.showCardSetPin.set(true)
            viewModel.cardNumber.set(viewModel.card.get()?.cardNumber)
        }
    }

    fun retrySetPin(sdkData: SDKData?, savedCardSettings: SavedCardSettings) {
        setSDKData(sdkData, savedCardSettings)
        viewModel.getWidgetsSecureSessionKey(requireContext())
    }
}