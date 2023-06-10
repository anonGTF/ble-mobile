package com.jamal.blescanner.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.util.Log
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleMtuChangedCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleRssiCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.google.common.eventbus.EventBus


class BleManagerUtils {
    private var sBleManager: BleManagerUtils? = null
    private var bleDevices: BleDevice? = null

    fun getInstance(): BleManagerUtils? {
        if (sBleManager == null) {
            sBleManager = BleManagerUtils()
        }
        return sBleManager
    }

    fun connect(bleDevice: BleDevice?) {
        if (BleManager.getInstance().isConnected(bleDevice)) {
            return
        }
        BleManager.getInstance().cancelScan()
        BleManager.getInstance().connect(bleDevice, object : BleGattCallback() {
            override fun onStartConnect() {}

            override fun onConnectFail(bleDevice2: BleDevice?, bleException: BleException?) {
                ToastUtil.showMessage("connect failure")
//                EventBus.getDefault().post(EveMessage(2, ""))
            }

            // com.clj.fastble.callback.BleGattCallback
            override fun onConnectSuccess(
                bleDevice2: BleDevice?,
                bluetoothGatt: BluetoothGatt?,
                i: Int
            ) {
                Log.e(
                    "TAG",
                    "连接成功  模式设置：" + BleManager.getInstance()
                        .requestConnectionPriority(bleDevice2, 1)
                )
                setMtu(bleDevice2, 90)
            }

            override fun onDisConnected(
                z: Boolean,
                bleDevice2: BleDevice?,
                bluetoothGatt: BluetoothGatt?,
                i: Int
            ) {
//                EventBus.getDefault().post(EveMessage(2, ""))
                if (z) LogUtil.e("断开了")
                else LogUtil.e("连接断开")
            }
        })
    }

    fun connectMAC(str: String?) {
        BleManager.getInstance().connect(str, object : BleGattCallback() {
            override fun onStartConnect() {}

            override fun onConnectFail(bleDevice: BleDevice?, bleException: BleException?) {
                ToastUtil.showMessage("连接失败")
            }

            override fun onConnectSuccess(
                bleDevice: BleDevice?,
                bluetoothGatt: BluetoothGatt?,
                i: Int
            ) {
                Log.e("TAG", "连接成功  模式设置：" + BleManager.getInstance().requestConnectionPriority(bleDevice, 1))
                setMtu(bleDevice, 90)
            }

            // com.clj.fastble.callback.BleGattCallback
            @SuppressLint("MissingPermission")
            override fun onDisConnected(
                z: Boolean,
                bleDevice: BleDevice?,
                bluetoothGatt: BluetoothGatt,
                i: Int
            ) {
                LogUtil.e("链接断开")
                bluetoothGatt.close()
            }
        })
    }

    fun setMtu(bleDevice: BleDevice?, i: Int) {
        BleManager.getInstance().setMtu(bleDevice, i, object : BleMtuChangedCallback() {
            override fun onSetMTUFailure(bleException: BleException) {
                LogUtil.e("连接成功mtu:失败")
            }

            override fun onMtuChanged(i2: Int) {
                LogUtil.e("连接成功mtu:$i2")
                notif(bleDevice)
            }
        })
    }

