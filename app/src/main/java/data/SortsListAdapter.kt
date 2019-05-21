package data

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.wasdappapp.R
import com.example.wasdappapp.ThisObjectActivity
import model.SortModel

class SortsListAdapter (private val list:ArrayList<SortModel>, private val context: Context)
    : RecyclerView.Adapter<SortsListAdapter.ViewHolder>() {

    inner class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItem(s: SortModel) {
            var objectName: TextView = itemView.findViewById(R.id.name_of_object) as TextView
            objectName.text = s.name

            var objectLocation: TextView = itemView.findViewById(R.id.location_of_object) as TextView
            objectLocation.text = s.location

            itemView.setOnClickListener {
                val intent = Intent(context, ThisObjectActivity::class.java)
                context.startActivity(intent)
            }
        }

    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SortsListAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_cardview, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: SortsListAdapter.ViewHolder, p1: Int) {
        p0.bindItem(list[p1])
    }
}
