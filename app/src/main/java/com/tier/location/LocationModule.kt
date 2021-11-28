package com.tier.location

import android.content.Context
import com.tier.ui.location.MyLocationProvider
import com.tier.ui.location.SimpleLocationProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationModule {

    @Provides
    @Singleton
    fun provideMyLocationProvider(context: Context): MyLocationProvider {
        return SimpleLocationProvider(context)
    }
}