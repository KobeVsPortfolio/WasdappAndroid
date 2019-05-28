package com.example.wasdappapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Base64
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_this_object.*
import kotlinx.android.synthetic.main.activity_this_object.nav_view
import model.User
import model.WasdappEntry
import java.io.IOException
import java.lang.Exception
import java.util.*

class ThisObjectActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    private val currentUser = auth.currentUser
    private val userCollection = db.collection("users")


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setUpMap()
    }


    private fun setUpMap() {
        val wasdappobj = intent.getParcelableExtra("wasdappobj") as WasdappEntry
        if (wasdappobj.lat != null && wasdappobj.lon != null) {
            val latlong = LatLng(wasdappobj.lat!!, wasdappobj.lon!!)
            mMap.addMarker(
                MarkerOptions()
                    .position(latlong)
                    .title(getAddress(latlong))
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 12f))
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
        } catch (e: IOException) {
            Toast.makeText(this, "Not correct", Toast.LENGTH_LONG).show()

        }
        return addressText
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_this_object)

        update_object_button.hide()

        val wasdappobj = intent.getParcelableExtra("wasdappobj") as WasdappEntry

        name_of_this_object.text = wasdappobj.name
        location_of_this_object.text = wasdappobj.locatie
        house_number_of_this_object.text = wasdappobj.nummer
        postal_code_of_this_object.text = wasdappobj.postCode
        town_of_this_object.text = wasdappobj.gemeente
        country_of_this_object.text = wasdappobj.land
        description_of_this_object.text = wasdappobj.omschrijving
        telephone_of_this_object.text = wasdappobj.telefoonNummer
        email_of_this_object.text = wasdappobj.email
        if(wasdappobj.image != null) {
        val bitmap = decoder(wasdappobj.image!!)
            photo_this_object.setImageBitmap(bitmap)
        }

        var checkins: String?= ""
            db.collection("wasdapps/${wasdappobj.id}/checkins").get().addOnSuccessListener {
                for(i in it.documents){
                   checkins += (" " + i["user"] + " checked in at " + i["timestamp"]+ "!!")
                    println(checkins)

                }
                tv_checkins.text = checkins
            }

        btn_checkin.setOnClickListener {
            val addedCheckin = HashMap<String, Any>()
            addedCheckin.put("user", currentUser!!.email!!)
            addedCheckin.put("timestamp", Calendar.getInstance().time)

            db.collection("wasdapps/${wasdappobj.id}/checkins").add(addedCheckin).addOnSuccessListener {
                println("write worked")
            }
        }



        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (!currentUser?.email.isNullOrBlank()) {
            userCollection.document("${currentUser?.email}").get().addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user?.role == "admin") {
                    nav_view_admin.visibility = VISIBLE
                    update_object_button.show()
                } else {
                    nav_view.visibility = VISIBLE
                }
            }
        }else{
            nav_view.visibility = VISIBLE
        }

        nav_view_admin.selectedItemId = R.id.navigation_list
        nav_view_admin.setOnNavigationItemSelectedListener { item ->
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
            when (item.itemId) {
                R.id.admin_users ->
                    startActivity(Intent(this, ListUsersActivity::class.java))
            }
            true
        }

        nav_view.selectedItemId = R.id.navigation_list
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
            intent.putExtra("wasdappobj", wasdappobj)
            startActivity(intent)
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        if (currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun decoder(base64Str: String): Bitmap{
        try {
            val imageBytes = Base64.decode(base64Str, 0)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }catch (e : Exception){
            return BitmapFactory.decodeResource(this.resources, R.drawable.logo_wasdap4)
        }
    }
}
