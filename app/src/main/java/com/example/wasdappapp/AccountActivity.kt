package com.example.wasdappapp

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_account.*

class AccountActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val email = auth.currentUser?.email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        delete_account.setOnClickListener {
            deleteAccount()
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
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
            when (item.itemId) {
                R.id.admin_users ->
                    startActivity(Intent(this, ListUsersActivity::class.java))
            }
            true

        }
        change_password.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
            finish()
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

    fun deleteAccount() {
        val password = EditText(this)
        password.hint = "Please enter your password."
        password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        password.transformationMethod = PasswordTransformationMethod()
        val deleteAlert = AlertDialog.Builder(this)
        deleteAlert.setTitle("Delete ${email}")
        deleteAlert.setMessage(
            "Are you sure you want to delete your account? This can't be undone."
        )
        deleteAlert.setView(password)

        deleteAlert.setPositiveButton("Delete") { dialogInterface: DialogInterface, i: Int ->
            val user = auth.currentUser
            val credential = EmailAuthProvider
                .getCredential(user?.email!!, password.text.toString())
            user.reauthenticate(credential)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        auth.currentUser?.delete()
                        Toast.makeText(applicationContext, "$email has been deleted.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
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
}

