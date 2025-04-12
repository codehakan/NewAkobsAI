package net.hakanakkaya.newakobsai.ui.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import net.hakanakkaya.newakobsai.R
import net.hakanakkaya.newakobsai.databinding.FragmentLoginBinding
import net.hakanakkaya.newakobsai.ui.base.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val TAG = "LoginFragment"

    override fun createBinding(
        inflater: LayoutInflater, 
        container: ViewGroup?
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        // Google ile giriş butonu devre dışı bırakılıyor
        binding.btnGoogleLogin.visibility = View.GONE
    }

    override fun setupListeners() {
        // Giriş butonunu ayarla
        binding.btnLogin.setOnClickListener {
            loginWithEmail()
        }
        
        // Şifremi Unuttum yönlendirmesi
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        
        // Kayıt olma butonunu ayarla
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun loginWithEmail() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()

        // Hataları sıfırla
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        
        var hasError = false

        // Boş alan kontrolü
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
        }
        
        if (hasError) return

        // Yükleniyor göstergesini göster
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
        setInputsEnabled(false)

        try {
            Log.d(TAG, "signInWithEmail:başlatılıyor")
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    // Yükleniyor göstergesini gizle
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    setInputsEnabled(true)

                    if (task.isSuccessful) {
                        // Giriş başarılı
                        Log.d(TAG, "signInWithEmail:success")
                        showToast(getString(R.string.login_success))
                        navigateToDashboard()
                    } else {
                        // Giriş başarısız
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        
                        when (task.exception) {
                            is FirebaseAuthInvalidUserException -> {
                                binding.tilEmail.error = getString(R.string.error_user_not_found)
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                binding.tilPassword.error = getString(R.string.error_invalid_credentials)
                            }
                            else -> {
                                val errorMessage = getString(R.string.login_failed) + ": ${task.exception?.message}"
                                showToast(errorMessage, Toast.LENGTH_LONG)
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Auth ile giriş sırasında hata", e)
            
            // Yükleniyor göstergesini gizle
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.isEnabled = true
            setInputsEnabled(true)
            
            showToast(getString(R.string.error_occurred, e.message), Toast.LENGTH_LONG)
        }
    }
    
    private fun setInputsEnabled(enabled: Boolean) {
        binding.edtEmail.isEnabled = enabled
        binding.edtPassword.isEnabled = enabled
    }
} 