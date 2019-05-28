package com.example.wasdappapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import data.SortsListAdapter
import kotlinx.android.synthetic.main.activity_list.*
import model.WasdappEntry

class ListActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    private var adapter: SortsListAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    var next: Query? = null
    var lastVisible: DocumentSnapshot? = null

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


        var sortList = ArrayList<WasdappEntry>()
        val first = db.collection("wasdapps").limit(5)
        first.get()
            .addOnSuccessListener { task ->

                lastVisible = task.documents[task.size() - 1]
                next = db.collection("wasdapps")
                    .startAfter(lastVisible!!)
                    .limit(5)

                for (document in task.documents!!) {
                    sortList.add(document.toObject(WasdappEntry::class.java)!!)
                }

                layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
                adapter = SortsListAdapter(sortList!!, this)

                rcv.layoutManager = layoutManager
                rcv.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }

        btn_more.setOnClickListener {
                next!!.get()
                    .addOnSuccessListener { task ->
                        if(!task.isEmpty){
                        //if (task.documents[task.size() - 1].exists()) {

                            lastVisible = task.documents[task.size() - 1]
                            next = db.collection("wasdapps")
                                .startAfter(lastVisible!!)
                                .limit(5)
                            for (document in task.documents!!) {
                                sortList.add(document.toObject(WasdappEntry::class.java)!!)
                            }
                        } else if(task.isEmpty){
                            btn_more.text = "Go to top"
                        }
                        rcv.layoutManager = layoutManager
                        rcv.adapter = adapter
                        adapter!!.notifyDataSetChanged()
                    }
            }

        btn_more.visibility = View.GONE
        rcv.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(!rcv.canScrollVertically(1)){
                    println("Last item.... ")
                    btn_more.visibility = View.VISIBLE
                }else{
                    btn_more.visibility = View.GONE
                }

            }
        })

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



