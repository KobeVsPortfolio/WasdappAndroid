package com.example.wasdappapp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
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


class QrActivity : AppCompatActivity() {

    private var scannerView : ZXingScannerView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        /*
        scannerView=ZXingScannerView(this)
        setContentView(scannerView)

        if(!checkPermission())
            resquestPermission()
*/
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
            false
        }



        /*btn_scan.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()
        }*/

    }/*
    private fun checkPermission() : Boolean{
        return ContextCompat.checkSelfPermission(this@QrActivity,android.Manifest.permission.CAMERA))== PackageManager.PERMISSION_GRANTED
    }

    override fun handleResult(p0: Result?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
*/




/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }*/
}