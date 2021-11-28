package com.tier.ui.map

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.tier.network.RxSchedulers
import com.tier.network.VehicleApi
import com.tier.network.model.Data
import com.tier.network.model.Stats
import com.tier.network.model.Vehicle
import com.tier.network.model.VehicleInfo
import com.tier.ui.location.MyLocationListener
import com.tier.ui.location.MyLocationProvider
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapViewModelInstrumentationTest {
    //Rule for AndroidObservers
    @get:Rule
    var instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: MapViewModel

    @Mock
    lateinit var vehicleApi: VehicleApi

    @Mock
    lateinit var closestVehiclesObserver: Observer<Vehicle>

    //Must be android test because of Android.location.distance inner call which otherwise returns always null/0
    @Suppress("IllegalIdentifier")
    @Test
    fun `viewModel startLocationUpdate should initiate closestVehicleObserver_onchange with the closest vehicle`() {
        //given
        val locationP = object : MyLocationProvider {
            lateinit var listener: MyLocationListener
            override fun setLocationListener(locationListener: MyLocationListener) {
                listener = locationListener
            }

            override fun startLocationRequest() {
                val myLocation = Location("test")
                myLocation.latitude = 0.1
                myLocation.longitude = 0.1
                listener.onLocationArrived(myLocation)
            }

            override fun stopLocationRequest() {}
        }

        val vehicle1 = createVehicleWithPosition("1",0.11,0.11)
        val vehicle2 = createVehicleWithPosition("2",20.0,20.0)
        val vehicle3 = createVehicleWithPosition("3",30.0,30.0)

        val vehicles = listOf(vehicle2, vehicle1, vehicle3)
        val vehicleInfo = VehicleInfo(Data(vehicles, Stats(0, 0, 0)))
        Mockito.`when`(vehicleApi.getVehicles()).thenReturn(Single.just(vehicleInfo))

        viewModel = MapViewModel(vehicleApi, locationP, RxSchedulers.TEST_SCHEDULER)
        viewModel.closestVehicleUpdate.observeForever(closestVehiclesObserver)

        //when
        viewModel.startLocationUpdates()

        //then
        Mockito.verify(closestVehiclesObserver).onChanged(vehicle1)
    }

    fun createVehicleWithPosition(id: String, latitude: Double, longitude: Double):Vehicle{
        return Vehicle(id, "", 0L, "", "", "", "", 99, "", "", 11, latitude, longitude)
    }
}