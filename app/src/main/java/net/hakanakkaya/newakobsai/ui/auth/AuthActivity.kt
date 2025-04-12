package net.hakanakkaya.newakobsai.ui.auth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.FirebaseApp
import net.hakanakkaya.newakobsai.R
import net.hakanakkaya.newakobsai.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController
    private val TAG = "AuthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase başlatma
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase başarıyla başlatıldı")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase başlatma hatası", e)
            Toast.makeText(
                this,
                "Firebase başlatma hatası: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }

        // NavHost ve NavController'ı ayarla
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.authNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Action bar'ı varsayılan olarak gizle
        supportActionBar?.hide()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
} 