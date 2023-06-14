package com.jamal.blescanner.base

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.github.florent37.runtimepermission.PermissionResult
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.jamal.blescanner.utils.Resource


abstract class BaseActivity<VB: ViewBinding>: AppCompatActivity() {
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun setup()

    protected fun <T> setObserver(
        onSuccess: (Resource<T>) -> Unit,
        onError: (Resource<T>) -> Unit = {},
        onLoading: (Resource<T>) -> Unit = {}
    ): Observer<in Resource<T>> {
        return Observer<Resource<T>> { data ->
            when(data) {
                is Resource.Success -> onSuccess(data)
                is Resource.Error -> onError(data)
                is Resource.Loading -> onLoading(data)
            }
        }
    }

    fun goToActivity(
        actDestination: Class<*>,
        data: Bundle? = null,
        clearIntent: Boolean = false,
        isFinish: Boolean = false
    ) {

        val intent = Intent(this, actDestination)

        if (clearIntent) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        data?.let { intent.putExtras(data) }

        startActivity(intent)

        if (isFinish) {
            finish()
        }
    }

    fun goToActivityForResult(resultCode: Int, actDestination:  Class<*>, data: Bundle?) {
        val activityIntent = Intent(this, actDestination)

        data?.let { activityIntent.putExtras(data) }

        startActivityForResult(activityIntent, resultCode)
    }

    fun setActivityResult(resultCode: Int, uri: Uri? = null, data: Bundle? = null) {
        val intent = Intent()
        uri?.let { intent.setData(uri) }
        data?.let { intent.putExtras(data) }
        setResult(resultCode, intent)
        finish()
    }

    protected fun askPermissions(vararg permissions: String, onAccepted: (PermissionResult) -> Unit) {
        askPermission(*permissions) {
            if (it.isAccepted) {
                onAccepted.invoke(it)
            }
        }.onDeclined { e ->
            if (e.hasDenied()) {
                e.denied.forEach { _ ->
                    AlertDialog.Builder(this)
                        .setMessage("Mohon menyetujui permintaan kami")
                        .setPositiveButton("Ya"){ _, _ ->
                            e.askAgain()
                        }
                        .setNegativeButton("Tidak"){ dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            }

            if (e.hasForeverDenied()) {
                Log.d("coba", "askPermissions: ${e.foreverDenied}")
                e.foreverDenied.forEach { _ ->
                    e.goToSettings()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun setupBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    protected fun setTitle(title: String) {
        supportActionBar?.title = title
    }

    protected fun showToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    protected fun showConfirmationDialog(
        title: String,
        message: String,
        positiveButton: String = "Ya",
        negativeButton: String = "Tidak",
        onPositiveClicked: () -> Unit = {},
        onNegativeClicked: () -> Unit = {}
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveButton) { dialog, _ ->
            onPositiveClicked.invoke()
            dialog.dismiss()
        }
        builder.setNegativeButton(negativeButton) { dialog, _ ->
            onNegativeClicked.invoke()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()

    }

    fun View.visible() = run { this.visibility = View.VISIBLE }
    fun View.gone() = run { this.visibility = View.GONE }

    fun getColorResource(resId: Int) = ContextCompat.getColor(this, resId)
}