package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import data.CheckInAdapter
import kotlinx.android.synthetic.main.activity_check_in_list.*
import model.CheckIn
import model.WasdappEntry
import java.util.*

class CheckInListActivity : AppCompatActivity() {

    private var adapter: CheckInAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_list)

        val wasdappobj = intent.getParcelableExtra("wasdappobj") as WasdappEntry

        val checkInList: ArrayList<CheckIn> = ArrayList()
        db.collection("wasdapps/${wasdappobj.id}/checkins").get().addOnSuccessListener { task ->
            for(i in task.documents){
                val checkIn = i.toObject(CheckIn::class.java)
                checkInList.add(checkIn!!)
                layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
                adapter = CheckInAdapter(checkInList, this)

                rcv_checkins.layoutManager = layoutManager
                rcv_checkins.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
        }

        btn_checkin.setOnClickListener {
            val addedCheckin = CheckIn()
            addedCheckin.userId = currentUser!!.email!!
            addedCheckin.timestamp = Calendar.getInstance().time
            db.collection("wasdapps/${wasdappobj.id}/checkins").add(addedCheckin).addOnSuccessListener {
                Toast.makeText(this, "Checked in!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CheckInListActivity::class.java)
                intent.putExtra("wasdappobj", wasdappobj)
                startActivity(intent)
            }
        }
    }
}
