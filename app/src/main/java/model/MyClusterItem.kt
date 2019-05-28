package model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem


class MyClusterItem(lat: Double, lng: Double, title: String, tag: WasdappEntry) : ClusterItem {
    private val mPosition: LatLng
    private val mTitle: String = title
    val mTag: WasdappEntry = tag

    init {
        mPosition = LatLng(lat, lng)
    }

    override fun getPosition(): LatLng {
        return mPosition
    }

    override fun getTitle(): String {
        return mTitle
    }

    override fun getSnippet(): String {
        return ""
    }
}