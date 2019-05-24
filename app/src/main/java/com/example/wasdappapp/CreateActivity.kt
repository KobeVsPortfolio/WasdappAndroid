package com.example.wasdappapp

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
import android.graphics.drawable.BitmapDrawable
import android.opengl.Visibility
import android.view.View
import kotlinx.android.synthetic.main.activity_cardview.*
import java.io.ByteArrayOutputStream


class CreateActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("wasdapps")

    lateinit var photoPath: String
    val REQUEST_TAKE_PHOTO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
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
        cancel_button2.setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
            finish()
        }

        confirm_entry.setOnClickListener {
            createEntry()
        }
        photo.setOnClickListener {
            takePicture()
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
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val base64 = encoder(photoPath)
            photo.setTextKeepState(base64)
            photo.textSize = 0f
            val bitmap = BitmapFactory.decodeFile(photoPath)
            val resizedBitmap = resizeBitmap(bitmap, 512,512)
            photoImage.setImageBitmap(resizedBitmap)
        }
    }

    private fun createImageFile(): File? {
        val timeStamp: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
        val fileName = timeStamp
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            fileName,
            ".jpg",
            storageDir
        ).apply { photoPath = absolutePath }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun encoder(filePath: String): String {
        val bitmap = BitmapFactory.decodeFile(filePath)
        val resizedBitmap = resizeBitmap(bitmap, 256,256)
        val stream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun resizeBitmap(bitmap: Bitmap, width:Int, height:Int):Bitmap {
        return Bitmap.createScaledBitmap(
            bitmap,
            width,
            height,
            false
        )

    }


}


