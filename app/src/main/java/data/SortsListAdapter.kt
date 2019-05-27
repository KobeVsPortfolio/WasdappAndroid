package data

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.wasdappapp.ListActivity
import com.example.wasdappapp.R
import com.example.wasdappapp.ThisObjectActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import model.WasdappEntry
import java.lang.Exception


class SortsListAdapter(private val list: ArrayList<WasdappEntry>, private val context: Context) :
    RecyclerView.Adapter<SortsListAdapter.ViewHolder>() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("wasdapps")

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(s: WasdappEntry) {

            val objectName: TextView = itemView.findViewById(R.id.name_of_object) as TextView
            objectName.text = s.name
            var trash_button : Button = itemView.findViewById(R.id.trash_button) as Button
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
            trash_button.setOnClickListener {
            val deleteAlert = AlertDialog.Builder(context)
            deleteAlert.setTitle("Delete object")
            deleteAlert.setMessage(
                "Are you sure you want to delete this order?"
            )
            deleteAlert.setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
                collection.document("${s.id}").delete()
                val intent = Intent(context, ListActivity::class.java)
                context.startActivity(intent)            }

        deleteAlert.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
        }

        val alertDialog: AlertDialog = deleteAlert.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.colorWarning))
        val textView = alertDialog.findViewById<TextView>(android.R.id.message)
        textView?.setTextColor(context.getColor(R.color.colorAccent))
        alertDialog.window?.setBackgroundDrawableResource(R.color.colorPrimary)

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

