package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_this_object.*
import kotlinx.android.synthetic.main.activity_this_object.nav_view
import model.WasdappEntry

class ThisObjectActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_this_object)
        nav_view.selectedItemId = R.id.navigation_list

        var wasdappobj = intent.getParcelableExtra("wasdappobj") as WasdappEntry

        name_of_this_object.text = wasdappobj.name
        location_of_this_object.text = wasdappobj.locatie
        house_number_of_this_object.text = wasdappobj.nummer
        postal_code_of_this_object.text = wasdappobj.postCode
        town_of_this_object.text = wasdappobj.gemeente
        country_of_this_object.text = wasdappobj.land
        description_of_this_object.text = wasdappobj.omschrijving
        telephone_of_this_object.text = wasdappobj.telefoonNummer
        email_of_this_object.text = wasdappobj.email
        location_of_this_object.text = wasdappobj.locatie



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

            intent.putExtra("wasdappobj" , wasdappobj)
            startActivity(intent)
            finish()
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
}
