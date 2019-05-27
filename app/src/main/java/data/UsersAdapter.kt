
package data

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.wasdappapp.ListUsersActivity
import com.example.wasdappapp.R
import com.google.firebase.firestore.FirebaseFirestore
import model.User


class UsersAdapter(private val list: ArrayList<User>, private val context: Context) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("users")


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(u: User) {
            val objectName: TextView = itemView.findViewById(R.id.name_of_user) as TextView
            objectName.text = u.email
            println("writing in adapter")

            val objectIsAdmin: TextView = itemView.findViewById(R.id.function_of_user) as TextView
            objectIsAdmin.text = u.role

            var bin_button : Button = itemView.findViewById(R.id.bin_button_user) as Button
            bin_button.setOnClickListener {
                val deleteAlert = AlertDialog.Builder(context)
                deleteAlert.setTitle("Delete object")
                deleteAlert.setMessage(
                    "Are you sure you want to delete this user?"
                )
                deleteAlert.setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
                    collection.document("${u.email}").delete()
                    val intent = Intent(context, ListUsersActivity::class.java)
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
