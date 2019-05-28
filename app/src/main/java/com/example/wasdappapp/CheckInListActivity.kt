package com.example.wasdappapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import data.CheckInAdapter
import data.SortsListAdapter
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.activity_this_object.*
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
        db.collection("wasdapps/${wasdappobj.id}/checkins").get().addOnSuccessListener {
            for(i in it.documents){
                i.toObject(CheckIn::class.java)
                checkInList.plus(i)
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
            }
        }
    }
}
