package com.jamal.blescanner.data.model.dto

import com.jamal.blescanner.data.model.BleDeviceModel

data class DevicesResponse(
    val devices: List<BleDeviceModel>
)
