package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_create.nav_view
import model.WasdappEntry
import java.time.LocalDateTime
import java.util.*

class CreateActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("wasdapps")

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
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun createEntry() {
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

        collection.get().addOnCompleteListener {
            if (it.result!!.isEmpty) {
                entry.id = 0L + 1
                collection.document("1").set(entry)
                val intent = Intent(this, ListActivity::class.java)
                startActivity(intent)
            } else {
                collection.orderBy("id", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener { task->
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
}


