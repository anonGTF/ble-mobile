package com.jamal.blescanner.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import com.google.android.material.tabs.TabLayoutMediator
import com.jamal.blescanner.R
import com.jamal.blescanner.base.BaseActivity
import com.jamal.blescanner.data.model.BleDeviceModel
import com.jamal.blescanner.data.model.dto.BaseResponse
import com.jamal.blescanner.data.model.dto.DevicesResponse
import com.jamal.blescanner.databinding.ActivityMainBinding
import com.jamal.blescanner.ui.auth.ProfileActivity
import com.jamal.blescanner.utils.TAB_TITLES
import com.jamal.blescanner.utils.Utils.getOrDefault
import com.jamal.blescanner.utils.Utils.toUuid
import dagger.hilt.android.AndroidEntryPoint
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val scanner: BluetoothLeScannerCompat by lazy { BluetoothLeScannerCompat.getScanner() }
    private lateinit var scannerSetting: ScanSettings
    private lateinit var scannerCallback: ScanCallback
    private val sectionPagerAdapter by lazy { SectionPagerAdapter(this) }
    private val scannerFilter: MutableList<ScanFilter> = mutableListOf()
    private val scannedDevices: MutableList<BleDeviceModel> = mutableListOf()
    private var isScanning = false
    private var isFirstTime = true
    private val viewModel: MainViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding =
        ActivityMainBinding::inflate

    override fun setup() {
        setupTabLayout()
        setupBluetoothHelper()
        setupAction()
        viewModel.databaseDevice.observe(this) { setDatabaseDeviceObserver(it) }
    }

    @SuppressLint("MissingPermission")
    private fun setupBluetoothHelper() {
        scannerSetting = ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(1000)
            .setUseHardwareBatchingIfSupported(true)
            .build()
        scannerFilter.add(ScanFilter.Builder().build())
        scannerCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val manufacturerData = result.scanRecord?.manufacturerSpecificData
                Log.d("coba", "onScanResult manufacturer: $manufacturerData")
                Log.d("coba", "onScanResult: $callbackType $result")
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                showToast("Error! code: $errorCode")
                Log.d("coba", "onScanFailed: $errorCode")
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                super.onBatchScanResults(results)
                scannedDevices.clear()
                results.forEach { result ->

                    if (result.device.type != BluetoothDevice.DEVICE_TYPE_LE) return@forEach

                    val manufacturerData = result.scanRecord?.manufacturerSpecificData
                    val manufacturerDataList = mutableListOf<Triple<Int, Int, String>>()
                    try {
                        manufacturerData?.forEach { _, value ->
                            Log.d("coba", "onScanResult ${result.device} manufacturer: $value ${value.size}")

                            if (value.size < 23) return

                            val major = ((value[18].toInt() and 0xFF) shl 8) or (value[19].toInt() and 0xFF)
                            val minor = ((value[20].toInt() and 0xFF) shl 8) or (value[21].toInt() and 0xFF)

                            val uuidBytes = value.sliceArray(2..17)
                            val uuid = uuidBytes.toUuid()
                            manufacturerDataList.add(Triple(major, minor, uuid))
                        }
                    } catch (e: Exception) {
                        return@forEach
                    }
                    val (major, minor, uuid) = manufacturerDataList.getOrDefault(0, Triple(0,0,""))
                    scannedDevices.add(BleDeviceModel(result.device.hashCode(), result.device.name, result.device.toString(), major, minor, uuid, result.rssi, 0, ""))
                }

                getDevices()
            }
        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun setupAction() {
        binding.btnStartScan.setOnClickListener {
            askPermissions(
                Manifest.permission.BLUETOOTH,
//                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_ADMIN,
                onAccepted = {
                    if (isScanning) {
                        binding.btnStartScan.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow))
                        binding.btnStartScan.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
                        binding.scanProgressBar.gone()
                        setTitle("Smart Warehouse")
                        scanner.stopScan(scannerCallback)
                        isFirstTime = true
                    } else {
                        binding.btnStartScan.setImageDrawable(resources.getDrawable(R.drawable.ic_pause))
                        binding.btnStartScan.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
                        binding.scanProgressBar.visible()
                        setTitle("Scanning...")
                        scanner.startScan(scannerFilter, scannerSetting, scannerCallback)
                    }
                    isScanning = !isScanning
                }
            )
        }
    }

    private fun setupTabLayout() {
        binding.viewPager.adapter = sectionPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miProfile -> {
                goToActivity(ProfileActivity::class.java)
                return true
            }
        }

        return false
    }

    private fun getDevices() {
//        if (isFirstTime) viewModel.getDevices().observe(this@MainActivity, setDevicesObserver())
//        isFirstTime = false
        viewModel.getDevices().observe(this@MainActivity, setDevicesObserver())
    }

    private fun setDevicesObserver() = setObserver<BaseResponse<DevicesResponse>>(
        onSuccess = {
            val data = it.data?.content?.devices.orEmpty()
            viewModel.setDatabaseDevice(data)
        },
        onError = {
            showToast(it.message.toString())
        },
        onLoading = {
            showToast("loading")
        }
    )

    private fun setDatabaseDeviceObserver(data: List<BleDeviceModel>) {
        val macMap = data.associateBy { it.mac }
        val databaseDevices = scannedDevices.filter { it.mac in macMap }
        val otherDevices = scannedDevices.filterNot { it.mac in macMap }

        databaseDevices.forEach { device ->
            macMap[device.mac]?.let { updatedDevice ->
                device.id = updatedDevice.id
                device.rackNo = updatedDevice.rackNo
                device.password = updatedDevice.password
            }
        }

        viewModel.setScannedDatabaseDevice(databaseDevices)
        viewModel.setScannedDevice(otherDevices)
    }
}