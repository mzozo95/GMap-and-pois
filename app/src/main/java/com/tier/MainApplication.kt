package com.tier

import android.app.Application
import com.tier.di.AppComponent
import com.tier.di.ContextModule
import com.tier.di.DaggerAppComponent
import com.tier.location.LocationModule
import com.tier.network.NetworkModule

class MainApplication : Application() {
    companion object {
        lateinit var injector: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        injector = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .networkModule(NetworkModule())
            .locationModule(LocationModule())
            .build()
    }
}