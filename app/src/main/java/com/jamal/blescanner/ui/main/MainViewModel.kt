package com.jamal.blescanner.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamal.blescanner.base.BaseViewModel
import com.jamal.blescanner.data.model.BleDeviceModel
import com.jamal.blescanner.data.preferences.Preferences
import com.jamal.blescanner.data.remote.BaseApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val pref: Preferences,
    private val api: BaseApi
) : BaseViewModel() {

    private val _sourceDeviceLists: MutableLiveData<Pair<List<BleDeviceModel>, List<BleDeviceModel>>> = MutableLiveData()
    val sourceDeviceLists: LiveData<Pair<List<BleDeviceModel>, List<BleDeviceModel>>>
        get() = _sourceDeviceLists

    private val _scannedDatabaseDevice: MutableLiveData<List<BleDeviceModel>> = MutableLiveData()
    val scannedDatabaseDevice: LiveData<List<BleDeviceModel>>
        get() = _scannedDatabaseDevice

    private val _scannedOtherDevice: MutableLiveData<List<BleDeviceModel>> = MutableLiveData()
    val scannedOtherDevice: LiveData<List<BleDeviceModel>>
        get() = _scannedOtherDevice

    fun getDevices() = wrapApiWithLiveData(
        apiCall = { api.getDevices() }
    )

    fun setDatabaseDevice(data: List<BleDeviceModel>) {
        val newPair = _sourceDeviceLists.value?.let {
            Pair(data, it.second)
        } ?: Pair(data, emptyList())
        _sourceDeviceLists.postValue(newPair)
    }

    fun setScannedDevice(data: List<BleDeviceModel>) {
        val newPair = _sourceDeviceLists.value?.let {
            Pair(it.first, data)
        } ?: Pair(emptyList(), data)
        _sourceDeviceLists.postValue(newPair)
    }

    fun setScannedDatabaseDevice(data: List<BleDeviceModel>) {
        _scannedDatabaseDevice.postValue(data)
    }

    fun setScannedOtherDevice(data: List<BleDeviceModel>) {
        _scannedOtherDevice.postValue(data)
    }
}