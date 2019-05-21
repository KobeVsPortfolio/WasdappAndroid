package com.example.wasdappapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import data.SortsListAdapter
import kotlinx.android.synthetic.main.activity_list.*
import model.SortModel

class ListActivity : AppCompatActivity() {
    private var adapter: SortsListAdapter?=null
    private var sortList: ArrayList<SortModel>?=null
    private var layoutManager: RecyclerView.LayoutManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

            sortList = ArrayList<SortModel>()
            layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
            adapter = SortsListAdapter(sortList!!, this)

            rcv.layoutManager= layoutManager
            rcv.adapter = adapter
            var nameList:Array<String> = arrayOf("Koffie Machine", "Frisdrank Automaat", "Snack Automaat")
            var locationList:Array<String> = arrayOf("Realdolmen", "Realdolmen","Colruyt")


            for(i in 0..2){
                var machine = SortModel()
                machine.name = nameList[i]
                machine.location =locationList[i]
                sortList?.add(machine)


            }
            adapter!!.notifyDataSetChanged()


        val navigationView = findViewById<View>(R.id.nav_view_list) as BottomNavigationView

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



