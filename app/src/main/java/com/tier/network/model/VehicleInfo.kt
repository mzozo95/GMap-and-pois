package com.tier.network.model

import com.google.gson.annotations.SerializedName


data class VehicleInfo(
    //or use custom deserializer, to not have plus objects: https://stackoverflow.com/questions/56614221/how-can-i-serialize-a-nested-object-using-gson-serializedname-in-java
    @SerializedName("data") val data : Data
)