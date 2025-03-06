package com.stitch.stitchwidgets.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.stitch.stitchwidgets.R
import com.stitch.stitchwidgets.databinding.WidgetStitchBinding
import com.stitch.stitchwidgets.utilities.Toast

open class StitchWidget : Fragment() {

    private lateinit var binding: WidgetStitchBinding
    val viewModel: StitchWidgetViewModel by viewModels()

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
                    R.layout.widget_stitch,
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