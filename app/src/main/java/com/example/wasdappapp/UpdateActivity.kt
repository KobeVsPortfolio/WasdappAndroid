package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.android.synthetic.main.activity_update.nav_view
import model.WasdappEntry
import java.util.*

class UpdateActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("wasdapps")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        nav_view.selectedItemId = R.id.navigation_list

        val wasdappobj = intent.getParcelableExtra("wasdappobj") as WasdappEntry

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

        update_button.setOnClickListener {
            updateEntry(wasdappobj)
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

    fun updateEntry(entry : WasdappEntry) {
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

        collection.document("${entry.id}").get().addOnCompleteListener {
            if (it.result!!.exists()) {
                collection.document("${entry.id}").set(entry)
                val intent = Intent(this, ListActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "The wasdapp you're trying to edit doesn't seem to exist.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
