package com.jamal.blescanner.ui.detail

import android.app.ProgressDialog
import android.bluetooth.BluetoothGatt
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleMtuChangedCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.jamal.blescanner.base.BaseActivity
import com.jamal.blescanner.data.model.BleDeviceModel
import com.jamal.blescanner.data.model.dto.BaseResponse
import com.jamal.blescanner.data.model.dto.DeleteResponse
import com.jamal.blescanner.data.model.dto.DeviceResponse
import com.jamal.blescanner.databinding.ActivityDetailBinding
import com.jamal.blescanner.ui.main.MainActivity
import com.jamal.blescanner.utils.HexUtils
import com.jamal.blescanner.utils.HexUtils.HexToInt
import com.jamal.blescanner.utils.HexUtils.bytesToHexString
import com.jamal.blescanner.utils.HexUtils.toBytes
import com.jamal.blescanner.utils.NOTIFY
import com.jamal.blescanner.utils.PREFIX_MAJOR
import com.jamal.blescanner.utils.PREFIX_MINOR
import com.jamal.blescanner.utils.PREFIX_NAME
import com.jamal.blescanner.utils.UUID
import com.jamal.blescanner.utils.Utils.orFalse
import com.jamal.blescanner.utils.WRITE
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailActivity : BaseActivity<ActivityDetailBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityDetailBinding =
        ActivityDetailBinding::inflate

    private val viewModel: DetailViewModel by viewModels()
    private var isSaved = false
    private var editMode = false
    private var bleDevice: BleDevice? = null
    private lateinit var data: BleDeviceModel

    override fun setup() {
        setupBackButton()
        setTitle("Detail BLE")
        data = intent.extras?.getParcelable(EXTRA_DATA)!!
        isSaved = intent.extras?.getBoolean(EXTRA_SAVED).orFalse()

        populateData()
        setupAction()
    }

    private fun populateData() {
        Log.d("coba", "populateData: $data")
        with(binding) {
            tvRackNo.text = data.rackNo.toString()
            tvName.text = data.name
            tvMac.text = data.mac
            tvUuid.text = data.uuid
            tvMajor.text = data.major.toString()
            tvMinor.text = data.minor.toString()
            tvDistance.text = "${data.rssi}dB"

            etName.setText(data.name)
            etMajor.setText(data.major.toString())
            etMinor.setText(data.minor.toString())
            etRackNo.setText(data.rackNo.toString())
        }
    }

    private fun setupAction() {
        if (isSaved) {
            binding.btnDelete.visible()
            binding.btnDelete.setOnClickListener { handleDelete() }
        } else {
            binding.btnDelete.gone()
        }
        binding.btnConnect.setOnClickListener { handleConnect() }
        binding.btnSave.setOnClickListener { handleSave() }
        binding.btnCancel.setOnClickListener { reset() }
        binding.btnSaveName.setOnClickListener {
            val name = binding.etName.text.toString()
            write(HexUtils.str2Hex(name), "name", PREFIX_NAME, 24)
        }
        binding.btnSaveMajor.setOnClickListener {
            val major = binding.etMajor.text.toString()
            write(HexUtils.IntToHex(major), "major", PREFIX_MAJOR, 4, true)
        }
        binding.btnSaveMinor.setOnClickListener {
            val minor = binding.etMinor.text.toString()
            write(HexUtils.IntToHex(minor), "minor", PREFIX_MINOR, 4, true)
        }
    }

    private fun handleDelete() {
        showConfirmationDialog(
            "Hapus BLE dari Gudang",
        "Anda yakin ingin menghapus BLE ini dari gudang?",
            onPositiveClicked = {
                viewModel.deleteDevice(data.id).observe(this, setDeleteObserver())
            }
        )
    }

    private fun setDeleteObserver() = setObserver<BaseResponse<DeleteResponse>>(
        onSuccess = {
            setTitle("Detail BLE")
            binding.progressBar.gone()
            showToast("device deleted")
            goToActivity(MainActivity::class.java, null, clearIntent = true, isFinish = true)
        },
        onError = {
            setTitle("Detail BLE")
            binding.progressBar.gone()
            showToast(it.message.toString())
        },
        onLoading = {
            setTitle("Deleting...")
            binding.progressBar.visible()
        }
    )

    private fun handleConnect() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Connecting to the device")
        BleManager.getInstance().connect(data.mac, object : BleGattCallback() {
            override fun onStartConnect() {
                progressDialog.show()
                Log.d("coba", "onStartConnect: ${data.mac}")
            }

            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                progressDialog.dismiss()
                showToast("Connection failed: $exception")
                Log.d("coba", "onConnectFail: $bleDevice $exception")
            }

            override fun onConnectSuccess(
                bleDevice: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                if (bleDevice == null) {
                    showToast("Device not found!")
                    return
                }
                progressDialog.dismiss()
                showToast("Successfully connected!")

                editMode = true
                binding.llAction.gone()
                binding.llActionEdit.visible()
                binding.llData.gone()
                binding.svEdit.visible()

                BleManager.getInstance().requestConnectionPriority(bleDevice, 1)
                setMtu(bleDevice, 50)
                Log.d("coba", "onConnectSuccess: $status $gatt $bleDevice")
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                device: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                showToast("Disconnected")
                Log.d("coba", "onDisConnected: $isActiveDisConnected $status")
            }

        })
    }

    private fun handleSave() {
        val name = binding.etName.text.toString()
        val major = binding.etMajor.text.toString().toInt()
        val minor = binding.etMinor.text.toString().toInt()
        val rackNo = binding.etRackNo.text.toString().toInt()

        if (isSaved) {
            viewModel.updateDevice(BleDeviceModel(
                    data.id, name, data.mac, major, minor, data.uuid, data.rssi, rackNo, ""))
                .observe(this, setSaveObserver())
        } else {
            viewModel.saveDevice(BleDeviceModel(
                data.id, name, data.mac, major, minor, data.uuid, data.rssi, rackNo, ""))
                .observe(this, setSaveObserver())
        }
        reset()
    }

    private fun setSaveObserver() = setObserver<BaseResponse<DeviceResponse>>(
        onSuccess = {
            setTitle("Detail BLE")
            binding.progressBar.gone()
            showToast("Device saved")
            it.data?.content?.device?.let { device -> data = device }
            populateData()
        },
        onError = {
            setTitle("Detail BLE")
            binding.progressBar.gone()
            showToast(it.message.toString())
        },
        onLoading = {
            setTitle("Saving...")
            binding.progressBar.visible()
        }
    )

    private fun reset() {
        editMode = false
        binding.llAction.visible()
        binding.llActionEdit.gone()
        binding.llData.visible()
        binding.svEdit.gone()
    }

    private fun setMtu(bleDevice: BleDevice, mtu: Int) {
        BleManager.getInstance().setMtu(bleDevice, mtu, object : BleMtuChangedCallback() {
            override fun onSetMTUFailure(exception: BleException?) {
                showToast("Set MTU Failed")
                Log.d("coba", "onSetMTUFailure: $exception")
            }

            override fun onMtuChanged(mtu: Int) {
                showToast("Successfully changed MTU: $mtu")
                Log.d("coba", "onMtuChanged: $mtu")
                setupNotify(bleDevice)
            }

        })
    }

    private fun setupNotify(bleDevice: BleDevice) {
        BleManager.getInstance().notify(bleDevice, UUID, NOTIFY, object : BleNotifyCallback() {
            override fun onNotifySuccess() {
                this@DetailActivity.bleDevice = bleDevice
                showToast("Success Open Operation Notification!")
            }

            override fun onNotifyFailure(exception: BleException?) {
                showToast("Failed Open Operation Notification!")
            }

            override fun onCharacteristicChanged(data: ByteArray?) {
                val bytesToHexString = bytesToHexString(data)
                val type = HexToInt(bytesToHexString!!.substring(2, 4))
                val value = if (bytesToHexString.length > 6) HexToInt(
                    bytesToHexString.substring(6, 8)
                ) else 0
                Log.d(
                    "coba",
                    "onCharacteristicChanged: $bytesToHexString type: $type value: $value"
                )
                if (type != 22) {
                    when (type) {
                        1 -> {
                            if (value == 0) {
                                showToast("Modify Password success")
                                return
                            } else {
                                showToast("Modify Password failure")
                                return
                            }
                        }

                        2 -> {
                            if (value == 0) {
                                showToast("Modify UUID success")
                                return
                            } else {
                                showToast("Modify UUID failure")
                                return
                            }
                        }

                        3 -> {
                            if (value == 0) {
                                showToast("Modify Major success")
                                return
                            } else {
                                showToast("Modify Major failure")
                                return
                            }
                        }

                        4 -> {
                            if (value == 0) {
                                showToast("Modify Minor success")
                                return
                            } else {
                                showToast("Modify Minor failure")
                                return
                            }
                        }

                        5 -> {
                            if (value == 0) {
                                showToast("Modify Name success")
                                return
                            } else {
                                showToast("Modify Name failure")
                                return
                            }
                        }

                        6 -> {
                            if (value == 0) {
                                showToast("修改发射功率成功")
                                return
                            } else {
                                showToast("修改发射功率失败")
                                return
                            }
                        }

                        7 -> {
                            if (value == 0) {
                                showToast("修改广播间隔成功")
                                return
                            } else {
                                showToast("修改广播间隔失败")
                                return
                            }
                        }

                        8 -> {
                            if (value == 0) {
                                showToast("修改一米处Rssi成功")
                                return
                            } else {
                                showToast("修改一米处Rssi失败")
                                return
                            }
                        }

                        9 -> {
                            if (value == 0) {
                                showToast("修改Mac成功")
                                return
                            } else {
                                showToast("修改Mac失败")
                                return
                            }
                        }
                    }
                }
            }
        })
    }

    private fun write(value: String, name: String, prefix: String, length: Int, isPrefixed: Boolean = false) {
        val data = prefix + HexUtils.addZeroForNum(value, length, isPrefixed)
        Log.d("coba", "send data: $data")
        BleManager.getInstance().write(
            bleDevice,
            UUID,
            WRITE,
            toBytes(data),
            false,
            object : BleWriteCallback() {
                override fun onWriteSuccess(i2: Int, i3: Int, bArr: ByteArray) {
                    Log.d("TAG", "send successfully" + bytesToHexString(bArr))
                }

                override fun onWriteFailure(bleException: BleException) {
                    Log.e("TAG", "failed to send ${bleException.description}")
                }
            })
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_SAVED = "extra_saved"
    }
}