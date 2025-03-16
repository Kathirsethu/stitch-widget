package com.stitch.stitchwidgets.ui.view_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.stitch.stitchwidgets.R
import com.stitch.stitchwidgets.WidgetSDK
import com.stitch.stitchwidgets.data.model.SavedCardSettings
import com.stitch.stitchwidgets.databinding.WidgetCardBinding
import com.stitch.stitchwidgets.utilities.CardUtils.getWidgetPadding
import com.stitch.stitchwidgets.utilities.CardUtils.setWidgetFontSize
import com.stitch.stitchwidgets.utilities.CardUtils.setWidgetTextColor
import com.stitch.stitchwidgets.utilities.CardUtils.setWidgetTypeFace
import com.stitch.stitchwidgets.utilities.Constants
import com.stitch.stitchwidgets.utilities.Utils

class CardWidget : Fragment() {
    private lateinit var binding: WidgetCardBinding
    private val viewModel: CardWidgetViewModel by viewModels()
    private lateinit var savedCardSettings: SavedCardSettings

    private fun cardNumber(isMasked: Boolean): String {
        return if (isMasked) String.format(
            "%s %s",
            getString(R.string.mask_demo_card),
            viewModel.card.cardNumber?.let {
                it.substring(it.length - 4, it.length)
            }) else viewModel.card.cardNumber ?: ""
    }

    private fun cvv(isMasked: Boolean): String {
        return if (isMasked) "XXX" else viewModel.card.cvv2 ?: ""
    }

