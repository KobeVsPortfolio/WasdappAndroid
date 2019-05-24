package com.example.wasdappapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_create.nav_view
import model.WasdappEntry
import java.io.IOException
import java.time.LocalDateTime
import java.util.*

class CreateActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("wasdapps")
    var latLong: LatLng? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        nav_view.selectedItemId = R.id.navigation_list

        if (checkPermissionForLocation(this)) {
            startLocationUpdates()
        }
        mLocationRequest = LocationRequest()

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
        cancel_button2.setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
            finish()
        }

        confirm_entry.setOnClickListener {
            handleLocation()
            createEntry()
        }
    }

    private fun handleLocation() {
        if (getLatLongFromAdress(postal_code.text.toString() + " " + town.text.toString() + ", " + street_name.text.toString() + " " + house_number.text.toString() + ", " + country.text.toString()) == LatLng(
                0.0,
                0.0
            )
        ) {
            Toast.makeText(this, "Location not found, using current location.", Toast.LENGTH_SHORT).show()
            latLong = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        } else {
            latLong =
                getLatLongFromAdress(postal_code.text.toString() + " " + town.text.toString() + ", " + street_name.text.toString() + " " + house_number.text.toString() + ", " + country.text.toString())
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

    fun createEntry() {
        val entry = WasdappEntry()
        entry.name = name.text.toString()
        entry.locatie = location.text.toString()
        entry.straat = street_name.text.toString()
        entry.nummer = house_number.text.toString()
        entry.postCode = postal_code.text.toString()
        entry.gemeente = town.text.toString()
        entry.land = country.text.toString()
        entry.omschrijving = description.text.toString()
        entry.telefoonNummer = telephone.text.toString()
        entry.email = email.text.toString()
        entry.aanmaakDatum = Calendar.getInstance().time
        entry.wijzigDatum = Calendar.getInstance().time
        entry.lon = latLong!!.longitude
        entry.lat = latLong!!.latitude

        collection.get().addOnCompleteListener {
            if (it.result!!.isEmpty) {
                entry.id = 0L + 1
                collection.document("1").set(entry)
                val intent = Intent(this, ListActivity::class.java)
                startActivity(intent)
            } else {
                collection.orderBy("id", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener { task ->
                    val lastId = task.result?.toObjects(WasdappEntry::class.java)
                        ?.get(0)?.id
                    if (lastId != null) {
                        entry.id = lastId + 1
                    }
                    if (lastId != null) {
                        collection.document(java.lang.Long.toString(lastId + 1)).set(entry)
                    }
                    val intent = Intent(this, ListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getLatLongFromAdress(adress: String): LatLng {
        if (adress == null) {
            return LatLng(0.0, 0.0)
        }
        var lat: Double? = null
        var lon: Double? = null
        val geocoder = Geocoder(this)
        try {
            var adresses: List<Address> = geocoder.getFromLocationName(adress, 1)
            if (adresses.size > 0) {
                var p: GeoPoint =
                    GeoPoint(adresses.get(0).latitude as Double, adresses.get(0).longitude as Double)
                lat = p.latitude
                lon = p.longitude
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (lat != null && lon != null) {
            return LatLng(lat!!, lon!!)
        }
        return LatLng(0.0, 0.0)

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    CreateActivity.LOCATION_PERMISSION_REQUEST_CODE
                )
                false
            }
        } else {
            true
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mLastLocation != null) {
        }
    }

    protected fun startLocationUpdates() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest!!.setInterval(INTERVAL)
        mLocationRequest!!.setFastestInterval(FASTEST_INTERVAL)

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

}


