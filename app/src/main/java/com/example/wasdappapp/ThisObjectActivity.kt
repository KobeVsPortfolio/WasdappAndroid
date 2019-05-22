package com.example.wasdappapp

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_this_object.*
import kotlinx.android.synthetic.main.activity_this_object.nav_view
import model.SortModel
import java.io.IOException

class ThisObjectActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMap()
    }


    private fun setUpMap() {
        val wasdappobj = intent.getParcelableExtra("wasdappobj") as SortModel
        val latlong = LatLng(wasdappobj.lat!!, wasdappobj.lon!!)
        mMap.addMarker(
            MarkerOptions()
                .position(latlong)
                .title(getAddress(latlong)))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 12f))

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


    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_this_object)
        nav_view.selectedItemId = R.id.navigation_list

        val wasdappobj = intent.getParcelableExtra("wasdappobj") as SortModel

        name_of_this_object.text = wasdappobj.name
        location_of_this_object.text = wasdappobj.locatie
        house_number_of_this_object.text = wasdappobj.nummer
        postal_code_of_this_object.text = wasdappobj.postCode
        town_of_this_object.text = wasdappobj.gemeente
        country_of_this_object.text = wasdappobj.land
        description_of_this_object.text = wasdappobj.omschrijving
        telephone_of_this_object.text = wasdappobj.telefoonNummer
        email_of_this_object.text = wasdappobj.email
        location_of_this_object.text = wasdappobj.locatie

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        nav_view.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home ->
                    startActivity(Intent(this, MainViewActivity::class.java))
            }
            when (item.itemId) {
                R.id.navigation_list ->
                    startActivity(Intent(this, ListActivity::class.java))
            }
            when (item.itemId) {
                R.id.navigation_qr_code ->
                    startActivity(Intent(this, QrActivity::class.java))
            }
            when (item.itemId) {
                R.id.navigation_account ->
                    startActivity(Intent(this, AccountActivity::class.java))
            }
            true
        }
        update_object_button.setOnClickListener {
            val intent = Intent(this, UpdateActivity::class.java)

            intent.putExtra("wasdappobj" , wasdappobj)
            startActivity(intent)
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