    fun notif(bleDevice: BleDevice?) {
        BleManager.getInstance()
            .notify(bleDevice, UUID, NOTIFY, object : BleNotifyCallback() {
                override fun onNotifySuccess() {
                    LogUtil.e("打开操作成功通知")
                    bleDevices = bleDevice
//                    EventBus.getDefault().post(EveMessage(1, ""))
                }

                override fun onNotifyFailure(bleException: BleException) {
                    LogUtil.e("打开操作失败通知")
                }

                override fun onCharacteristicChanged(bArr: ByteArray) {
                    val bytesToHexString: String = HexUtils.bytesToHexString(bArr).orEmpty()
                    LogUtil.e("原始数据:$bytesToHexString")
                    val HexToInt: Int = HexUtils.HexToInt(bytesToHexString.substring(2, 4))
                    val HexToInt2 = if (bytesToHexString.length > 6) HexUtils.HexToInt(bytesToHexString.substring(6, 8)) else 0

                    LogUtil.e("数据返回mac地址:$bytesToHexString----->数据返回:type:$HexToInt---->value:$HexToInt2")

                    if (HexToInt == 21) {
                        LogUtil.e("开关value:" + bytesToHexString.substring(8))
//                        val eventBus: EventBus = EventBus.getDefault()
//                        eventBus.post(EveMessage(10, bytesToHexString.substring(8) + ""))
                    } else if (HexToInt != 22) {
                        when (HexToInt) {
                            0 -> {
//                                EventBus.getDefault()
//                                    .post(EveMessage(4, Integer.valueOf(HexToInt2)))
                                return
                            }

                            1 -> if (HexToInt2 == 0) {
                                ToastUtil.showMessage("Modify Password success")
                                return
                            } else {
                                ToastUtil.showMessage("Modify Password failure")
                                return
                            }

                            2 -> if (HexToInt2 == 0) {
                                ToastUtil.showMessage("Modify UUID success")
                                return
                            } else {
                                ToastUtil.showMessage("Modify UUID failure")
                                return
                            }

                            3 -> if (HexToInt2 == 0) {
                                ToastUtil.showMessage("Modify Major success")
                                return
                            } else {
                                ToastUtil.showMessage("Modify Major failure")
                                return
                            }

                            4 -> if (HexToInt2 == 0) {
                                ToastUtil.showMessage("Modify Minor success")
                                return
                            } else {
                                ToastUtil.showMessage("Modify Minor failure")
                                return
                            }

                            5 -> if (HexToInt2 == 0) {
                                ToastUtil.showMessage("Modify Name success")
                                return
                            } else {
                                ToastUtil.showMessage("Modify Name failure")
                                return
                            }

                            6 -> if (HexToInt2 == 0) {
                                ToastUtil.showMessage("修改发射功率成功")
                                return
                            } else {
                                ToastUtil.showMessage("修改发射功率失败")
                                return
                            }

                            7 -> if (HexToInt2 == 0) {
                                ToastUtil.showMessage("修改广播间隔成功")
                                return
                            } else {
                                ToastUtil.showMessage("修改广播间隔失败")
                                return
                            }

                            8 -> if (HexToInt2 == 0) {
                                ToastUtil.showMessage("修改一米处Rssi成功")
                                return
                            } else {
                                ToastUtil.showMessage("修改一米处Rssi失败")
                                return
                            }

                            9 -> if (HexToInt2 == 0) {
                                ToastUtil.showMessage("修改Mac成功")
                                return
                            } else {
                                ToastUtil.showMessage("修改Mac失败")
                                return
                            }

                            else -> when (HexToInt) {
                                16 -> {
                                    LogUtil.e("温度value:" + bytesToHexString.substring(8))
//                                    val eventBus2: EventBus = EventBus.getDefault()
//                                    eventBus2.post(
//                                        EveMessage(
//                                            5,
//                                            bytesToHexString.substring(8) + ""
//                                        )
//                                    )
                                    return
                                }

                                17 -> {
                                    LogUtil.e("气压value:" + bytesToHexString.substring(8))
//                                    val eventBus3: EventBus = EventBus.getDefault()
//                                    eventBus3.post(
//                                        EveMessage(
//                                            7,
//                                            bytesToHexString.substring(8) + ""
//                                        )
//                                    )
                                    return
                                }

                                18 -> {
                                    LogUtil.e("湿度value:" + bytesToHexString.substring(8))
//                                    val eventBus4: EventBus = EventBus.getDefault()
//                                    eventBus4.post(
//                                        EveMessage(
//                                            6,
//                                            bytesToHexString.substring(8) + ""
//                                        )
//                                    )
                                    return
                                }

                                19 -> {
                                    LogUtil.e("震动value:" + bytesToHexString.substring(8))
//                                    val eventBus5: EventBus = EventBus.getDefault()
//                                    eventBus5.post(
//                                        EveMessage(
//                                            8,
//                                            bytesToHexString.substring(8) + ""
//                                        )
//                                    )
                                    return
                                }

                                else -> when (HexToInt) {
                                    32 -> {
//                                        EventBus.getDefault().post(EveMessage(32, bytesToHexString))
                                        return
                                    }

                                    33 -> {
//                                        EventBus.getDefault().post(EveMessage(33, bytesToHexString))
                                        return
                                    }

                                    34 -> {
//                                        EventBus.getDefault().post(EveMessage(34, bytesToHexString))
                                        return
                                    }

                                    35 -> {
//                                        EventBus.getDefault().post(EveMessage(35, bytesToHexString))
                                        return
                                    }

                                    36 -> {
//                                        EventBus.getDefault().post(EveMessage(36, bytesToHexString))
                                        return
                                    }

                                    37 -> {
//                                        EventBus.getDefault().post(EveMessage(37, bytesToHexString))
                                        return
                                    }

                                    38 -> {
//                                        EventBus.getDefault().post(EveMessage(38, bytesToHexString))
                                        return
                                    }

                                    39 -> {
//                                        EventBus.getDefault().post(EveMessage(39, bytesToHexString))
                                        return
                                    }

                                    40 -> {
//                                        EventBus.getDefault().post(EveMessage(40, bytesToHexString))
                                        return
                                    }

                                    41 -> {
//                                        EventBus.getDefault().post(EveMessage(41, bytesToHexString))
                                        return
                                    }

                                    42 -> {
//                                        EventBus.getDefault().post(EveMessage(42, bytesToHexString))
                                        return
                                    }

                                    43 -> {
//                                        EventBus.getDefault().post(EveMessage(43, bytesToHexString))
                                        return
                                    }

                                    44 -> {
//                                        EventBus.getDefault().post(EveMessage(44, bytesToHexString))
                                        return
                                    }

                                    45 -> {
//                                        EventBus.getDefault().post(EveMessage(45, bytesToHexString))
                                        return
                                    }

                                    46 -> {
//                                        EventBus.getDefault().post(EveMessage(46, bytesToHexString))
                                        return
                                    }

                                    47 -> {
//                                        EventBus.getDefault().post(EveMessage(47, bytesToHexString))
                                        return
                                    }

                                    48 -> {
//                                        val eventBus6: EventBus = EventBus.getDefault()
//                                        eventBus6.post(
//                                            EveMessage(
//                                                48,
//                                                bytesToHexString.substring(9) + ""
//                                            )
//                                        )
                                        return
                                    }

                                    else -> return
                                }
                            }
                        }
                    } else {
                        LogUtil.e("电量value:" + bytesToHexString.substring(8))
//                        val eventBus7: EventBus = EventBus.getDefault()
//                        eventBus7.post(EveMessage(9, bytesToHexString.substring(8) + ""))
                    }
                }
            })
    }

