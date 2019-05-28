package com.example.wasdappapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import data.SortsListAdapter
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list.nav_view
import model.User
import model.WasdappEntry

class ListActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")
    private var adapter: SortsListAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)


        add_new_object_button.hide()

        userCollection.document("${currentUser?.email}").get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            if(user?.role == "admin"){
                nav_view_admin.visibility = VISIBLE
                add_new_object_button.show()
            }else{
                nav_view.visibility = VISIBLE
            }
        }

        nav_view_admin.selectedItemId = R.id.navigation_list
        nav_view_admin.setOnNavigationItemSelectedListener { item ->
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
            when (item.itemId) {
                R.id.admin_users ->
                    startActivity(Intent(this, ListUsersActivity::class.java))
            }
            true
        }

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

        val sortList = ArrayList<WasdappEntry>()
        db.collection("wasdapps").get()
            .addOnSuccessListener { task ->
                for (document in task.documents!!) {
                    sortList.add(document.toObject(WasdappEntry::class.java)!!)
                }
                layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
                adapter = SortsListAdapter(sortList!!, this)

                rcv.layoutManager = layoutManager
                rcv.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }

        add_new_object_button.setOnClickListener {
            startActivity(Intent(this, CreateActivity::class.java))
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        if (currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}



