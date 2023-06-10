package com.jamal.blescanner.ui.auth

import android.view.LayoutInflater
import androidx.activity.viewModels
import com.jamal.blescanner.base.BaseActivity
import com.jamal.blescanner.data.model.dto.BaseResponse
import com.jamal.blescanner.data.model.dto.LogoutResponse
import com.jamal.blescanner.data.model.dto.UserResponse
import com.jamal.blescanner.databinding.ActivityProfileBinding
import com.jamal.blescanner.utils.Utils.orZero
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    override val bindingInflater: (LayoutInflater) -> ActivityProfileBinding =
        ActivityProfileBinding::inflate

    private val viewModel: AuthViewModel by viewModels()

    override fun setup() {
        setTitle("Profil")
        viewModel.getProfile().observe(this, setProfileObserver())
        binding.btnLogout.setOnClickListener { viewModel.logout().observe(this, setLogoutObserver()) }
    }

    private fun setProfileObserver() = setObserver<BaseResponse<UserResponse>>(
        onSuccess = {
            val user = it.data?.content?.user
            with(binding) {
                tvName.text = user?.name.orEmpty()
                tvNip.text = user?.nip.orZero().toString()
                tvEmail.text = user?.email.orEmpty()
            }
        },
        onError = {
            binding.progressBar.gone()
            showToast(it.message.toString())
        },
        onLoading = { binding.progressBar.visible() }
    )

    private fun setLogoutObserver() = setObserver<BaseResponse<LogoutResponse>>(
        onSuccess = {
            showToast("Berhasil logout!")
            goToActivity(LoginActivity::class.java, null, clearIntent = true, isFinish = true)
        },
        onError = {
            binding.progressBar.gone()
            showToast(it.message.toString())
        },
        onLoading = { binding.progressBar.visible() }
    )
}