package com.tier.ui.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class Marker(lat: Double, lng: Double, private val poiTitle: String) : ClusterItem {
    private val position: LatLng = LatLng(lat, lng)

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return poiTitle
    }

    override fun getSnippet(): String? {
        return null
    }
}