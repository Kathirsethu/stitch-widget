package com.stitch.cardmanagement.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.stitch.cardmanagement.R
import com.stitch.cardmanagement.databinding.FragmentCardManagementSdkBinding
import com.stitch.cardmanagement.utilities.Toast

open class CardManagementSDKFragment : Fragment() {

    private lateinit var binding: FragmentCardManagementSdkBinding
    val viewModel: CardManagementSDKViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.invoke(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Toast(requireContext())
        if (!::binding.isInitialized) {
            binding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.fragment_card_management_sdk,
                    container,
                    false
                )
            config()
        }
        return binding.root
    }

    private fun config() {
        binding.viewModel = viewModel
    }
}