package com.jamal.blescanner.ui.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.jamal.blescanner.R
import com.jamal.blescanner.base.BaseAdapter
import com.jamal.blescanner.data.model.BleDeviceModel
import com.jamal.blescanner.databinding.ItemBleDeviceLayoutBinding

class BleDeviceAdapter : BaseAdapter<ItemBleDeviceLayoutBinding, BleDeviceModel>() {
    override val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> ItemBleDeviceLayoutBinding =
        ItemBleDeviceLayoutBinding::inflate

    override fun bind(binding: ItemBleDeviceLayoutBinding, item: BleDeviceModel) {
        Log.d("coba", "bind: $item")
        with(binding) {
            tvName.text = item.name
            tvMac.text = item.mac
            tvUuid.text = item.uuid
            tvMajor.text = item.major.toString()
            tvMinor.text = item.minor.toString()
            tvDistance.text = "${item.rssi}dB"
            tvRackNo.text = item.rackNo.toString()
            cvDistance.setCardBackgroundColor(ContextCompat.getColor(this.root.context, getColorIndicator(item.rssi)))
        }
    }

    private fun getColorIndicator(rssi: Int): Int =
        if (rssi >= -40) R.color.green
        else if (rssi >= -70) R.color.orange
        else R.color.red
}