package com.jamal.blescanner.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.jamal.blescanner.SmartWarehouseApplication


object ToastUtil {
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var toast: Toast? = null
    private val synObj = Any()
    fun showMessage(str: String?) {
        showMessage(str, 0)
    }

    fun showMessage(charSequence: CharSequence?, i: Int) {
        if (charSequence.isNullOrEmpty()) {
            return
        }
        handler.post(Runnable
        // from class: com.jx.ibeancon.utils.ToastUtil.1
        // java.lang.Runnable
        {
            synchronized(synObj) {
                if (toast != null) {
                    toast!!.setText(charSequence)
                    toast!!.duration = i
                } else {
                    toast = Toast.makeText(
                        SmartWarehouseApplication.instance.applicationContext,
                        charSequence,
                        i
                    )
                    val unused = toast
                    toast!!.setGravity(48, 0, 0)
                }
                toast!!.show()
            }
        })
    }

    @JvmOverloads
    fun showMessage(i: Int, i2: Int = 0) {
        handler.post(Runnable
        // from class: com.jx.ibeancon.utils.ToastUtil.2
        // java.lang.Runnable
        {
            synchronized(synObj) {
                if (toast != null) {
                    toast!!.setText(i)
                    toast!!.duration = i2
                } else {
                    toast = Toast.makeText(
                        SmartWarehouseApplication.instance.applicationContext,
                        i,
                        i2
                    )
                    val unused = toast
                }
                toast!!.show()
            }
        })
    }
}