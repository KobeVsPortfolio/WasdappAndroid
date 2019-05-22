package com.example.wasdappapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.zxing.Result
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_qr.*
import kotlinx.android.synthetic.main.activity_qr.nav_view
import me.dm7.barcodescanner.zxing.ZXingScannerView
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler
import org.w3c.dom.Text


class QrActivity : AppCompatActivity(),ResultHandler {

    private val REQUEST_CAMERA =1
    private var scannerView : ZXingScannerView?=null
    private var txtResult : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        nav_view.selectedItemId = R.id.navigation_qr_code
        nav_view.setOnNavigationItemSelectedListener { item ->
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
        scannerView=findViewById(R.id.scanner)

        txtResult = findViewById(R.id.text_result)

        if(!checkPermission())
            requestPermisson()

    }
    private fun checkPermission() : Boolean{
        return ContextCompat.checkSelfPermission(this@QrActivity,android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermisson(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),REQUEST_CAMERA)
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()){
            if (scannerView == null){
                scannerView=findViewById(R.id.scanner)
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
        val vibrator= applicationContext.getSystemService(Context.VIBRATOR_SERVICE)as Vibrator
        vibrator.vibrate(100)
        /*txtResult?.text
        scannerView?.setResultHandler(this)
        scannerView?.startCamera()*/

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Result")
        builder.setPositiveButton("OK"){ dialog,wich ->
           scannerView?.resumeCameraPreview (this@QrActivity)
           startActivity(intent)
           }
        builder.setMessage(result)
        val alert = builder.create()
        alert.show()
        }
    }






