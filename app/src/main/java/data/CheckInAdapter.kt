package data

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wasdappapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.CheckIn


class CheckInAdapter(private val list: ArrayList<CheckIn>, private val context: Context) :
    RecyclerView.Adapter<CheckInAdapter.ViewHolder>() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("wasdapps")
    val userCollection = db.collection("users")
    val currentUser = auth.currentUser

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(checkIn: CheckIn) {
            val user: TextView = itemView.findViewById(R.id.user) as TextView
            val time: TextView = itemView.findViewById(R.id.timestamp) as TextView

            user.text = checkIn.userId
            time.text = checkIn.timestamp.toString()
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_checkin_cardview, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
}

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItem(list[p1])
    }
}

