package net.hakanakkaya.newakobsai.ui.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import net.hakanakkaya.newakobsai.R
import net.hakanakkaya.newakobsai.databinding.FragmentForgotPasswordBinding
import net.hakanakkaya.newakobsai.ui.base.BaseFragment

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {

    private val TAG = "ForgotPasswordFragment"

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgotPasswordBinding {
        return FragmentForgotPasswordBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        // View'ları başlat
    }

    override fun setupListeners() {
        // Geri butonu
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Şifre sıfırlama bağlantısı gönderme butonu
        binding.btnSendResetLink.setOnClickListener {
            sendPasswordResetEmail()
        }
        
        // İletişim butonu
        binding.tvContactSupport.setOnClickListener {
            showToast(getString(R.string.support_contact), Toast.LENGTH_LONG)
        }
    }

    private fun sendPasswordResetEmail() {
        val email = binding.edtEmail.text.toString().trim()
        
        // Hataları sıfırla
        binding.tilEmail.error = null
        
        var hasError = false
        
        // Email kontrolü
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.enter_email)
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.invalid_email)
            hasError = true
        }
        
        if (hasError) return
        
        // Yükleniyor göstergesini göster
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSendResetLink.isEnabled = false
        binding.edtEmail.isEnabled = false
        
        try {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    // Yükleniyor göstergesini gizle
                    binding.progressBar.visibility = View.GONE
                    binding.btnSendResetLink.isEnabled = true
                    binding.edtEmail.isEnabled = true
                    
                    if (task.isSuccessful) {
                        Log.d(TAG, "Şifre sıfırlama e-postası gönderildi: $email")
                        showToast(getString(R.string.password_reset_sent), Toast.LENGTH_LONG)
                        
                        // Giriş sayfasına dön
                        findNavController().navigateUp()
                    } else {
                        Log.w(TAG, "Şifre sıfırlama e-postası gönderilemedi", task.exception)
                        
                        val errorMessage = task.exception?.message ?: getString(R.string.error_unknown)
                        binding.tilEmail.error = getString(R.string.password_reset_failed, errorMessage)
                    }
                }
        } catch (e: Exception) {
            // Yükleniyor göstergesini gizle
            binding.progressBar.visibility = View.GONE
            binding.btnSendResetLink.isEnabled = true
            binding.edtEmail.isEnabled = true
            
            Log.e(TAG, "Şifre sıfırlama e-postası gönderirken hata", e)
            binding.tilEmail.error = getString(R.string.error_occurred, e.message)
        }
    }
} 