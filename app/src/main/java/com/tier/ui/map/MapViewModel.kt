package com.tier.ui.map

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.tier.network.RxSchedulers
import com.tier.network.VehicleApi
import com.tier.network.model.Vehicle
import com.tier.ui.location.MyLocationListener
import com.tier.ui.location.MyLocationProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MapViewModel
@Inject
constructor(
    private val vehicleApi: VehicleApi,
    private val myLocationProvider: MyLocationProvider,
    private val rxScheduler: RxSchedulers
) : ViewModel() {

    private val disposable = CompositeDisposable()

    val loading: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val error: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val locationUpdate: MutableLiveData<LatLng> = MutableLiveData<LatLng>()
    val firstLocationUpdate: MutableLiveData<LatLng> = MutableLiveData<LatLng>()
    val closestVehicleUpdate: MutableLiveData<Vehicle> = MutableLiveData<Vehicle>()
    val vehicles: MutableLiveData<List<Vehicle>> by lazy {
        val liveData = MutableLiveData<List<Vehicle>>()
        fetchVehicles(liveData);
        return@lazy liveData
    }

    var isFirstLocationUpdate = true

    fun startLocationUpdates() {
        loading.value = true
        myLocationProvider.setLocationListener(object : MyLocationListener {
            override fun onLocationArrived(location: Location) {
                val latLng = LatLng(location.latitude, location.longitude)
                locationUpdate.value = latLng
                if (isFirstLocationUpdate) {
                    isFirstLocationUpdate = false
                    firstLocationUpdate.value = latLng
                }
                loading.value = false
                findClosestVehicle(location)
            }
        })
        myLocationProvider.startLocationRequest()
    }

    fun stopLocationUpdates() {
        myLocationProvider.stopLocationRequest()
    }

    private fun findClosestVehicle(myLocation: Location) {
        var minimumDistance = Float.MAX_VALUE;
        var closestVehicle: Vehicle? = null

        vehicles.value?.forEach { vehicle ->
            val item = Location("")
            item.latitude = vehicle.latitude
            item.longitude = vehicle.longitude
            val currentDistance = myLocation.distanceTo(item)
            if (currentDistance < minimumDistance) {
                minimumDistance = currentDistance
                closestVehicle = vehicle
            }
        }

        if (closestVehicle != null) {
            this.closestVehicleUpdate.value = closestVehicle
        }
    }

    private fun fetchVehicles(liveData: MutableLiveData<List<Vehicle>>) {
        loading.value = true;
        disposable.add(
            vehicleApi.getVehicles()
                .retry(2)
                .compose(rxScheduler.applySchedulers())
                .subscribe(
                    { value ->
                        loading.value = false
                        liveData.value = value.data.vehicles
                        error.value = false
                    },
                    {
                        loading.value = false;
                        error.value = true
                    }
                ))
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}
