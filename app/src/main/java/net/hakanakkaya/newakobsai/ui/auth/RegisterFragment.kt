package net.hakanakkaya.newakobsai.ui.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import net.hakanakkaya.newakobsai.R
import net.hakanakkaya.newakobsai.databinding.FragmentRegisterBinding
import net.hakanakkaya.newakobsai.ui.base.BaseFragment

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    private val TAG = "RegisterFragment"

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        // View'ları başlat
    }

    override fun setupListeners() {
        // Geri butonu
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Kayıt ol butonu
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
        
        // Google ile kayıt butonu
        binding.btnGoogleRegister.setOnClickListener {
            // Google ile kayıt işlemi şu an devre dışı
            showToast(getString(R.string.feature_not_available))
        }
    }

    private fun registerUser() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()
        val confirmPassword = binding.edtConfirmPassword.text.toString()

        // Hataları sıfırla
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
        
        var hasError = false
        
        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.enter_email)
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.invalid_email)
            hasError = true
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.enter_password)
            hasError = true
        } else if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.password_min_length)
            hasError = true
        }
        
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = getString(R.string.enter_confirm_password)
            hasError = true
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.passwords_not_match)
            hasError = true
        }
        
        if (hasError) return

        // Yükleniyor göstergesini göster
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
        binding.btnGoogleRegister.isEnabled = false
        setInputsEnabled(false)

        try {
            // Firebase Auth ile kayıt
            Log.d(TAG, "createUserWithEmail:başlatılıyor")
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    // Yükleniyor göstergesini gizle
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    binding.btnGoogleRegister.isEnabled = true
                    setInputsEnabled(true)

                    if (task.isSuccessful) {
                        // Kayıt başarılı
                        Log.d(TAG, "createUserWithEmail:success")
                        showToast(getString(R.string.register_success))
                        
                        // Ana sayfaya yönlendir
                        navigateToDashboard()
                    } else {
                        // Kayıt başarısız
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthWeakPasswordException -> getString(R.string.error_weak_password)
                            is FirebaseAuthUserCollisionException -> {
                                binding.tilEmail.error = getString(R.string.error_email_already_used)
                                getString(R.string.error_email_already_used)
                            }
                            is FirebaseAuthException -> getString(R.string.error_auth, task.exception?.message)
                            else -> getString(R.string.register_failed, task.exception?.message)
                        }
                        
                        showToast(errorMessage, Toast.LENGTH_LONG)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Auth ile kayıt sırasında hata", e)
            
            // Yükleniyor göstergesini gizle
            binding.progressBar.visibility = View.GONE
            binding.btnRegister.isEnabled = true
            binding.btnGoogleRegister.isEnabled = true
            setInputsEnabled(true)
            
            showToast(getString(R.string.error_occurred, e.message), Toast.LENGTH_LONG)
        }
    }
    
    private fun setInputsEnabled(enabled: Boolean) {
        binding.edtEmail.isEnabled = enabled
        binding.edtPassword.isEnabled = enabled
        binding.edtConfirmPassword.isEnabled = enabled
    }
} 