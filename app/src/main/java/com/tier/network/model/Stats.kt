package com.tier.network.model

import com.google.gson.annotations.SerializedName

data class Stats (
	@SerializedName("open") val open : Long,
	@SerializedName("assigned") val assigned : Long,
	@SerializedName("resolved") val resolved : Long
)