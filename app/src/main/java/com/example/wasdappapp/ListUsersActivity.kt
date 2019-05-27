package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import data.SortsListAdapter
import data.UsersAdapter
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_list_users.*
import model.User
import model.WasdappEntry

class ListUsersActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()


    private var adapter: UsersAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_users)

        nav_viewx.selectedItemId = R.id.admin_users
        nav_viewx.setOnNavigationItemSelectedListener { item ->
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
        val db = FirebaseFirestore.getInstance()

        var userList = ArrayList<User>()
        db.collection("users").get()
            .addOnSuccessListener { task ->
                for (document in task.documents!!) {
                    println(document.data.toString())
                    userList.add(document.toObject(User::class.java)!!)

                }

            }

        layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        adapter = UsersAdapter(userList, this)

        rcvx.layoutManager = layoutManager
        rcvx.adapter = adapter
        adapter!!.notifyDataSetChanged()
    }
}
