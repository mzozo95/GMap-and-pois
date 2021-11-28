package com.tier.ui.location

import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.*

interface MyLocationListener {
    fun onLocationArrived(location: Location)
}

interface MyLocationProvider {
    fun setLocationListener(locationListener: MyLocationListener)

    fun startLocationRequest()

    fun stopLocationRequest()
}

class SimpleLocationProvider constructor(private val context: Context) : MyLocationProvider {
    private val fusedLocationClient: FusedLocationProviderClient
    private val locationRequest: LocationRequest
    var myLocationListener: MyLocationListener? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            myLocationListener?.onLocationArrived(lastLocation)
        }
    }

    init {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2500

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    override fun setLocationListener(locationListener: MyLocationListener) {
        myLocationListener = locationListener
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun startLocationRequest() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )
    }

    override fun stopLocationRequest() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}