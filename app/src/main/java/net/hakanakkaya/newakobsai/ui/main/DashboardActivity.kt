package net.hakanakkaya.newakobsai.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import net.hakanakkaya.newakobsai.R
import net.hakanakkaya.newakobsai.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val TAG = "DashboardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
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
            .findFragmentById(R.id.mainNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        // Bottom Navigation için kök ekranları belirle
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, 
                R.id.profileFragment, 
                R.id.emergencyFragment
            )
        )

        // Action bar'ı gizle (Material Design BottomNavigationView ile kullanılması daha iyi)
        supportActionBar?.hide()
        
        // Bottom Navigation'ı navController ile bağla
        binding.bottomNavigation.setupWithNavController(navController)

        // Geri tuşu davranışını özelleştir
        setupBackPressedHandler()
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Ana fragment'te miyiz (navigation graph'ın startDestination'ında mıyız) kontrol et
                if (isStartDestinationFragment()) {
                    // Ana fragment'teyiz, çıkış diyalogu göster
                    showExitConfirmationDialog()
                } else {
                    // Ana fragment'te değiliz, normal geri navigasyonu yap
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })
    }

    private fun isStartDestinationFragment(): Boolean {
        // Navigation graph'ın startDestination'ı olan R.id.homeFragment kontrolü
        val navGraph = navController.graph
        return navController.currentDestination?.id == navGraph.startDestinationId
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Çıkış")
            .setMessage("Gardaş çıkmak istiyon mu essahtan laaa")
            .setPositiveButton("He") { _, _ ->
                // Uygulamayı kapat
                finish()
            }
            .setNegativeButton("Yoh") { dialog, _ ->
                // Diyalogu kapat, hiçbir şey yapma
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
} 