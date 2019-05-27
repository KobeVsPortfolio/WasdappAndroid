package com.example.wasdappapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_update.*
import model.User
import model.WasdappEntry
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class UpdateActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("wasdapps")
    private val currentUser = auth.currentUser
    private val userCollection = db.collection("users")
    var latLong: LatLng? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val INTERVAL: Long = 2000
    private val FASTEST_INTERVAL: Long = 1000
    lateinit var mLastLocation: Location
    internal lateinit var mLocationRequest: LocationRequest

    lateinit var photoPath: String
    val REQUEST_TAKE_PHOTO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        if (checkPermissionForLocation(this)) {
            startLocationUpdates()
        }
        mLocationRequest = LocationRequest()

        val wasdappobj = intent.getParcelableExtra("wasdappobj") as WasdappEntry

        name_update.setText(wasdappobj.name)
        location_update.setText(wasdappobj.locatie)
        house_number_update.setText(wasdappobj.nummer)
        postal_code_update.setText(wasdappobj.postCode)
        street_name_update.setText(wasdappobj.straat)
        town_update.setText(wasdappobj.gemeente)
        country_update.setText(wasdappobj.land)
        description_update.setText(wasdappobj.omschrijving)
        telephone_update.setText(wasdappobj.telefoonNummer)
        email_update.setText(wasdappobj.email)
        location_update.setText(wasdappobj.locatie)

        nav_view.visibility = View.VISIBLE
        nav_view_admin.visibility = View.INVISIBLE

        userCollection.document("${currentUser?.email}").get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            if(user?.role == "admin"){
                nav_view.visibility = View.INVISIBLE
                nav_view_admin.visibility = View.VISIBLE
            }
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
        cancel_button.setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
            finish()
        }
        update_button.setOnClickListener {
            handleLocation()
            updateEntry(wasdappobj)
        }

        photoUpdate.setOnClickListener {
            takePicture()
        }

    }

    public override fun onStart() {
        super.onStart()
        if (currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (intent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
            }
            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(this, "com.example.wasdappapp.fileprovider", photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, REQUEST_TAKE_PHOTO)
            }
        }

    }

    private fun updateEntry(entry : WasdappEntry) {
        entry.name = name_update.text.toString()
        entry.locatie = location_update.text.toString()
        entry.straat = street_name_update.text.toString()
        entry.nummer = house_number_update.text.toString()
        entry.postCode = postal_code_update.text.toString()
        entry.gemeente = town_update.text.toString()
        entry.land = country_update.text.toString()
        entry.omschrijving = description_update.text.toString()
        entry.telefoonNummer = telephone_update.text.toString()
        entry.email = email_update.text.toString()
        entry.wijzigDatum = Calendar.getInstance().time
        entry.lat = latLong!!.latitude
        entry.lon = latLong!!.longitude
        entry.image = photoUpdate.text.toString()

        collection.document("${entry.id}").get().addOnCompleteListener {
            if (it.result!!.exists()) {
                collection.document("${entry.id}").set(entry)
                val intent = Intent(this, ListActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "The wasdapp you're trying to edit doesn't seem to exist.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun handleLocation() {
        if (getLatLongFromAdress(postal_code_update.text.toString() + " " + town_update.text.toString() + ", " + street_name_update.text.toString() + " " + house_number_update.text.toString() + ", " + country_update.text.toString()) == LatLng(
                0.0,
                0.0
            )
        ) {
            Toast.makeText(this, "Location not found, using current location.", Toast.LENGTH_SHORT).show()
            latLong = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        } else {
            latLong =
                getLatLongFromAdress(postal_code_update.text.toString() + " " + town_update.text.toString() + ", " + street_name_update.text.toString() + " " + house_number_update.text.toString() + ", " + country_update.text.toString())
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
                    UpdateActivity.LOCATION_PERMISSION_REQUEST_CODE
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val base64 = encoder(photoPath)
            photoUpdate.setTextKeepState(base64)
            photoUpdate.textSize = 0f
            val bitmap = BitmapFactory.decodeFile(photoPath)
            val resizedBitmap = resizeBitmap(bitmap, 512, 512)
            photoUpdateImage.setImageBitmap(resizedBitmap)
        }
    }

    private fun createImageFile(): File? {
        val timeStamp: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            timeStamp,
            ".jpg",
            storageDir
        ).apply { photoPath = absolutePath }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encoder(filePath: String): String {
        val bitmap = BitmapFactory.decodeFile(filePath)
        val resizedBitmap = resizeBitmap(bitmap, 256, 256)
        val stream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(
            bitmap,
            width,
            height,
            false
        )
    }

}
