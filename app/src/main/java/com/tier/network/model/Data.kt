package com.tier.network.model

import com.google.gson.annotations.SerializedName


data class Data (
	@SerializedName("current") val vehicles : List<Vehicle>,
	@SerializedName("stats") val stats : Stats
)