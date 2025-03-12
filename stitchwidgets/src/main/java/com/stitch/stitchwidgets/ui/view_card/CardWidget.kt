package com.stitch.stitchwidgets.ui.view_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.stitch.stitchwidgets.R
import com.stitch.stitchwidgets.WidgetSDK
import com.stitch.stitchwidgets.data.model.SavedCardSettings
import com.stitch.stitchwidgets.databinding.WidgetCardBinding
import com.stitch.stitchwidgets.utilities.Utils

class CardWidget : Fragment() {
    private lateinit var binding: WidgetCardBinding
    private val viewModel: CardWidgetViewModel by viewModels()
    private lateinit var savedCardSettings: SavedCardSettings

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
            binding.layoutDemoCard.tvCardNumber.text =
                if (viewModel.isCardNumberMaskEnabled.get() == null || viewModel.isCardNumberMaskEnabled.get() == true) String.format(
                    "%s %s",
                    getString(R.string.mask_demo_card),
                    viewModel.card.cardNumber?.let {
                        it.substring(it.length - 4, it.length)
                    }) else viewModel.card.cardNumber
            binding.layoutDemoCard.tvCardProfileName.text = "Navin Sivaji"
            binding.layoutDemoCard.tvCardExpiry.text = viewModel.card.expiry
            binding.layoutDemoCard.tvCardCVV.text =
                if (viewModel.isCardCVVMaskEnabled.get() == null || viewModel.isCardCVVMaskEnabled.get() == true) "XXX" else viewModel.card.cvv2
            if (viewModel.isCardNumberEye.get() == null || viewModel.isCardNumberEye.get() == true) {
                binding.layoutDemoCard.ivCardNumberEye.visibility = View.VISIBLE
                if (viewModel.isCardNumberMasked.get() == null || viewModel.isCardNumberMasked.get() == false) {
                    binding.layoutDemoCard.ivCardNumberEye.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_password_hidden
                        )
                    )
                } else {
                    binding.layoutDemoCard.ivCardNumberEye.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_password_visible
                        )
                    )
                }
            } else {
                binding.layoutDemoCard.ivCardNumberEye.visibility = View.GONE
            }
            if (viewModel.isCVVEye.get() == null || viewModel.isCVVEye.get() == true) {
                binding.layoutDemoCard.ivCVVEye.visibility = View.VISIBLE
                if (viewModel.isCardCVVMasked.get() == null || viewModel.isCardCVVMasked.get() == false) {
                    binding.layoutDemoCard.ivCVVEye.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_password_hidden
                        )
                    )
                } else {
                    binding.layoutDemoCard.ivCVVEye.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_password_visible
                        )
                    )
                }
            } else {
                binding.layoutDemoCard.ivCVVEye.visibility = View.GONE
            }
        }
        binding.layoutDemoCard.ivCardNumberEye.setOnClickListener {
            if (viewModel.isCardNumberMaskEnabled.get() == null || viewModel.isCardNumberMaskEnabled.get() == true) {
                viewModel.isCardNumberMasked.set(!(viewModel.isCardNumberMasked.get() ?: true))
                if (viewModel.isCardNumberMasked.get() == null || viewModel.isCardNumberMasked.get() == false) {
                    binding.layoutDemoCard.ivCardNumberEye.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_password_hidden
                        )
                    )
                    binding.layoutDemoCard.tvCardNumber.text = viewModel.card.cardNumber
                } else {
                    binding.layoutDemoCard.ivCardNumberEye.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_password_visible
                        )
                    )
                    binding.layoutDemoCard.tvCardNumber.text =
                        String.format("%s %s", getString(R.string.mask_demo_card),
                            viewModel.card.cardNumber?.let {
                                it.substring(it.length - 4, it.length)
                            })
                }
            }
        }
        binding.layoutDemoCard.tvCardNumber.setOnClickListener {
            if (viewModel.isCardNumberEye.get() != null && viewModel.isCardNumberEye.get() == false && (viewModel.isCardNumberMaskEnabled.get() == null || viewModel.isCardNumberMaskEnabled.get() == true)) {
                viewModel.isCardNumberMasked.set(!(viewModel.isCardNumberMasked.get() ?: true))
                binding.layoutDemoCard.tvCardNumber.text =
                    if (viewModel.isCardNumberMasked.get() == null || viewModel.isCardNumberMasked.get() == false) {
                        viewModel.card.cardNumber
                    } else {
                        String.format("%s %s", getString(R.string.mask_demo_card),
                            viewModel.card.cardNumber?.let {
                                it.substring(it.length - 4, it.length)
                            })
                    }
            }
        }
        binding.layoutDemoCard.ivCVVEye.setOnClickListener {
            if (viewModel.isCardCVVMaskEnabled.get() == null || viewModel.isCardCVVMaskEnabled.get() == true) {
                viewModel.isCardCVVMasked.set(!(viewModel.isCardCVVMasked.get() ?: true))
                if (viewModel.isCardCVVMasked.get() == null || viewModel.isCardCVVMasked.get() == false) {
                    binding.layoutDemoCard.ivCVVEye.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_password_hidden
                        )
                    )
                    binding.layoutDemoCard.tvCardCVV.text = viewModel.card.cvv2
                } else {
                    binding.layoutDemoCard.ivCVVEye.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_password_visible
                        )
                    )
                    binding.layoutDemoCard.tvCardCVV.text = "XXX"
                }
            }
        }
        binding.layoutDemoCard.tvCardCVV.setOnClickListener {
            if (viewModel.isCVVEye.get() != null && viewModel.isCVVEye.get() == false && (viewModel.isCardCVVMaskEnabled.get() == null || viewModel.isCardCVVMaskEnabled.get() == true)) {
                viewModel.isCardCVVMasked.set(!(viewModel.isCardCVVMasked.get() ?: true))
                binding.layoutDemoCard.tvCardCVV.text =
                    if (viewModel.isCardCVVMasked.get() == null || viewModel.isCardCVVMasked.get() == false) viewModel.card.cvv2 else "XXX"
            }
        }
    }

    private fun setCardStyleProperties() {
        binding.layoutDemoCard.tvCardNumber.typeface = (if (savedCardSettings.fontFamily != null) {
            ResourcesCompat.getFont(requireContext(), savedCardSettings.fontFamily!!)
        } else {
            ResourcesCompat.getFont(requireContext(), R.font.inconsolata_semi_bold)
        })
        binding.layoutDemoCard.tvCardProfileNameLabel.typeface =
            (if (savedCardSettings.fontFamily != null) {
                ResourcesCompat.getFont(requireContext(), savedCardSettings.fontFamily!!)
            } else {
                ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
            })
        binding.layoutDemoCard.tvCardProfileName.typeface =
            (if (savedCardSettings.fontFamily != null) {
                ResourcesCompat.getFont(requireContext(), savedCardSettings.fontFamily!!)
            } else {
                ResourcesCompat.getFont(requireContext(), R.font.inter_semi_bold)
            })
        binding.layoutDemoCard.tvCardExpiryLabel.typeface =
            (if (savedCardSettings.fontFamily != null) {
                ResourcesCompat.getFont(requireContext(), savedCardSettings.fontFamily!!)
            } else {
                ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
            })
        binding.layoutDemoCard.tvCardExpiry.typeface = (if (savedCardSettings.fontFamily != null) {
            ResourcesCompat.getFont(requireContext(), savedCardSettings.fontFamily!!)
        } else {
            ResourcesCompat.getFont(requireContext(), R.font.inter_semi_bold)
        })
        binding.layoutDemoCard.tvCardCVVLabel.typeface =
            (if (savedCardSettings.fontFamily != null) {
                ResourcesCompat.getFont(requireContext(), savedCardSettings.fontFamily!!)
            } else {
                ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
            })
        binding.layoutDemoCard.tvCardCVV.typeface = (if (savedCardSettings.fontFamily != null) {
            ResourcesCompat.getFont(requireContext(), savedCardSettings.fontFamily!!)
        } else {
            ResourcesCompat.getFont(requireContext(), R.font.inter_semi_bold)
        })
        if (savedCardSettings.fontColor != null) {
            savedCardSettings.fontColor
        } else {
            R.color.white
        }?.let {
            try {
                binding.layoutDemoCard.tvCardNumber.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        it
                    )
                )
                binding.layoutDemoCard.tvCardExpiry.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        it
                    )
                )
                binding.layoutDemoCard.tvCardExpiryLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        it
                    )
                )
                binding.layoutDemoCard.tvCardCVV.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        it
                    )
                )
                binding.layoutDemoCard.tvCardCVVLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        it
                    )
                )
                binding.layoutDemoCard.tvCardProfileName.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        it
                    )
                )
                binding.layoutDemoCard.tvCardProfileNameLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        it
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                binding.layoutDemoCard.tvCardNumber.setTextColor(it)
                binding.layoutDemoCard.tvCardExpiry.setTextColor(it)
                binding.layoutDemoCard.tvCardExpiryLabel.setTextColor(it)
                binding.layoutDemoCard.tvCardCVV.setTextColor(it)
                binding.layoutDemoCard.tvCardCVVLabel.setTextColor(it)
                binding.layoutDemoCard.tvCardProfileName.setTextColor(it)
                binding.layoutDemoCard.tvCardProfileNameLabel.setTextColor(it)
            }
        }
        if (savedCardSettings.fontSize != null &&
            savedCardSettings.fontSize.toString().isNotEmpty()
        ) {
            savedCardSettings.fontSize.toString()
        } else {
            "16"
        }.let {
            binding.layoutDemoCard.tvCardProfileName.textSize = it.toFloat()
            binding.layoutDemoCard.tvCardExpiry.textSize = it.toFloat()
            binding.layoutDemoCard.tvCardCVV.textSize = it.toFloat()
        }
        if (savedCardSettings.fontSize != null &&
            savedCardSettings.fontSize.toString().isNotEmpty()
        ) {
            savedCardSettings.fontSize.toString()
        } else {
            "24"
        }.let {
            binding.layoutDemoCard.tvCardNumber.textSize = "24".toFloat()
        }
        if (savedCardSettings.cardNumberPaddingTop != null &&
            savedCardSettings.cardNumberPaddingTop.toString().isNotEmpty()
        ) {
            savedCardSettings.cardNumberPaddingTop.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.numberTopPadding = it
        }
        if (savedCardSettings.cardNumberPaddingBottom != null &&
            savedCardSettings.cardNumberPaddingBottom.toString().isNotEmpty()
        ) {
            savedCardSettings.cardNumberPaddingBottom.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.numberBottomPadding = it
        }
        if (savedCardSettings.cardNumberPaddingLeft != null &&
            savedCardSettings.cardNumberPaddingLeft.toString().isNotEmpty()
        ) {
            savedCardSettings.cardNumberPaddingLeft.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.numberLeftPadding = it
        }
        if (savedCardSettings.cardNumberPaddingRight != null &&
            savedCardSettings.cardNumberPaddingRight.toString().isNotEmpty()
        ) {
            savedCardSettings.cardNumberPaddingRight.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.numberRightPadding = it
        }
        if (savedCardSettings.expiryPaddingTop != null &&
            savedCardSettings.expiryPaddingTop.toString().isNotEmpty()
        ) {
            savedCardSettings.expiryPaddingTop.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.expiryTopPadding = it
        }
        if (savedCardSettings.expiryPaddingBottom != null &&
            savedCardSettings.expiryPaddingBottom.toString().isNotEmpty()
        ) {
            savedCardSettings.expiryPaddingBottom.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.expiryBottomPadding = it
        }
        if (savedCardSettings.expiryPaddingLeft != null &&
            savedCardSettings.expiryPaddingLeft.toString().isNotEmpty()
        ) {
            savedCardSettings.expiryPaddingLeft.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.expiryLeftPadding = it
        }
        if (savedCardSettings.expiryPaddingRight != null &&
            savedCardSettings.expiryPaddingRight.toString().isNotEmpty()
        ) {
            savedCardSettings.expiryPaddingRight.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.expiryRightPadding = it
        }
        if (savedCardSettings.cvvPaddingTop != null &&
            savedCardSettings.cvvPaddingTop.toString().isNotEmpty()
        ) {
            savedCardSettings.cvvPaddingTop.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.cvvTopPadding = it
        }
        if (savedCardSettings.cvvPaddingBottom != null &&
            savedCardSettings.cvvPaddingBottom.toString().isNotEmpty()
        ) {
            savedCardSettings.cvvPaddingBottom.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.cvvBottomPadding = it
        }
        if (savedCardSettings.cvvPaddingLeft != null &&
            savedCardSettings.cvvPaddingLeft.toString().isNotEmpty()
        ) {
            savedCardSettings.cvvPaddingLeft.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.cvvLeftPadding = it
        }
        if (savedCardSettings.cvvPaddingRight != null &&
            savedCardSettings.cvvPaddingRight.toString().isNotEmpty()
        ) {
            savedCardSettings.cvvPaddingRight.toString()
        } else {
            "0"
        }.let {
            binding.layoutDemoCard.cvvRightPadding = it
        }
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
        viewModel.isCardNumberMaskEnabled.set(savedCardSettings.maskCardNumber)
        viewModel.isCardCVVMaskEnabled.set(savedCardSettings.maskCvv)
        viewModel.isCardNumberEye.set(savedCardSettings.showEyeIcon)
        viewModel.isCVVEye.set(savedCardSettings.showEyeIcon)
    }
}