package com.devfk.ma.screeningpractice.ui.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.devfk.ma.screeningpractice.R
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.form_login.*

class MainActivity : AppCompatActivity(),View.OnClickListener {
    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    //google Sign In
    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth

    //facebook Sign In
    var callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialization()
    }

    private fun initialization() {
        FacebookSdk.sdkInitialize(this.applicationContext)

        firebaseAuth = FirebaseAuth.getInstance()
        configureGoogleSignIn()
        btnGoogleSignIn.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        btnCheck.setOnClickListener(this)
        btnFacebookSignIn.setReadPermissions("email")

        btnFacebookSignIn.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                val credential = FacebookAuthProvider.getCredential(loginResult!!.accessToken.token)
                firebaseAuth!!.signInWithCredential(credential).addOnFailureListener {

                    Toast.makeText(this@MainActivity,"error: ${it.toString()}",Toast.LENGTH_LONG).show()

                }.addOnSuccessListener {result->
                    val name = result.user?.displayName
                    startActivity(HomeActivity.getLaunchIntent(this@MainActivity))
                    finish()
                }
            }

            override fun onCancel() {
                Toast.makeText(this@MainActivity,"Canceled",Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(this@MainActivity,"error: ${exception.toString()}",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuth(account)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnNext -> {
                startActivity(HomeActivity.getLaunchIntent(this))
            }

            R.id.btnCheck -> nextPage(etPalindrome.text)

            R.id.btnGoogleSignIn->{
                signIn()
            }
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun nextPage(text: Editable?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(checkPolindrome(text.toString()))
        builder.setNeutralButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun checkPolindrome(str: String): String {
        var string = str.replace(" ","")
        var reverse = string.reversed()
        if(string.equals(reverse)){
            return "is Polindrome"
        }else{
            return "not Polindrome"
        }

    }
    private fun firebaseAuth(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(HomeActivity.getLaunchIntent(this))
            } else {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(HomeActivity.getLaunchIntent(this))
            finish()
        }
        val accessToken: AccessToken? = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired()
        if(isLoggedIn){
            startActivity(HomeActivity.getLaunchIntent(this))
            finish()
        }
    }
}

