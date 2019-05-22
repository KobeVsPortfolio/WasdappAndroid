package com.example.wasdappapp

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firestore.v1.FirestoreGrpc
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val displayName = auth.currentUser?.displayName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        delete_account.setOnClickListener {
            val deleteAlert = AlertDialog.Builder(this)
            deleteAlert.setTitle("Delete ${displayName}")
            deleteAlert.setMessage(
                "Are you sure you want to delete your account? This can't be undone."
            )

            deleteAlert.setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
                auth.currentUser?.delete()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(applicationContext, "$displayName has been deleted.", Toast.LENGTH_SHORT).show()
            }
            deleteAlert.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            }

            val alertDialog: AlertDialog = deleteAlert.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorWarning))
            val textView = alertDialog.findViewById<TextView>(android.R.id.message)
            textView?.setTextColor(getColor(R.color.colorPrimaryDark))
            alertDialog.window?.setBackgroundDrawableResource(R.color.colorAccent2)
        }

        logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        nav_view.selectedItemId = R.id.navigation_account
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

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

