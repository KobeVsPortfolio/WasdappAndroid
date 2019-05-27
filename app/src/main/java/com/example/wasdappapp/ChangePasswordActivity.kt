package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_change_password.*
import model.User
import kotlinx.android.synthetic.main.activity_account.nav_view as nav_view1

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val currentUser = auth.currentUser
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        auth = FirebaseAuth.getInstance()
        btn_change_password.setOnClickListener {
            changePassword()
        }
        cancel_password.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
            finish()
        }

        nav_view.visibility = View.VISIBLE
        nav_view_admin.visibility = View.INVISIBLE

        userCollection.document("${currentUser?.email}").get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            if(user?.role == "admin"){
                nav_view.visibility = View.INVISIBLE
                nav_view_admin.visibility = View.VISIBLE
            }
        }

        nav_view_admin.selectedItemId = R.id.navigation_account
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
        if (currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun changePassword() {
        et_current_password.transformationMethod = PasswordTransformationMethod()
        et_new_password.transformationMethod = PasswordTransformationMethod()
        et_confirm_password.transformationMethod = PasswordTransformationMethod()

        if (et_current_password.text.isNotBlank() &&
            et_new_password.text.isNotBlank() &&
            et_confirm_password.text.isNotBlank()
        ) {
            if (et_new_password.text.toString().equals(et_confirm_password.text.toString())) {
                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = EmailAuthProvider
                        .getCredential(user.email!!, et_current_password.text.toString())
                    user.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                user.updatePassword(et_new_password.text.toString()).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Password changed succesfully, please sign-in again.",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        auth.signOut()
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                }
                            } else {
                                et_current_password.text = null
                                et_new_password.text = null
                                et_confirm_password.text = null
                                Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

            } else {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT)
                    .show()
            }

        } else {
            Toast.makeText(this, "Please enter all the fields.", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

