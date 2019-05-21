package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.View
import android.widget.TextView

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val navigationView = findViewById<View>(R.id.nav_view_account) as BottomNavigationView

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
    }
    }