    companion object {
        lateinit var networkListener: () -> Boolean
        lateinit var progressBarListener: (isVisible: Boolean) -> Unit
        lateinit var logoutListener: (unAuth: Boolean) -> Unit
        private lateinit var reFetchSessionToken: (viewType: String) -> Unit
        lateinit var secureToken: String

        fun newInstance(
            networkListener: () -> Boolean,
            progressBarListener: (isVisible: Boolean) -> Unit,
            logoutListener: (unAuth: Boolean) -> Unit,
            reFetchSessionToken: (viewType: String) -> Unit,
            secureToken: String,
        ): CardWidget {
            val cardWidget = CardWidget()
            this.networkListener = networkListener
            this.progressBarListener = progressBarListener
            this.logoutListener = logoutListener
            this.reFetchSessionToken = reFetchSessionToken
            this.secureToken = secureToken
            return cardWidget
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedCardSettings = WidgetSDK.viewCardSettings
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.widget_card,
                    container,
                    false
                )
            config()
        }
        return binding.root
    }

    private fun config() {
        binding.viewModel = viewModel

        viewModel.networkListener = networkListener
        viewModel.progressBarListener = progressBarListener
        viewModel.logoutListener = logoutListener

        viewModel.secureToken = secureToken
        val deviceFingerprint: String = Utils.getDeviceFingerprint(requireContext())
        viewModel.getWidgetSecureSessionKey(deviceFingerprint)
        setCardStyleProperties()
        viewModel.setCardData = {
            setCardDataFromAPIResponse()
        }
        binding.layoutDemoCard.ivCardNumberEye.setOnClickListener {
            changeCardNumberEyeState()
        }
        binding.layoutDemoCard.tvCardNumber.setOnClickListener {
            toggleCardNumber()
        }
        binding.layoutDemoCard.ivCVVEye.setOnClickListener {
            changeCVVEyeState()
        }
        binding.layoutDemoCard.tvCardCVV.setOnClickListener {
            toggleCVV()
        }
    }

    private fun setCardDataFromAPIResponse() {
        binding.layoutDemoCard.tvCardNumber.text =
            cardNumber(viewModel.isCardNumberMaskEnabled.get() == null || viewModel.isCardNumberMaskEnabled.get() == true)
        binding.layoutDemoCard.tvCardProfileName.text = Constants.SampleData.CARD_PROFILE_NAME
        binding.layoutDemoCard.tvCardExpiry.text = viewModel.card.expiry
        binding.layoutDemoCard.tvCardCVV.text =
            cvv(viewModel.isCardCVVMaskEnabled.get() == null || viewModel.isCardCVVMaskEnabled.get() == true)
        if (viewModel.isCardNumberEye.get() == null || viewModel.isCardNumberEye.get() == true) {
            binding.layoutDemoCard.ivCardNumberEye.visibility = View.VISIBLE
            handleCardNumberEyeImage()
        } else {
            binding.layoutDemoCard.ivCardNumberEye.visibility = View.GONE
        }
        if (viewModel.isCVVEye.get() == null || viewModel.isCVVEye.get() == true) {
            binding.layoutDemoCard.ivCVVEye.visibility = View.VISIBLE
            handleCVVEyeImage()
        } else {
            binding.layoutDemoCard.ivCVVEye.visibility = View.GONE
        }
    }

    private fun changeCardNumberEyeState() {
        if (viewModel.isCardNumberMaskEnabled.get() == null || viewModel.isCardNumberMaskEnabled.get() == true) {
            viewModel.isCardNumberMasked.set(!(viewModel.isCardNumberMasked.get() ?: true))
            handleCardNumberEyeImage()
            binding.layoutDemoCard.tvCardNumber.text =
                cardNumber(viewModel.isCardNumberMasked.get() != null && viewModel.isCardNumberMasked.get() != false)
        }
    }

    private fun handleCardNumberEyeImage() {
        binding.layoutDemoCard.ivCardNumberEye.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (viewModel.isCardNumberMasked.get() == null || viewModel.isCardNumberMasked.get() == false) R.drawable.ic_password_hidden else R.drawable.ic_password_visible
            )
        )
    }

    private fun toggleCardNumber() {
        if (viewModel.isCardNumberEye.get() != null && viewModel.isCardNumberEye.get() == false && (viewModel.isCardNumberMaskEnabled.get() == null || viewModel.isCardNumberMaskEnabled.get() == true)) {
            viewModel.isCardNumberMasked.set(!(viewModel.isCardNumberMasked.get() ?: true))
            binding.layoutDemoCard.tvCardNumber.text =
                cardNumber(viewModel.isCardNumberMasked.get() != null && viewModel.isCardNumberMasked.get() != false)
        }
    }

    private fun changeCVVEyeState() {
        if (viewModel.isCardCVVMaskEnabled.get() == null || viewModel.isCardCVVMaskEnabled.get() == true) {
            viewModel.isCardCVVMasked.set(!(viewModel.isCardCVVMasked.get() ?: true))
            handleCVVEyeImage()
            cvv(viewModel.isCardCVVMasked.get() != null && viewModel.isCardCVVMasked.get() != false)
        }
    }

    private fun handleCVVEyeImage() {
        binding.layoutDemoCard.ivCVVEye.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (viewModel.isCardCVVMasked.get() == null || viewModel.isCardCVVMasked.get() == false) R.drawable.ic_password_hidden else R.drawable.ic_password_visible
            )
        )
    }

    private fun toggleCVV() {
        if (viewModel.isCVVEye.get() != null && viewModel.isCVVEye.get() == false && (viewModel.isCardCVVMaskEnabled.get() == null || viewModel.isCardCVVMaskEnabled.get() == true)) {
            viewModel.isCardCVVMasked.set(!(viewModel.isCardCVVMasked.get() ?: true))
            binding.layoutDemoCard.tvCardCVV.text =
                cvv(viewModel.isCardCVVMasked.get() != null && viewModel.isCardCVVMasked.get() != false)
        }
    }

    private fun setCardStyleProperties() {
        setWidgetTypeFace()

        setWidgetFontColor()

        setWidgetFontSize()

        setWidgetCardNumberPadding()

        setWidgetExpiryPadding()

        setWidgetCVVPadding()

        setCardMedia()

        viewModel.isCardNumberMaskEnabled.set(savedCardSettings.maskCardNumber)
        viewModel.isCardCVVMaskEnabled.set(savedCardSettings.maskCvv)
        viewModel.isCardNumberEye.set(savedCardSettings.showEyeIcon)
        viewModel.isCVVEye.set(savedCardSettings.showEyeIcon)
    }

    private fun setWidgetTypeFace() {
        binding.layoutDemoCard.tvCardNumber.setWidgetTypeFace(
            requireContext(),
            savedCardSettings.fontFamily,
            R.font.inconsolata_semi_bold
        )
        binding.layoutDemoCard.tvCardProfileNameLabel.setWidgetTypeFace(
            requireContext(),
            savedCardSettings.fontFamily,
            R.font.inter_medium
        )
        binding.layoutDemoCard.tvCardProfileName.setWidgetTypeFace(
            requireContext(),
            savedCardSettings.fontFamily,
            R.font.inter_semi_bold
        )
        binding.layoutDemoCard.tvCardExpiryLabel.setWidgetTypeFace(
            requireContext(),
            savedCardSettings.fontFamily,
            R.font.inter_medium
        )
        binding.layoutDemoCard.tvCardExpiry.setWidgetTypeFace(
            requireContext(),
            savedCardSettings.fontFamily,
            R.font.inter_semi_bold
        )
        binding.layoutDemoCard.tvCardCVVLabel.setWidgetTypeFace(
            requireContext(),
            savedCardSettings.fontFamily,
            R.font.inter_medium
        )
        binding.layoutDemoCard.tvCardCVV.setWidgetTypeFace(
            requireContext(),
            savedCardSettings.fontFamily,
            R.font.inter_semi_bold
        )
    }

    private fun setWidgetFontColor() {
        binding.layoutDemoCard.tvCardNumber.setWidgetTextColor(
            requireContext(), savedCardSettings.fontColor, R.color.white
        )
        binding.layoutDemoCard.tvCardExpiry.setWidgetTextColor(
            requireContext(), savedCardSettings.fontColor, R.color.white
        )
        binding.layoutDemoCard.tvCardExpiryLabel.setWidgetTextColor(
            requireContext(), savedCardSettings.fontColor, R.color.white
        )
        binding.layoutDemoCard.tvCardCVV.setWidgetTextColor(
            requireContext(), savedCardSettings.fontColor, R.color.white
        )
        binding.layoutDemoCard.tvCardCVVLabel.setWidgetTextColor(
            requireContext(), savedCardSettings.fontColor, R.color.white
        )
        binding.layoutDemoCard.tvCardProfileName.setWidgetTextColor(
            requireContext(), savedCardSettings.fontColor, R.color.white
        )
        binding.layoutDemoCard.tvCardProfileNameLabel.setWidgetTextColor(
            requireContext(), savedCardSettings.fontColor, R.color.white
        )
    }

    private fun setWidgetFontSize() {
        binding.layoutDemoCard.tvCardProfileName.setWidgetFontSize(
            savedCardSettings.fontSize.toString(),
            16f
        )
        binding.layoutDemoCard.tvCardExpiry.setWidgetFontSize(
            savedCardSettings.fontSize.toString(),
            16f
        )
        binding.layoutDemoCard.tvCardCVV.setWidgetFontSize(
            savedCardSettings.fontSize.toString(),
            16f
        )
        binding.layoutDemoCard.tvCardNumber.setWidgetFontSize(
            savedCardSettings.fontSize.toString(),
            24f
        )
    }

    private fun setWidgetCardNumberPadding() {
        binding.layoutDemoCard.numberTopPadding =
            getWidgetPadding(savedCardSettings.cardNumberPaddingTop.toString(), "0")

        binding.layoutDemoCard.numberBottomPadding =
            getWidgetPadding(savedCardSettings.cardNumberPaddingBottom.toString(), "0")

        binding.layoutDemoCard.numberLeftPadding =
            getWidgetPadding(savedCardSettings.cardNumberPaddingLeft.toString(), "0")

        binding.layoutDemoCard.numberRightPadding =
            getWidgetPadding(savedCardSettings.cardNumberPaddingRight.toString(), "0")
    }

    private fun setWidgetExpiryPadding() {
        binding.layoutDemoCard.expiryTopPadding =
            getWidgetPadding(savedCardSettings.expiryPaddingTop.toString(), "0")

        binding.layoutDemoCard.expiryBottomPadding =
            getWidgetPadding(savedCardSettings.expiryPaddingBottom.toString(), "0")

        binding.layoutDemoCard.expiryLeftPadding =
            getWidgetPadding(savedCardSettings.expiryPaddingLeft.toString(), "0")

        binding.layoutDemoCard.expiryRightPadding =
            getWidgetPadding(savedCardSettings.expiryPaddingRight.toString(), "0")
    }

    private fun setWidgetCVVPadding() {
        binding.layoutDemoCard.cvvTopPadding =
            getWidgetPadding(savedCardSettings.cvvPaddingTop.toString(), "0")

        binding.layoutDemoCard.cvvBottomPadding =
            getWidgetPadding(savedCardSettings.cvvPaddingBottom.toString(), "0")

        binding.layoutDemoCard.cvvLeftPadding =
            getWidgetPadding(savedCardSettings.cvvPaddingLeft.toString(), "0")

        binding.layoutDemoCard.cvvRightPadding =
            getWidgetPadding(savedCardSettings.cvvPaddingRight.toString(), "0")
    }

    private fun setCardMedia() {
        if (savedCardSettings.backgroundImage != null) {
            Glide.with(requireContext()).load(savedCardSettings.backgroundImage)
                .into(binding.layoutDemoCard.ivCardMedia)
            binding.layoutDemoCard.ivCardMedia.visibility = View.VISIBLE
        } else {
            binding.layoutDemoCard.ivCardMedia.visibility = View.GONE
            binding.layoutDemoCard.ivCardMedia.setImageDrawable(null)
            savedCardSettings.background?.let {
                when (it) {
                    is Double -> {
                        binding.layoutDemoCard.clCustomerCardBg.setBackgroundColor(it.toInt())
                    }

                    is Int -> {
                        binding.layoutDemoCard.clCustomerCardBg.setBackgroundColor(it)
                    }
                }
            }
        }
    }
}