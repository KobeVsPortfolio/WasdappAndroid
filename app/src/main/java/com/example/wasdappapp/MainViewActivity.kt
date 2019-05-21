package com.example.wasdappapp

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MainViewActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMap()
    }



    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    private fun setUpMap() {
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"))

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE

            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                placeMarkerOnMap(LatLng(location.latitude, location.longitude))
                val currentLangLong = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLangLong, 12f))
            }
        }

        mMap.setOnMapClickListener {
                location ->
            placeMarkerOnMap(location)
        }
    }

    private fun getAddress(latLng: LatLng): String {
        // Create the object
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""
        try {// Asks the geocoder to get the address from the location passed to the method.
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)// If the response contains any address, then append it to a string and return.
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                addressText = address.getAddressLine(0)
            }
        } catch (e: IOException)
        {
            Toast.makeText(this,"Not correct", Toast.LENGTH_LONG).show()

        }
        return addressText
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        val titleStr = getAddress(location)  // add these two lines
        markerOptions.title(titleStr)
        mMap.addMarker(markerOptions)}



override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_view)

        val navigationView = findViewById<View>(R.id.nav_view_main_view) as BottomNavigationView

        navigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.navigation_home ->
                    startActivity(Intent(this, MainViewActivity::class.java))
            }
            when(item.itemId){
                R.id.navigation_list ->
                    startActivity(Intent(this, ListActivity::class.java))
            }
            when(item.itemId){
                R.id.navigation_qr_code ->
                    startActivity(Intent(this, QrActivity::class.java))
            }
            when(item.itemId){
                R.id.navigation_account ->
                    startActivity(Intent(this, AccountActivity::class.java))
            }

            true
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
}
