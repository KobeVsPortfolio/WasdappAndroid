package data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.wasdappapp.R
import com.example.wasdappapp.ThisObjectActivity
import model.WasdappEntry
import java.lang.Exception

class SortsListAdapter(private val list: ArrayList<WasdappEntry>, private val context: Context) :
    RecyclerView.Adapter<SortsListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(s: WasdappEntry) {
            val objectName: TextView = itemView.findViewById(R.id.name_of_object) as TextView
            objectName.text = s.name

            val objectLocation: TextView = itemView.findViewById(R.id.location_of_object) as TextView
            objectLocation.text = s.gemeente

            if (s.image != null) {
                val bitmap = decoder(s.image!!)
                val objectImage: ImageView = itemView.findViewById(R.id.photo_of_object) as ImageView
                objectImage.setImageBitmap(bitmap)
            }

            itemView.setOnClickListener {
                val intent = Intent(context, ThisObjectActivity::class.java)
                intent.putExtra("wasdappobj", s)
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

    fun decoder(base64Str: String): Bitmap {
        try {
            val imageBytes = Base64.decode(base64Str, 0)
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            return BitmapFactory.decodeResource(context.resources, R.drawable.logo_wasdap4)
        }
    }
}
