package com.jamal.blescanner.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jamal.blescanner.base.BaseFragment
import com.jamal.blescanner.databinding.FragmentDeviceListBinding
import com.jamal.blescanner.ui.detail.DetailActivity
import com.jamal.blescanner.utils.TAB_TITLES
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceListFragment(
    private val type: Int
) : BaseFragment<FragmentDeviceListBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDeviceListBinding =
        FragmentDeviceListBinding::inflate

    private val deviceAdapter: BleDeviceAdapter by lazy { BleDeviceAdapter() }
    private val viewModel: MainViewModel by activityViewModels()

    override fun setup() {
        setupRecyclerView()
        if (isDatabaseMode()) {
            viewModel.scannedDatabaseDevice.observe(this) {
                Log.d("coba", "setup: scannedDatabaseDevice $it")
                deviceAdapter.differ.submitList(it)
            }
        } else {
            viewModel.scannedDevice.observe(this) {
                Log.d("coba", "setup: scannedDevice $it")
                deviceAdapter.differ.submitList(it)
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding.rvItems) {
            adapter = deviceAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        deviceAdapter.setOnItemClickListener {
            goToActivity(DetailActivity::class.java, Bundle().apply {
                putParcelable(DetailActivity.EXTRA_DATA, it)
                putBoolean(DetailActivity.EXTRA_SAVED, isDatabaseMode())
            })
        }
    }

    private fun isDatabaseMode() = type == TAB_TITLES[0]
}