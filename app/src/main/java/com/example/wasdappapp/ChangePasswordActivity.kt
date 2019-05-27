package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_account.nav_view as nav_view1

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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

