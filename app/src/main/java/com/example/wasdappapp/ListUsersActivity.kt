package com.example.wasdappapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import data.UsersAdapter
import model.User
import kotlinx.android.synthetic.main.activity_list_users.*

class ListUsersActivity : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    private var adapter: UsersAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("users")
    private val currentUser = auth.currentUser
    private val userCollection = db.collection("users")
    var admin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_users)

        getUser()

        add_user_password.transformationMethod = PasswordTransformationMethod()
        add_user_confirm_password.transformationMethod = PasswordTransformationMethod()

        add_admin.setOnClickListener {
            val email = add_email.text.toString()
            val password = add_user_password.text.toString()
            val repeatPassword = add_user_confirm_password.text.toString()
            admin = checkBox.isChecked
            if (!(email.isBlank() && password.isBlank())) {
                if (password == repeatPassword) {
                    if (password.length > 5) {
                        addUser(email, password, admin)
                    } else {
                        Toast.makeText(this, "Your password needs at least 6 characters.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all the fields.", Toast.LENGTH_SHORT).show()
            }
        }

        nav_view_admin.visibility = View.INVISIBLE

        userCollection.document("${currentUser?.email}").get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            if(user?.role == "admin"){
                nav_view_admin.visibility = View.VISIBLE
            }
        }

        nav_view_admin.selectedItemId = R.id.admin_users
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
    }

    private fun addUser(email: String, password: String, admin: Boolean) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful) {
                    if (admin) {
                        val user = HashMap<String, Any>()
                        user["email"] = email
                        user["role"] = "admin"
                        collection.document(email).set(user)
                        Toast.makeText(
                            baseContext, "Admin with email: $email has been added.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val user = HashMap<String, Any>()
                        user["email"] = email
                        user["role"] = "user"
                        collection.document(email).set(user)
                        Toast.makeText(
                            baseContext, "User with email: $email has been added.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    add_user_password.text = null
                    add_user_confirm_password.text = null
                    add_email.text = null
                    checkBox.isChecked = false
                } else {
                    Toast.makeText(
                        baseContext, "Usercreation failed, email is invalid or already in use.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun getUser() {
        var userList = ArrayList<User>()
        collection.get()
            .addOnSuccessListener { task ->
                for (document in task.documents!!) {
                    userList.add(document.toObject(User::class.java)!!)
                }
                layoutManager = LinearLayoutManager(this)
                adapter = UsersAdapter(userList, this)
                rv_users.layoutManager = layoutManager
                rv_users.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
    }
    public override fun onStart() {
        super.onStart()
        if (currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
