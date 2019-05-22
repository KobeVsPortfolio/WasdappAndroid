package com.example.wasdappapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import data.SortsListAdapter
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.nav_view
import model.SortModel

class ListActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    private var adapter: SortsListAdapter? = null
    //private var sortList: ArrayList<SortModel>?=null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
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

        val db = FirebaseFirestore.getInstance()

        var sortList = ArrayList<SortModel>()
        db.collection("wasdapps").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        var sortModel = SortModel()
                        var naam = document.data["name"] as String
                        //var locatie = document.data["locatie"] as String

                        sortModel.name = naam
                        // sortModel.location = locatie
                        println("binnen loop= " + document.data)
                        sortList.add(sortModel)
                    }
                    println("Volledige list :" + sortList.toString())

                    layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
                    adapter = SortsListAdapter(sortList!!, this)

                    rcv.layoutManager = layoutManager
                    rcv.adapter = adapter
                    adapter!!.notifyDataSetChanged()
                }
            }

        add_new_object_button.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
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



