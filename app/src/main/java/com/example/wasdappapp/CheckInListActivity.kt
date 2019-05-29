package com.example.wasdappapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import data.CheckInAdapter
import kotlinx.android.synthetic.main.activity_check_in_list.*
import kotlinx.android.synthetic.main.activity_list.*
import model.CheckIn
import model.WasdappEntry
import java.util.*

class CheckInListActivity : AppCompatActivity() {

    private var adapter: CheckInAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    private val currentUser = auth.currentUser
    var next: Query? = null
    var lastVisible: DocumentSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in_list)

        val wasdappobj = intent.getParcelableExtra("wasdappobj") as WasdappEntry

        val checkInList: ArrayList<CheckIn> = ArrayList()

        val first = db.collection("wasdapps/${wasdappobj.id}/checkins").limit(8)

        first.get()
            .addOnSuccessListener { task ->
                if(!task.isEmpty){
                lastVisible = task.documents[task.size() - 1]
                next = db.collection("wasdapps/${wasdappobj.id}/checkins")
                    .startAfter(lastVisible!!)
                    .limit(5)

            for(i in task.documents) {
                val checkIn = i.toObject(CheckIn::class.java)
                checkInList.add(checkIn!!)

            }
                layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
                adapter = CheckInAdapter(checkInList, this)

                rcv_checkins.layoutManager = layoutManager
                rcv_checkins.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
        }

        btn_checkin_more.setOnClickListener{
            next!!.get()
                .addOnSuccessListener { task ->
                    if(!task.isEmpty){
                        //if (task.documents[task.size() - 1].exists()) {
                        lastVisible = task.documents[task.size() - 1]
                        next = db.collection("wasdapps/${wasdappobj.id}/checkins")
                            .startAfter(lastVisible!!)
                            .limit(5)
                        for ( i in task.documents!!) {
                            val checkIn = i.toObject(CheckIn::class.java)
                            checkInList.add(checkIn!!)
                        }
                    } else if(task.isEmpty){
                        btn_checkin_more.text = "Go to top"
                    }
                    rcv_checkins.layoutManager = layoutManager
                    rcv_checkins.adapter = adapter
                    adapter!!.notifyDataSetChanged()
                }
        }

        btn_checkin_more.visibility = View.GONE
        rcv_checkins.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(!rcv_checkins.canScrollVertically(1)){
                    println("Last item.... ")
                    btn_checkin_more.visibility = View.VISIBLE
                }else{
                    btn_checkin_more.visibility = View.GONE
                }

            }
        })

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
