package com.example.wasdappapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var callbackManager: CallbackManager
    lateinit var mTwitterAuthClient: TwitterAuthClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(
                TwitterAuthConfig(
                    "ejQuDaMY5amRkmZOJezkebj4g",
                    "apK6ipdpYejrE2YY6XYUtbafOhLq9DDgIzcImBXlsnYmCe4iaw"
                )
            )
            .debug(true)
            .build()
        Twitter.initialize(config)

        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        password.transformationMethod = PasswordTransformationMethod()

        mTwitterAuthClient = TwitterAuthClient()

        callbackManager = CallbackManager.Factory.create()

        AppEventsLogger.activateApp(getApplication())

        configureGoogleSignIn()
        setupUI()

        login_with_twitter.setOnClickListener {
            mTwitterAuthClient.authorize(this, object : Callback<TwitterSession>() {
                override fun success(result: Result<TwitterSession>?) {
                    println("Twitter callback succes..")
                    handleTwitterSession(result!!.data)
                }

                override fun failure(exception: TwitterException?) {
                    println("error => " + exception?.stackTrace)
                }
            })
        }

        login_with_email.setOnClickListener {
            var email = email.text.toString()
            var password = password.text.toString()

            if (!email.isBlank() && !password.isBlank()) {
                signIn(email, password)
            } else {
                Toast.makeText(this, "Please provide an email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        sign_up.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        login_with_facebook.setOnClickListener {

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"))

            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    println("Cancel")
                }

                override fun onError(error: FacebookException?) {
                    println(error!!.message)
                }

                override fun onSuccess(loginResult: LoginResult) {
                    println("facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }
            })

        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setupUI() {
        login_with_google.setOnClickListener {
            signInWithGoofle()
        }
    }

    private fun signInWithGoofle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
                println("error -> " + e.printStackTrace())
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    baseContext, "Welcome  ${acct.displayName}.",
                    Toast.LENGTH_LONG
                ).show()
                val user = auth.currentUser
                updateUI(user)
            } else {
                Toast.makeText(this, "Google sign in failed:( ....", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        println("handleFacebookAccessToken:" + token)
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    println("signInWithCredential:failure" + task.getException())
                    Toast.makeText(
                        this@MainActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun handleTwitterSession(session: TwitterSession) {
        println("handleTwitterSession:$session")

        val credential = TwitterAuthProvider.getCredential(
            session.authToken.token,
            session.authToken.secret
        )

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    println("signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    println("signInWithCredential:failure" + task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val intent = Intent(this, MainViewActivity::class.java)
            startActivity(intent)
        }
    }
}
