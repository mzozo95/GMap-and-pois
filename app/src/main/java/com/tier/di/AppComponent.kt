package com.tier.di

import com.tier.location.LocationModule
import com.tier.network.NetworkModule
import com.tier.ui.map.MapsActivity
import com.tier.ui.ViewModelModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ContextModule::class, NetworkModule::class, ViewModelModule::class, LocationModule::class])
interface AppComponent {
    fun inject(mapsActivity: MapsActivity)
}