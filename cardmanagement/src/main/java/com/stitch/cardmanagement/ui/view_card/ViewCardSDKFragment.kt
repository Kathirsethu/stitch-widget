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
        binding.layoutHorizontal.viewModel = viewModel
        binding.layoutHorizontalFlippable1.viewModel = viewModel
        binding.layoutVerticalFlippable.viewModel = viewModel
        binding.layoutHorizontalFlippable2.viewModel = viewModel

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
                getString(R.string.style_horizontal_flippable_1) -> {
                    flipCard(
                        it,
                        binding.layoutHorizontalFlippable1.cvCustomerCardFront,
                        binding.layoutHorizontalFlippable1.cvCustomerCardBack
                    )
                }

                getString(R.string.style_vertical_flippable) -> {
                    flipCard(
                        it,
                        binding.layoutVerticalFlippable.cvCustomerCardFront,
                        binding.layoutVerticalFlippable.cvCustomerCardBack
                    )
                }

                getString(R.string.style_horizontal_flippable_2) -> {
                    flipCard(
                        it,
                        binding.layoutHorizontalFlippable2.cvCustomerCardFront,
                        binding.layoutHorizontalFlippable2.cvCustomerCardBack
                    )
                }
            }
        }

        binding.layoutHorizontalFlippable1.clCustomerCard.setOnTouchListener(object :
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

        binding.layoutVerticalFlippable.clCustomerCard.setOnTouchListener(object :
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

        binding.layoutHorizontalFlippable2.clCustomerCard.setOnTouchListener(object :
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
        binding.layoutHorizontalFlippable1.cvCustomerCardFront.cameraDistance = 8000 * scale
        binding.layoutHorizontalFlippable1.cvCustomerCardBack.cameraDistance = 8000 * scale
        binding.layoutVerticalFlippable.cvCustomerCardFront.cameraDistance = 8000 * scale
        binding.layoutVerticalFlippable.cvCustomerCardBack.cameraDistance = 8000 * scale
        binding.layoutHorizontalFlippable2.cvCustomerCardFront.cameraDistance = 8000 * scale
        binding.layoutHorizontalFlippable2.cvCustomerCardBack.cameraDistance = 8000 * scale
    }

    fun setCardStyle(style: String, savedCardSettings: SavedCardSettings) {
        viewModel.savedCardSettings.set(savedCardSettings)
        viewModel.isFront.value = true
        viewModel.style.set(style)
        updateCardStyle()
    }

    private fun updateCardStyle() {
        binding.layoutDefault.root.visibility = View.GONE
        binding.layoutHorizontal.root.visibility = View.GONE
        binding.layoutHorizontalFlippable1.root.visibility = View.GONE
        binding.layoutVerticalFlippable.root.visibility = View.GONE
        binding.layoutHorizontalFlippable2.root.visibility = View.GONE
        when (viewModel.style.get()) {
            getString(R.string.style_default) -> {
                binding.layoutDefault.root.visibility = View.VISIBLE
            }

            getString(R.string.style_horizontal) -> {
                binding.layoutHorizontal.root.visibility = View.VISIBLE
            }

            getString(R.string.style_horizontal_flippable_1) -> {
                binding.layoutHorizontalFlippable1.root.visibility = View.VISIBLE
            }

            getString(R.string.style_vertical_flippable) -> {
                binding.layoutVerticalFlippable.root.visibility = View.VISIBLE
            }

            getString(R.string.style_horizontal_flippable_2) -> {
                binding.layoutHorizontalFlippable2.root.visibility = View.VISIBLE
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
            viewModel.cardStyleFontFamily.set(R.font.euclid_flex_bold)
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
        if (viewModel.savedCardSettings.get()?.numberPadding != null &&
            viewModel.savedCardSettings.get()?.numberPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleNumberPadding.set(
                viewModel.savedCardSettings.get()?.numberPadding.toString()
            )
        } else {
            viewModel.cardStyleNumberPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.expiryPadding != null &&
            viewModel.savedCardSettings.get()?.expiryPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleExpiryPadding.set(
                viewModel.savedCardSettings.get()?.expiryPadding.toString()
            )
        } else {
            viewModel.cardStyleExpiryPadding.set("0")
        }
        if (viewModel.savedCardSettings.get()?.cvvPadding != null &&
            viewModel.savedCardSettings.get()?.cvvPadding.toString().isNotEmpty()
        ) {
            viewModel.cardStyleCVVPadding.set(
                viewModel.savedCardSettings.get()?.cvvPadding.toString()
            )
        } else {
            viewModel.cardStyleCVVPadding.set("0")
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