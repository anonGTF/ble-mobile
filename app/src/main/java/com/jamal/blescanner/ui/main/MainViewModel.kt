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

    private val _databaseDevice: MutableLiveData<List<BleDeviceModel>> = MutableLiveData()
    val databaseDevice: LiveData<List<BleDeviceModel>>
        get() = _databaseDevice

    private val _scannedDatabaseDevice: MutableLiveData<List<BleDeviceModel>> = MutableLiveData()
    val scannedDatabaseDevice: LiveData<List<BleDeviceModel>>
        get() = _scannedDatabaseDevice

    private val _scannedDevice: MutableLiveData<List<BleDeviceModel>> = MutableLiveData()
    val scannedDevice: LiveData<List<BleDeviceModel>>
        get() = _scannedDevice

    fun getDevices() = wrapApiWithLiveData(
        apiCall = { api.getDevices() }
    )

    fun setDatabaseDevice(data: List<BleDeviceModel>) {
        _databaseDevice.postValue(data)
    }

    fun setScannedDatabaseDevice(data: List<BleDeviceModel>) {
        _scannedDatabaseDevice.postValue(data)
    }

    fun setScannedDevice(data: List<BleDeviceModel>) {
        _scannedDevice.postValue(data)
    }
}