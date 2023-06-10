package com.jamal.blescanner.ui.detail

import com.jamal.blescanner.base.BaseViewModel
import com.jamal.blescanner.data.model.BleDeviceModel
import com.jamal.blescanner.data.preferences.Preferences
import com.jamal.blescanner.data.remote.BaseApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val pref: Preferences,
    private val api: BaseApi
) : BaseViewModel() {

    fun deleteDevice(id: Int) = wrapApiWithLiveData(
        apiCall = { api.deleteDevice(id, pref.userID) }
    )

    fun saveDevice(data: BleDeviceModel) = wrapApiWithLiveData(
        apiCall = {
            api.addDevice(pref.userID, data.name, data.uuid, data.mac, data.major, data.minor, data.rackNo, data.password)
        }
    )

    fun updateDevice(data: BleDeviceModel) = wrapApiWithLiveData(
        apiCall = {
            api.updateDevice(data.id, data.name, data.uuid, data.mac, data.major, data.minor, data.rackNo, data.password)
        }
    )

}