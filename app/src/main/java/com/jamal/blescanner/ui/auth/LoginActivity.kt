package com.jamal.blescanner.ui.auth

import android.view.LayoutInflater
import androidx.activity.viewModels
import com.afollestad.vvalidator.form
import com.jamal.blescanner.base.BaseActivity
import com.jamal.blescanner.data.model.dto.AuthResponse
import com.jamal.blescanner.data.model.dto.BaseResponse
import com.jamal.blescanner.databinding.ActivityLoginBinding
import com.jamal.blescanner.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityLoginBinding =
        ActivityLoginBinding::inflate

    private val viewModel: AuthViewModel by viewModels()

    override fun setup() {
        setTitle("Login")
        if (viewModel.validateLoggedIn()) {
            goToActivity(MainActivity::class.java)
            finish()
        }
        form {
            useRealTimeValidation()
            input(binding.etEmail, name = null) {
                isNotEmpty().description("Email wajib diisi")
                isEmail().description("Silahkan masukan email yang valid!")
            }

            input(binding.etPassword, name = null) {
                isNotEmpty().description("Password wajib diisi")
            }

            submitWith(binding.btnLogin) {
                viewModel.login(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                    .observe(this@LoginActivity, setLoginObserver())
            }
        }
    }

    private fun setLoginObserver() = setObserver<BaseResponse<AuthResponse>>(
        onSuccess = {
            binding.progressBar.gone()
            showToast("Login berhasil")
            goToActivity(MainActivity::class.java)
            finish()
        },
        onError = {
            binding.progressBar.gone()
            showToast(it.message.toString())
        },
        onLoading = { binding.progressBar.visible() }
    )
}