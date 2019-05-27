package com.example.wasdappapp

import android.app.Activity
import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.content.FileProvider
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_create.nav_view
import model.WasdappEntry
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ProgressBar
import java.io.ByteArrayOutputStream


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

    lateinit var photoPath: String
    val REQUEST_TAKE_PHOTO = 1
    val IMAGE_PICK_REQUEST = 2
    val KEY_IMAGE_URI = "imageUri"
    private val PERMISSION_REQUEST_CODE: Int = 101

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        progressBar.bringToFront()
        progressBar.visibility = View.INVISIBLE

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
        photo.setOnClickListener {
            if (checkPermission()) takePicture() else requestPermission()
        }


        upload.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).also { it.type = "image/*" }
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_IMAGE_URI)) {
            imageUri = savedInstanceState.getParcelable(KEY_IMAGE_URI) as Uri
            imageUri?.let { loadAndShowPhoto(it) }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {

                    takePicture()

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {

            }
        }
    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
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

    private fun createEntry() {
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
        entry.image = photo.text.toString()

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            loadAndShowPhoto(data.data)


        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val base64 = encoder(photoPath)
            photo.setTextKeepState(base64)
            photo.textSize = 0f
            val bitmap = BitmapFactory.decodeFile(photoPath)
            val resizedBitmap = resizeBitmap(bitmap, 512, 512)
            photoImage.setImageBitmap(resizedBitmap)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState != null && imageUri != null) {
            outState.putParcelable(KEY_IMAGE_URI, imageUri)
        }
    }

    private fun loadAndShowPhoto(uri: Uri) {
        progressBar.visibility = View.VISIBLE
        load {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)

        } then {
            val resizedBitmap = resizeBitmap(it, 512, 512)
            photoImage.setImageBitmap(resizedBitmap)
            progressBar.visibility = View.INVISIBLE

            val byteArrayOutputStream = ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            val byteArray = byteArrayOutputStream.toByteArray()
            val encoded = Base64.getEncoder().encodeToString(byteArray)
            photo.setTextKeepState(encoded)
            photo.textSize = 0f

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


