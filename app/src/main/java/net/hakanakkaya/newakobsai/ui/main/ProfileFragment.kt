package net.hakanakkaya.newakobsai.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.hakanakkaya.newakobsai.R
import net.hakanakkaya.newakobsai.databinding.FragmentProfileBinding
import net.hakanakkaya.newakobsai.ui.auth.AuthActivity
import net.hakanakkaya.newakobsai.ui.base.BaseFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val TAG = "ProfileFragment"

    override fun createBinding(
        inflater: LayoutInflater, 
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        updateProfileUI()
    }

    override fun setupListeners() {
        // Profil güncelleme butonu
        binding.btnUpdateProfile.setOnClickListener {
            updateProfile()
        }

        // Şifre değiştirme butonu
        binding.btnChangePassword.setOnClickListener {
            changePassword()
        }

        // Çıkış yapma butonu
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun updateProfileUI() {
        val user = getCurrentUser()
        
        if (user != null) {
            // Profil bilgilerini göster
            binding.edtProfileName.setText(getUserDisplayName())
            binding.edtProfileEmail.setText(getUserEmail())
        } else {
            // Kullanıcı giriş yapmamışsa login sayfasına yönlendir
            showToast(getString(R.string.login_required))
            navigateToAuth()
        }
    }

    private fun updateProfile() {
        val name = binding.edtProfileName.text.toString().trim()
        
        if (name.isEmpty()) {
            showToast(getString(R.string.enter_name))
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        // Güncelleme işlemleri burada yapılacak
        // ...
        
        binding.progressBar.visibility = View.GONE
        showToast(getString(R.string.profile_updated))
    }

    private fun changePassword() {
        // Şifre değiştirme işlemleri burada yapılacak
        // ...
        
        showToast(getString(R.string.password_changed))
    }

    private fun logout() {
        binding.progressBar.visibility = View.VISIBLE
        
        try {
            auth.signOut()
            showToast(getString(R.string.logout_success))
            navigateToAuth()
        } catch (e: Exception) {
            showToast(getString(R.string.logout_failed, e.message))
        } finally {
            binding.progressBar.visibility = View.GONE
        }
    }
} 