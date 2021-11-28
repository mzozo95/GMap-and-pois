package com.tier.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.tier.ui.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {
    companion object {
        private const val apiBaseUrl = "https://api.jsonbin.io/"
        private const val secretKey = BuildConfig.SECRET_KEY
    }

    @Provides
    @Singleton
    fun provideScheduler(): RxSchedulers {
        return RxSchedulers.DEFAULT
    }

    @Provides
    @Singleton
    fun provideMovieApi(): VehicleApi {
        return buildRetrofit(
            buildHttpClient(),
            apiBaseUrl
        ).create(
            VehicleApi::class.java
        )
    }

    private fun buildRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun buildHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(secretKey))
            .addInterceptor(loggingInterceptor)
            .build()
    }
}