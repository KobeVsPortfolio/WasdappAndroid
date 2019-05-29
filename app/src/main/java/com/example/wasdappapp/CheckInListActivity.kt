package com.example.wasdappapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import data.CheckInAdapter
import kotlinx.android.synthetic.main.activity_check_in_list.*
import model.CheckIn
import model.User
import model.WasdappEntry
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class CheckInListActivity : AppCompatActivity() {

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    var currentLatLong: LatLng? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private var adapter: CheckInAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    private val currentUser = auth.currentUser
    var next: Query? = null
    var lastVisible: DocumentSnapshot? = null
    private val userCollection = db.collection("users")

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
            val twentyFourAgo = LocalDateTime.now().minusDays(1)
            val ago = java.util.Date
                .from(
                    twentyFourAgo.atZone(ZoneId.systemDefault())
                        .toInstant()
                )
            db.collection("wasdapps/${wasdappobj.id}/checkins").whereEqualTo("userId", currentUser!!.email).get()
                .addOnSuccessListener { task ->
                    if(!task.isEmpty) {
                        for (i in task.documents) {
                            val checkIn = i.toObject(CheckIn::class.java)
                            if (checkIn != null) {
                                if (checkIn.timestamp!!.time > ago.time) {
                                    Toast.makeText(this, "You've already checked in today.", Toast.LENGTH_SHORT).show()
                                    break
                                } else {
                                    checkIn(wasdappobj)
                                }
                            }
                        }
                    }else{
                        checkIn(wasdappobj)
                    }
                }

        }


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                CheckInListActivity.LOCATION_PERMISSION_REQUEST_CODE

            )
            return
        }
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLatLong = LatLng(location.latitude, location.longitude)
            }

            val wasdapplatlon = LatLng(wasdappobj.lat!!, wasdappobj.lon!!)
            if (calculationByDistance(wasdapplatlon, currentLatLong as LatLng) > 100) {
                btn_checkin.hide()
            } else {
                btn_checkin.show()
            }
        }

        if (!currentUser?.email.isNullOrBlank()) {
            userCollection.document("${currentUser?.email}").get().addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user?.role == "admin") {
                    nav_view_admin.visibility = View.VISIBLE
                } else {
                    nav_view.visibility = View.VISIBLE
                }
            }
        } else {
            nav_view.visibility = View.VISIBLE
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
    }

    fun calculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val Radius = 6371// radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2))
        val c = 2 * Math.asin(Math.sqrt(a))
        val valueResult = Radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))

        println(
            "Radius Value " + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        return Radius * c
    }

    private fun checkIn(wasdappobj: WasdappEntry) {
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

    public override fun onStart() {
        super.onStart()
        if (currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
