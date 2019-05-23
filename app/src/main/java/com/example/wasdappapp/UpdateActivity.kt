package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.android.synthetic.main.activity_update.nav_view
import model.SortModel

class UpdateActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        nav_view.selectedItemId = R.id.navigation_list

        var wasdappobj = intent.getParcelableExtra("wasdappobj") as SortModel

        name_update.setText(wasdappobj.name)
        location_update.setText(wasdappobj.locatie)
        house_number_update.setText(wasdappobj.nummer)
        postal_code_update.setText(wasdappobj.postCode)
        town_update.setText(wasdappobj.gemeente)
        country_update.setText(wasdappobj.land)
        description_update.setText(wasdappobj.omschrijving)
        telephone_update.setText(wasdappobj.telefoonNummer)
        email_update.setText(wasdappobj.email)
        location_update.setText(wasdappobj.locatie)

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
