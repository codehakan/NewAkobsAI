package net.hakanakkaya.newakobsai.ui.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import net.hakanakkaya.newakobsai.R
import net.hakanakkaya.newakobsai.ui.auth.AuthActivity
import net.hakanakkaya.newakobsai.ui.main.DashboardActivity

/**
 * Tüm fragment'ler için temel sınıf.
 * Bu sınıf, tüm fragment'lerde ortak olan işlemleri içerir:
 * - ViewBinding işlemleri için standart yapı
 * - Firebase Auth erişimi
 * - Toast mesajları gösterme yardımcı metodu
 * - Aktiviteler arası geçiş metodları
 * - Kullanıcı bilgilerine erişim metodları
 * 
 * Kullanım:
 * 1. Fragment sınıfınızı BaseFragment<FragmentBinding> şeklinde extend edin
 * 2. createBinding metodunu override ederek binding nesnesini oluşturun
 * 3. initViews ve setupListeners metodlarını override ederek UI işlemlerini yapın
 * 
 * @param <VB> Fragment için kullanılacak ViewBinding sınıfı
 */
abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    companion object {
        private const val TAG = "BaseFragment"
    }

    // ViewBinding için genel değişken
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    // Tüm alt fragment'lerin ortak kullanması için FirebaseAuth instance'ı
    protected val auth: FirebaseAuth by lazy { 
        try {
            FirebaseAuth.getInstance().also {
                Log.d(TAG, "Firebase Auth başarıyla başlatıldı")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Auth başlatma hatası", e)
            showToast(getString(R.string.firebase_init_error, e.message))
            FirebaseAuth.getInstance() // Yine de instance'ı döndür, gerekirse null check yapılabilir
        }
    }

    // Fragment'in view'ını oluşturma işlemi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = createBinding(inflater, container)
        return binding.root
    }

    /**
     * Alt sınıfların binding'i oluşturmak için override edeceği metod
     * Örnek: return FragmentLoginBinding.inflate(inflater, container, false)
     */
    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupListeners()
    }

    /**
     * Fragment'in görünümlerini başlatan metod.
     * Örnek: textView.text = "Merhaba"
     */
    protected abstract fun initViews()

    /**
     * Fragment'teki butonlar vb. için click listener'ları ayarlayan metod
     * Örnek: button.setOnClickListener { doSomething() }
     */
    protected abstract fun setupListeners()

    // Belleği temizlemek için binding'i null yap
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Yardımcı metodlar
    protected fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(requireContext(), message, duration).show()
    }

    protected fun getCurrentUser() = auth.currentUser

    protected fun isUserLoggedIn() = auth.currentUser != null

    protected fun getUserEmail() = auth.currentUser?.email ?: ""

    protected fun getUserDisplayName() = auth.currentUser?.displayName ?: ""

    protected fun getUserId() = auth.currentUser?.uid ?: ""
    
    // Ortak Navigation metotları
    protected open fun navigateToAuth() {
        val intent = Intent(requireActivity(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    protected open fun navigateToDashboard() {
        val intent = Intent(requireActivity(), DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
} 