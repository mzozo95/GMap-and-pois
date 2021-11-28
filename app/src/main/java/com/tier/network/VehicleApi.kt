package com.tier.network

import com.tier.network.model.VehicleInfo;

import io.reactivex.Observable;
import io.reactivex.Single
import retrofit2.http.GET;

interface VehicleApi {
    //https://api.jsonbin.io/b/5fa8ff8dbd01877eecdb898f
    @GET("/b/5fa8ff8dbd01877eecdb898f")
    fun getVehicles(): Single<VehicleInfo>
}