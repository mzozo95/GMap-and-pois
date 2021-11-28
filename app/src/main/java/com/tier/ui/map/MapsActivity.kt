package com.tier.ui.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.clustering.ClusterManager
import com.tier.MainApplication.Companion.injector
import com.tier.di.ViewModelFactory
import com.tier.network.model.Vehicle
import com.tier.ui.R
import com.tier.ui.databinding.ActivityMapsBinding
import javax.inject.Inject

class MapsActivity : LocationPermissionBaseActivity(),
    OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MapViewModel

    private lateinit var binding: ActivityMapsBinding

    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<Marker>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.loading.observe(this) { isLoading ->
            binding.tvLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { showError ->
            binding.tvError.visibility = if (showError) View.VISIBLE else View.GONE
        }

        viewModel.firstLocationUpdate.observe(this) { latlng ->
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12.0f))
        }

        viewModel.closestVehicleUpdate.observe(this) { vehicle ->
            renderClosestVehicle(vehicle)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        clusterManager = ClusterManager<Marker>(this, map)
        map.setOnCameraIdleListener(clusterManager);

        viewModel.vehicles.observe(this) { vehicles ->
            renderVehicles(vehicles)
            askForLocationPermission()
        }
    }

    override fun onLocationPermissionGranted() {
        @SuppressLint("MissingPermission")
        map.isMyLocationEnabled = true
        viewModel.startLocationUpdates()
    }

    override fun onDestroy() {
        viewModel.stopLocationUpdates()
        super.onDestroy()
    }

    private fun renderVehicles(vehicles: List<Vehicle>) {
        var count = 0
        vehicles.forEach {
            count++
            clusterManager.addItem(Marker(it.latitude, it.longitude, "Poi: $count"))
        }
    }

    private fun renderClosestVehicle(vehicle: Vehicle?) {
        if (vehicle == null) {
            binding.tvClosestVehicle.visibility = View.GONE
        } else {
            binding.tvClosestVehicle.visibility = View.VISIBLE
            binding.tvClosestVehicle.text =
                String.format(
                    getString(R.string.vehicle_data),
                    vehicle.model,
                    vehicle.state,
                    vehicle.battery
                )
        }
    }
}