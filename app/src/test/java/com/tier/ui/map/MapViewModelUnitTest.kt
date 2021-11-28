package com.tier.ui.map

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.tier.network.RxSchedulers
import com.tier.network.VehicleApi
import com.tier.network.model.Data
import com.tier.network.model.Stats
import com.tier.network.model.Vehicle
import com.tier.network.model.VehicleInfo
import com.tier.ui.location.MyLocationListener
import com.tier.ui.location.MyLocationProvider
import io.reactivex.Single
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapViewModelUnitTest {
    //Rule for AndroidObservers
    @get:Rule
    var instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: MapViewModel

    @Mock
    lateinit var vehicleApi: VehicleApi

    @Mock
    lateinit var locationProvider: MyLocationProvider

    @Mock
    lateinit var loadingObserver: Observer<Boolean>

    @Mock
    lateinit var vehiclesObserver: Observer<List<Vehicle>>

    @Mock
    lateinit var errorObserver: Observer<Boolean>

    @Mock
    lateinit var firstLocationObserver: Observer<LatLng>

    @Before
    fun init() {
        viewModel = MapViewModel(vehicleApi, locationProvider, RxSchedulers.TEST_SCHEDULER)
    }

    @Test
    fun `fetchVehiclesByLazy observer subscription loads data`() {
        //given
        val vehicleInfo = VehicleInfo(Data(listOf(), Stats(0, 0, 0)))
        Mockito.`when`(vehicleApi.getVehicles()).thenReturn(Single.just(vehicleInfo))

        //when
        viewModel.vehicles.observeForever(vehiclesObserver)

        //then
        verify(vehiclesObserver).onChanged(vehicleInfo.data.vehicles)
    }

    @Test
    fun `fetchVehiclesByLazy sets Loading True Then False`() {
        //given
        val vehicleInfo = VehicleInfo(Data(listOf(), Stats(0, 0, 0)))
        Mockito.`when`(vehicleApi.getVehicles()).thenReturn(Single.just(vehicleInfo))
        viewModel.loading.observeForever(loadingObserver)

        //when
        viewModel.vehicles.observeForever(vehiclesObserver)

        //then
        val captor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(loadingObserver, Mockito.times(2)).onChanged(captor.capture())
        assertTrue(captor.allValues[0])
        assertFalse(captor.allValues[1])
    }

    @Test
    fun `fetchVehiclesByLazy don't call error on success`() {
        val vehicleInfo = VehicleInfo(Data(listOf(), Stats(0, 0, 0)))
        Mockito.`when`(vehicleApi.getVehicles()).thenReturn(Single.just(vehicleInfo))
        viewModel.error.observeForever(errorObserver)

        viewModel.vehicles.observeForever(vehiclesObserver)

        verify(errorObserver, never()).onChanged(true)
    }

    @Test
    fun `fetchVehiclesByLazy calls loading true than false on error`() {
        Mockito.`when`(vehicleApi.getVehicles()).thenReturn(Single.error(Throwable("Error path")))
        viewModel.loading.observeForever(loadingObserver)

        viewModel.vehicles.observeForever(vehiclesObserver)

        val captor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(loadingObserver, Mockito.times(2)).onChanged(captor.capture())
        assertTrue(captor.allValues[0])
        assertFalse(captor.allValues[1])
    }

    @Test
    fun `viewModel startLocationUpdate calls locationProvider and sets up listener on startLocationRequest()`() {
        viewModel.startLocationUpdates()

        verify(locationProvider).startLocationRequest()

        val captor: KArgumentCaptor<MyLocationListener> = argumentCaptor()
        verify(locationProvider, Mockito.times(1)).setLocationListener(captor.capture())
    }

    @Test
    fun `viewModel stopLocationUpdate calls locationProvider stop`() {
        viewModel.stopLocationUpdates()

        verify(locationProvider).stopLocationRequest()
    }

    @Test
    fun `viewModel startLocationUpdate should show and hide loading twice as the vehicles will also download`() {
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

        val vehicleInfo = VehicleInfo(Data(listOf(), Stats(0, 0, 0)))
        Mockito.`when`(vehicleApi.getVehicles()).thenReturn(Single.just(vehicleInfo))

        viewModel = MapViewModel(vehicleApi, locationP, RxSchedulers.TEST_SCHEDULER)
        viewModel.loading.observeForever(loadingObserver)

        //when
        viewModel.startLocationUpdates()

        //then
        val captor: KArgumentCaptor<Boolean> = argumentCaptor()
        verify(loadingObserver, Mockito.times(4)).onChanged(captor.capture())
        assertTrue(captor.allValues[0])
        assertFalse(captor.allValues[1])
        assertTrue(captor.allValues[2])
        assertFalse(captor.allValues[3])
    }

    @Test
    fun `viewModel startLocationUpdate should initiate firstLocationObserver only once`() {
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
                //simulate multiple location arrived calls:
                listener.onLocationArrived(myLocation)
                listener.onLocationArrived(myLocation)
            }

            override fun stopLocationRequest() {}
        }

        val vehicleInfo = VehicleInfo(Data(listOf(), Stats(0, 0, 0)))
        Mockito.`when`(vehicleApi.getVehicles()).thenReturn(Single.just(vehicleInfo))

        viewModel = MapViewModel(vehicleApi, locationP, RxSchedulers.TEST_SCHEDULER)
        viewModel.firstLocationUpdate.observeForever(firstLocationObserver)

        //when
        viewModel.startLocationUpdates()

        //then
        val captor: KArgumentCaptor<LatLng> = argumentCaptor()
        verify(firstLocationObserver, Mockito.times(1)).onChanged(captor.capture())
    }

    //To see location distance tests open in AndroidTest folder: MapViewModelInstrumentationTest.kotlin file
}