package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.android.synthetic.main.activity_update.nav_view

class UpdateActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        nav_view.selectedItemId = R.id.navigation_list


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
        cancel_button.setOnClickListener {
        startActivity(Intent(this, ListActivity::class.java))
        finish()
}

         }
}
