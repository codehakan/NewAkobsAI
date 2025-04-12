package net.hakanakkaya.newakobsai.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import net.hakanakkaya.newakobsai.R
import net.hakanakkaya.newakobsai.databinding.FragmentHomeBinding
import net.hakanakkaya.newakobsai.ui.base.BaseFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : BaseFragment<FragmentHomeBinding>(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        // Konum izinlerini kontrol et
        checkLocationPermission()
    }

    override fun initViews() {
        // initViews işlemleri onViewCreated'da yapıldığı için boş bırakıyoruz
    }

    override fun setupListeners() {
        // setupListeners işlemleri onViewCreated'da yapıldığı için boş bırakıyoruz
    }

    private fun updateDateView() {
        val dateFormat = SimpleDateFormat("dd.MM", Locale("tr"))
        val currentDate = dateFormat.format(Date())
        binding.tvUpdateTime.text = getString(R.string.location_update) + " " + currentDate
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                showToast(getString(R.string.location_permission_required))
            }
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    
                    // Konum adresini al
                    val geocoder = Geocoder(requireContext(), Locale("tr"))
                    try {
                        val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        if (addresses?.isNotEmpty() == true) {
                            val address = addresses[0]
                            val addressText = address.thoroughfare ?: "" + " " + 
                                    (address.subThoroughfare ?: "")
                            // Adres bilgisini güncelle
                            binding.tvAddress.text = addressText.ifEmpty { "Diriliş mah. 25.sokak" }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    // Haritayı güncelledik
                    if (::mMap.isInitialized) {
                        updateMapWithLocation(latLng)
                    }
                }
            }
        }
    }

    private fun updateMapWithLocation(location: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(location).title("Konumunuz"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        
        // UI ayarları
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        
        // Konum izni varsa konumu göster
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            getLocation()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
} 