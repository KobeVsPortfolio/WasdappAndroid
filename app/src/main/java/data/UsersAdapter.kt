
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
import model.User
import model.WasdappEntry
import java.lang.Exception

class UsersAdapter(private val list: ArrayList<User>, private val context: Context) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(u: User) {
            val objectName: TextView = itemView.findViewById(R.id.name_of_user) as TextView
            objectName.text = u.email
            println("writing in adapter")

            val objectIsAdmin: TextView = itemView.findViewById(R.id.function_of_user) as TextView
            objectIsAdmin.text = u.role

            /*itemView.findViewById(R.id.bin_button).setOnClickListener {

            }*/
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): UsersAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_admin_users, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: UsersAdapter.ViewHolder, p1: Int) {
        p0.bindItem(list[p1])
    }

}
