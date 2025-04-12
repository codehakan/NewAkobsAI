package net.hakanakkaya.newakobsai.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import net.hakanakkaya.newakobsai.R
import net.hakanakkaya.newakobsai.databinding.FragmentWelcomeBinding
import net.hakanakkaya.newakobsai.ui.base.BaseFragment
import net.hakanakkaya.newakobsai.ui.main.DashboardActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val TAG = "WelcomeFragment"

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWelcomeBinding {
        return FragmentWelcomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase Auth kontrolü - zaten BaseFragment'te var
        // Eğer kullanıcı zaten giriş yapmışsa, Dashboard'a yönlendir
        auth.currentUser?.let {
            Log.d(TAG, "Kullanıcı zaten giriş yapmış: ${it.email}")
            navigateToDashboard()
            return
        }

        // Konum sağlayıcısını başlat
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Haritayı yükle
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapContainer) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapContainer, it)
                    .commit()
            }
        mapFragment.getMapAsync(this)

        // Güncel tarih
        updateDateView()
    }

    override fun initViews() {
        // initViews işlemleri onViewCreated'da yapıldığı için boş bırakıyoruz
    }

    override fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }

        // Google login butonunu gizle
        binding.googleLogin.visibility = View.GONE
    }

    private fun updateDateView() {
        val dateFormat = SimpleDateFormat("dd.MM", Locale("tr"))
        val currentDate = dateFormat.format(Date())
        binding.tvUpdateTime.text = getString(R.string.location_update) + " " + currentDate
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        
        // UI ayarları
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        
        // Varsayılan konum (Sivas merkez)
        val sivasCenter = LatLng(39.7477, 37.0173)
        mMap.addMarker(MarkerOptions().position(sivasCenter).title("Konumunuz"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sivasCenter, 15f))

        // Gerçek konum bilgisini almak için izin kontrolü ve konum alma işlemi MainActivity'den alınacak
    }

    override fun navigateToDashboard() {
        val intent = Intent(requireActivity(), DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
} 