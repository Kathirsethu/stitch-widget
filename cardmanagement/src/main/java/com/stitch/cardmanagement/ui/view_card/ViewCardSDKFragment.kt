package com.stitch.cardmanagement.ui.view_card

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
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
import com.stitch.cardmanagement.databinding.FragmentViewCardSdkBinding
import com.stitch.cardmanagement.ui.CardManagementSDKFragment
import com.stitch.cardmanagement.utilities.Constants
import com.stitch.cardmanagement.utilities.OnSwipeTouchListener
import java.util.Timer
import java.util.TimerTask

open class ViewCardSDKFragment : CardManagementSDKFragment() {

    private lateinit var binding: FragmentViewCardSdkBinding
    private lateinit var frontAnimation: AnimatorSet
    private lateinit var backAnimation: AnimatorSet

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
        ): ViewCardSDKFragment {
            val viewCardSDKFragment = ViewCardSDKFragment()
            this.networkListener = networkListener
            this.progressBarListener = progressBarListener
            this.logoutListener = logoutListener
            this.savedCardSettings = savedCardSettings
            this.reFetchSessionToken = reFetchSessionToken
            return viewCardSDKFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_view_card_sdk,
                container,
                false
            )
        config()
        return binding.root
    }

    private fun config() {
        binding.viewModel = viewModel
        binding.layoutDefault.viewModel = viewModel
        binding.layoutVertical.viewModel = viewModel
        binding.layoutVerticalFlippable1.viewModel = viewModel
        binding.layoutHorizontalFlippable.viewModel = viewModel
        binding.layoutVerticalFlippable2.viewModel = viewModel

        viewModel.networkListener = networkListener
        viewModel.progressBarListener = progressBarListener
        viewModel.logoutListener = logoutListener
        viewModel.reFetchSessionToken = reFetchSessionToken

        viewModel.viewType.set(Constants.ViewType.VIEW_CARD)
        viewModel.accountNumberBack.set(getString(R.string.masked_card_number))
        viewModel.cardCVVBack.set(getString(R.string.masked_card_cvv))

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
        initiateAnimation()

        viewModel.isFront.observe(viewLifecycleOwner) {
            when (viewModel.style.get()) {
                getString(R.string.style_vertical_flippable_1) -> {
                    flipCard(
                        it,
                        binding.layoutVerticalFlippable1.cvCustomerCardFront,
                        binding.layoutVerticalFlippable1.cvCustomerCardBack
                    )
                }

                getString(R.string.style_horizontal_flippable) -> {
                    flipCard(
                        it,
                        binding.layoutHorizontalFlippable.cvCustomerCardFront,
                        binding.layoutHorizontalFlippable.cvCustomerCardBack
                    )
                }

                getString(R.string.style_vertical_flippable_2) -> {
                    flipCard(
                        it,
                        binding.layoutVerticalFlippable2.cvCustomerCardFront,
                        binding.layoutVerticalFlippable2.cvCustomerCardBack
                    )
                }
            }
        }

        binding.layoutVerticalFlippable1.clCustomerCard.setOnTouchListener(object :
            OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                viewModel.isFront.value = !(viewModel.isFront.value ?: true)
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                viewModel.isFront.value = !(viewModel.isFront.value ?: true)
            }
        })

        binding.layoutHorizontalFlippable.clCustomerCard.setOnTouchListener(object :
            OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                viewModel.isFront.value = !(viewModel.isFront.value ?: true)
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                viewModel.isFront.value = !(viewModel.isFront.value ?: true)
            }
        })

        binding.layoutVerticalFlippable2.clCustomerCard.setOnTouchListener(object :
            OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                viewModel.isFront.value = !(viewModel.isFront.value ?: true)
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                viewModel.isFront.value = !(viewModel.isFront.value ?: true)
            }
        })

        viewModel.onShowMaskedCardNumberClick = {
            viewModel.isCardNumberMasked.set(!(viewModel.isCardNumberMasked.get() ?: true))
        }

        viewModel.onShowMaskedCardCVVClick = {
            if (viewModel.isCardCVVMasked.get() == true) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        viewModel.isCardCVVMasked.set(true)
                    }
                }, 30000)
            }
            viewModel.isCardCVVMasked.set(!(viewModel.isCardCVVMasked.get() ?: true))
        }
        if (viewModel.card.get() != null) {
            viewModel.cardNumber.set(viewModel.card.get()?.cardNumber)
            viewModel.nameOnCard.set(viewModel.card.get()?.embossedName)
            viewModel.getWidgetsSecureSessionKey(requireContext())
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
        updateCardStyle()
    }

    private fun initiateAnimation() {
        frontAnimation =
            AnimatorInflater.loadAnimator(
                requireContext(), R.animator.front_animator
            ) as AnimatorSet
        backAnimation =
            AnimatorInflater.loadAnimator(requireContext(), R.animator.back_animator) as AnimatorSet
        val scale = requireContext().resources.displayMetrics.density
        binding.layoutVerticalFlippable1.cvCustomerCardFront.cameraDistance = 8000 * scale
        binding.layoutVerticalFlippable1.cvCustomerCardBack.cameraDistance = 8000 * scale
        binding.layoutHorizontalFlippable.cvCustomerCardFront.cameraDistance = 8000 * scale
        binding.layoutHorizontalFlippable.cvCustomerCardBack.cameraDistance = 8000 * scale
        binding.layoutVerticalFlippable2.cvCustomerCardFront.cameraDistance = 8000 * scale
        binding.layoutVerticalFlippable2.cvCustomerCardBack.cameraDistance = 8000 * scale
    }

    fun setCardStyle(style: String, savedCardSettings: SavedCardSettings) {
        viewModel.savedCardSettings.set(savedCardSettings)
        viewModel.isFront.value = true
        viewModel.style.set(style)
        updateCardStyle()
    }

    private fun updateCardStyle() {
        binding.layoutDefault.root.visibility = View.GONE
        binding.layoutVertical.root.visibility = View.GONE
        binding.layoutVerticalFlippable1.root.visibility = View.GONE
        binding.layoutHorizontalFlippable.root.visibility = View.GONE
        binding.layoutVerticalFlippable2.root.visibility = View.GONE
        when (viewModel.style.get()) {
            getString(R.string.style_default) -> {
                binding.layoutDefault.root.visibility = View.VISIBLE
            }

            getString(R.string.style_vertical) -> {
                binding.layoutVertical.root.visibility = View.VISIBLE
            }

            getString(R.string.style_vertical_flippable_1) -> {
                binding.layoutVerticalFlippable1.root.visibility = View.VISIBLE
            }

            getString(R.string.style_horizontal_flippable) -> {
                binding.layoutHorizontalFlippable.root.visibility = View.VISIBLE
            }

            getString(R.string.style_vertical_flippable_2) -> {
                binding.layoutVerticalFlippable2.root.visibility = View.VISIBLE
            }

            else -> {
                binding.layoutDefault.root.visibility = View.VISIBLE
            }
        }
        setCardStyleProperties()
    }

    private fun setCardStyleProperties() {
        if (viewModel.savedCardSettings.get()?.fontFamily != null) {
            viewModel.cardStyleFontFamily.set(viewModel.savedCardSettings.get()?.fontFamily)
        } else {
            viewModel.cardStyleFontFamily.set(R.font.euclid_flex_regular)
        }
        if (viewModel.savedCardSettings.get()?.fontColor != null) {
            viewModel.cardStyleFontColor.set(viewModel.savedCardSettings.get()?.fontColor)
        } else {
            viewModel.cardStyleFontColor.set(
                ContextCompat.getColor(requireContext(), R.color.white)
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
        if (viewModel.savedCardSettings.get()?.numberTopPadding != null &&
            viewModel.savedCardSettings.get()?.numberTopPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleNumberTopPadding.set(
                viewModel.savedCardSettings.get()?.numberTopPadding.toString()
            )
        } else {
            viewModel.cardStyleNumberTopPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.numberBottomPadding != null &&
            viewModel.savedCardSettings.get()?.numberBottomPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleNumberBottomPadding.set(
                viewModel.savedCardSettings.get()?.numberBottomPadding.toString()
            )
        } else {
            viewModel.cardStyleNumberBottomPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.numberStartPadding != null &&
            viewModel.savedCardSettings.get()?.numberStartPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleNumberStartPadding.set(
                viewModel.savedCardSettings.get()?.numberStartPadding.toString()
            )
        } else {
            viewModel.cardStyleNumberStartPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.numberEndPadding != null &&
            viewModel.savedCardSettings.get()?.numberEndPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleNumberEndPadding.set(
                viewModel.savedCardSettings.get()?.numberEndPadding.toString()
            )
        } else {
            viewModel.cardStyleNumberEndPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.expiryTopPadding != null &&
            viewModel.savedCardSettings.get()?.expiryTopPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleExpiryTopPadding.set(
                viewModel.savedCardSettings.get()?.expiryTopPadding.toString()
            )
        } else {
            viewModel.cardStyleExpiryTopPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.expiryBottomPadding != null &&
            viewModel.savedCardSettings.get()?.expiryBottomPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleExpiryBottomPadding.set(
                viewModel.savedCardSettings.get()?.expiryBottomPadding.toString()
            )
        } else {
            viewModel.cardStyleExpiryBottomPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.expiryStartPadding != null &&
            viewModel.savedCardSettings.get()?.expiryStartPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleExpiryStartPadding.set(
                viewModel.savedCardSettings.get()?.expiryStartPadding.toString()
            )
        } else {
            viewModel.cardStyleExpiryStartPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.expiryEndPadding != null &&
            viewModel.savedCardSettings.get()?.expiryEndPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleExpiryEndPadding.set(
                viewModel.savedCardSettings.get()?.expiryEndPadding.toString()
            )
        } else {
            viewModel.cardStyleExpiryEndPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.cvvTopPadding != null &&
            viewModel.savedCardSettings.get()?.cvvTopPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleCVVTopPadding.set(
                viewModel.savedCardSettings.get()?.cvvTopPadding.toString()
            )
        } else {
            viewModel.cardStyleCVVTopPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.cvvBottomPadding != null &&
            viewModel.savedCardSettings.get()?.cvvBottomPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleCVVBottomPadding.set(
                viewModel.savedCardSettings.get()?.cvvBottomPadding.toString()
            )
        } else {
            viewModel.cardStyleCVVBottomPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.cvvStartPadding != null &&
            viewModel.savedCardSettings.get()?.cvvStartPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleCVVStartPadding.set(
                viewModel.savedCardSettings.get()?.cvvStartPadding.toString()
            )
        } else {
            viewModel.cardStyleCVVStartPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.cvvEndPadding != null &&
            viewModel.savedCardSettings.get()?.cvvEndPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleCVVEndPadding.set(
                viewModel.savedCardSettings.get()?.cvvEndPadding.toString()
            )
        } else {
            viewModel.cardStyleCVVEndPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.background != null) {
            viewModel.cardStyleBackground.set(viewModel.savedCardSettings.get()?.background)
        } else {
            viewModel.cardStyleBackground.set(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.bg_gradient_default_corner_8
                )
            )
        }
        if (viewModel.savedCardSettings.get()?.bgImageFile != null) {
            viewModel.cardMediaFile.set(viewModel.savedCardSettings.get()?.bgImageFile)
        }
        viewModel.isCardNumberMasked.set(viewModel.savedCardSettings.get()?.isCardNumberMasked)
        viewModel.isCardCVVMasked.set(viewModel.savedCardSettings.get()?.isCardCVVMasked)
    }

    private fun flipCard(isFront: Boolean?, targetFront: View, targetBack: View) {
        if (isFront != null && !isFront) {
            frontAnimation.setTarget(targetFront)
            backAnimation.setTarget(targetBack)
            frontAnimation.start()
            backAnimation.start()
        } else {
            frontAnimation.setTarget(targetBack)
            backAnimation.setTarget(targetFront)
            backAnimation.start()
            frontAnimation.start()
        }
    }

    fun retryFetchCard(sdkData: SDKData?, savedCardSettings: SavedCardSettings) {
        setSDKData(sdkData, savedCardSettings)
        viewModel.getWidgetsSecureSessionKey(requireContext())
    }
}