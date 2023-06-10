package com.jamal.blescanner.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.jamal.blescanner.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class BleDeviceModel(
    override var id: Int,
    val name: String,
    val mac: String,
    val major: Int = 0,
    val minor: Int = 0,
    val uuid: String,
    val rssi: Int = 0,
    @SerializedName("rack_no")
    var rackNo: Int = 0,
    var password: String
) : BaseModel(id), Parcelable