    fun sendData(str: String, str2: String, i: Int) {
        if (BleManager.getInstance().isConnected(bleDevices)) {
            val str3 = str + HexUtils.addZeroForNum(str2, i)
            LogUtil.e("发送数据:$str3")
            BleManager.getInstance().write(
                bleDevices,
                UUID,
                WRITE,
                HexUtils.toBytes(str3),
                false,
                object : BleWriteCallback() {
                    // from class: com.jx.ibeancon.utils.BleManagerUtils.5
                    // com.clj.fastble.callback.BleWriteCallback
                    override fun onWriteSuccess(i2: Int, i3: Int, bArr: ByteArray) {
                        Log.e("TAG", "发送成功" + HexUtils.bytesToHexString(bArr))
                    }

                    // com.clj.fastble.callback.BleWriteCallback
                    override fun onWriteFailure(bleException: BleException) {
                        Log.e("TAG", "发送失败")
                    }
                })
        }
    }

    fun sendData2(str: String, str2: String, i: Int) {
        if (BleManager.getInstance().isConnected(bleDevices)) {
            val str3 = str + HexUtils.addZeroForNum2(str2, i)
            LogUtil.e("发送数据:$str3")
            BleManager.getInstance().write(
                bleDevices,
                UUID,
                WRITE,
                HexUtils.toBytes(str3),
                false,
                object : BleWriteCallback() {
                    // from class: com.jx.ibeancon.utils.BleManagerUtils.6
                    // com.clj.fastble.callback.BleWriteCallback
                    override fun onWriteSuccess(i2: Int, i3: Int, bArr: ByteArray) {
                        Log.e("TAG", "发送成功" + HexUtils.bytesToHexString(bArr))
                    }

                    // com.clj.fastble.callback.BleWriteCallback
                    override fun onWriteFailure(bleException: BleException) {
                        Log.e("TAG", "发送失败")
                    }
                })
        }
    }

    fun sendData(str: String) {
        if (BleManager.getInstance().isConnected(bleDevices)) {
            LogUtil.e("发送数据:$str")
            BleManager.getInstance().write(
                bleDevices,
                UUID,
                WRITE,
                HexUtils.toBytes(str),
                false,
                object : BleWriteCallback() {
                    // from class: com.jx.ibeancon.utils.BleManagerUtils.7
                    // com.clj.fastble.callback.BleWriteCallback
                    override fun onWriteSuccess(i: Int, i2: Int, bArr: ByteArray) {
                        Log.e("TAG", "发送成功" + HexUtils.bytesToHexString(bArr))
                    }

                    // com.clj.fastble.callback.BleWriteCallback
                    override fun onWriteFailure(bleException: BleException) {
                        Log.e("TAG", "发送失败")
                    }
                })
        }
    }

    fun getRssi() {
        if (BleManager.getInstance().isConnected(bleDevices)) {
            BleManager.getInstance().readRssi(bleDevices, object : BleRssiCallback() {
                // from class: com.jx.ibeancon.utils.BleManagerUtils.8
                // com.clj.fastble.callback.BleRssiCallback
                override fun onRssiFailure(bleException: BleException) {}

                // com.clj.fastble.callback.BleRssiCallback
                override fun onRssiSuccess(i: Int) {
                    val bleManagerUtils = this@BleManagerUtils
                    bleManagerUtils.sendData(
                        "f308f301",
                        HexUtils.IntToHex(Math.abs(i).toString() + ""),
                        2
                    )
                }
            })
        }
    }

}