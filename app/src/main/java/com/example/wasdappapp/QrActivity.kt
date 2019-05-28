package com.example.wasdappapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View.VISIBLE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_qr.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler
import model.User
import model.WasdappEntry


class QrActivity : AppCompatActivity(), ResultHandler {

    private val REQUEST_CAMERA = 1
    private var scannerView: ZXingScannerView? = null
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("wasdapps")
    private val currentUser = auth.currentUser
    private val userCollection = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)

        if (!currentUser?.email.isNullOrBlank()) {
            userCollection.document("${currentUser?.email}").get().addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user?.role == "admin") {
                    nav_view_admin.visibility = VISIBLE
                } else {
                    nav_view.visibility = VISIBLE
                }
            }
        } else {
            nav_view.visibility = VISIBLE
        }

        nav_view_admin.selectedItemId = R.id.navigation_qr_code
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

        nav_view.selectedItemId = R.id.navigation_qr_code
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
        scannerView = findViewById(R.id.scanner)

        if (!checkPermission())
            requestPermisson()

    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this@QrActivity,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermisson() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA)
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()) {
            if (scannerView == null) {
                scannerView = findViewById(R.id.scanner)
                setContentView(scannerView)
            }
            scannerView?.setResultHandler(this)
            scannerView?.startCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView?.stopCamera()
    }

    override fun handleResult(p0: Result?) {
        val result = p0?.text
        val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)

        if (result != null && !result.contains("/")) {
                collection.document(result).get().addOnCompleteListener { task ->
                    if(task.isSuccessful && task.result?.exists()!!) {
                            var entry = task.result?.toObject(WasdappEntry::class.java)!!
                            val intent = Intent(this, ThisObjectActivity::class.java)
                            intent.putExtra("wasdappobj", entry)
                            startActivity(intent)
                    }else {
                        showInvalidQr(result)
                    }
                }
        }else{
            showInvalidQr(result)
        }
    }

    fun showInvalidQr(result: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No wasdapp found with this ID.")
        builder.setPositiveButton("OK") { _, _ ->
            scannerView?.resumeCameraPreview(this@QrActivity)
            startActivity(intent)
        }
        builder.setMessage(result)
        val alert = builder.create()
        alert.show()
    }

    public override fun onStart() {
        super.onStart()
        if (currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}






