package com.jamal.blescanner.ui.detail

import android.app.ProgressDialog
import android.bluetooth.BluetoothGatt
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleMtuChangedCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.jamal.blescanner.base.BaseActivity
import com.jamal.blescanner.data.model.BleDeviceModel
import com.jamal.blescanner.data.model.dto.BaseResponse
import com.jamal.blescanner.data.model.dto.DeleteResponse
import com.jamal.blescanner.data.model.dto.DeviceResponse
import com.jamal.blescanner.databinding.ActivityDetailBinding
import com.jamal.blescanner.utils.HexUtils
import com.jamal.blescanner.utils.HexUtils.bytesToHexString
import com.jamal.blescanner.utils.HexUtils.toBytes
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
        onSuccess = { showToast("device deleted") }
    )

    private fun handleConnect() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Connecting...")
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
                progressDialog.dismiss()
                showToast("Successfully connected!")
                this@DetailActivity.bleDevice = bleDevice

                editMode = true
                binding.llAction.gone()
                binding.llActionEdit.visible()
                binding.llData.gone()
                binding.svEdit.visible()

                BleManager.getInstance().requestConnectionPriority(bleDevice, 1)
                setMtu(50)
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
        BleManager.getInstance().disableBluetooth()
        BleManager.getInstance().enableBluetooth()
    }

    private fun setSaveObserver() = setObserver<BaseResponse<DeviceResponse>>(
        onSuccess = { showToast("Device saved") }
    )

    private fun reset() {
        editMode = false
        binding.llAction.visible()
        binding.llActionEdit.gone()
        binding.llData.visible()
        binding.svEdit.gone()
    }

    private fun setMtu(mtu: Int) {
        BleManager.getInstance().setMtu(bleDevice, mtu, object : BleMtuChangedCallback() {
            override fun onSetMTUFailure(exception: BleException?) {
                showToast("Set MTU Failed")
                Log.d("coba", "onSetMTUFailure: $exception")
            }

            override fun onMtuChanged(mtu: Int) {
                showToast("Successfully changed MTU: $mtu")
                Log.d("coba", "onMtuChanged: $mtu")
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
                    showToast("Successfully edit $name")
                    Log.d("TAG", "send successfully" + bytesToHexString(bArr))
                }

                override fun onWriteFailure(bleException: BleException) {
                    showToast("Failed to edit $name")
                    Log.e("TAG", "failed to send ${bleException.description}")
                }
            })
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_SAVED = "extra_saved"
    }
}