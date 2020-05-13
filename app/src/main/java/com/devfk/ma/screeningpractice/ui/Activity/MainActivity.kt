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
import com.devfk.ma.screeningpractice.data.Model.User
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.realm.Realm
import io.realm.kotlin.createObject
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

    //Firebase Analytics
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    //Realm
    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialization()
    }

    private fun initialization() {
        loadview.visibility = View.GONE
        realm = Realm.getDefaultInstance()

        firebaseAuth = FirebaseAuth.getInstance()

        //firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        // facebook setting
        FacebookSdk.sdkInitialize(this.applicationContext)
        // Google Sign in config
        configureGoogleSignIn()
        btnGoogleSignIn.setOnClickListener(this)

        btnNext.setOnClickListener(this)
        btnCheck.setOnClickListener(this)
        btnFacebookSignIn.setReadPermissions("email")

        btnFacebookSignIn.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                loadview.visibility = View.VISIBLE
                val credential = FacebookAuthProvider.getCredential(loginResult!!.accessToken.token)
                firebaseAuth(credential)
            }

            override fun onCancel() {
                loadview.visibility = View.GONE
                Toast.makeText(this@MainActivity,"Canceled",Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                loadview.visibility = View.GONE
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
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    firebaseAuth(credential)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Failde : $e", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnNext -> {
                saveUser(etName.text.toString(),"-")
                startActivity(HomeActivity.getLaunchIntent(this))
            }

            R.id.btnCheck -> nextPage(etPalindrome.text)

            R.id.btnGoogleSignIn->{
                loadview.visibility = View.VISIBLE
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
    private fun firebaseAuth(credential: AuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                loadview.visibility = View.GONE
                val user = FirebaseAuth.getInstance().currentUser
                user!!.displayName?.let { it1 -> user!!.email?.let { it2 -> saveUser(it1, it2) } }
                startActivity(HomeActivity.getLaunchIntent(this))
            } else {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun saveUser(displayName: String, email: String) {
        realm.executeTransaction{
            var saveUser = realm.createObject<User>()
            saveUser.name = displayName
            saveUser.email = email
        }
    }


    override fun onStart() {
        super.onStart()
        val user = realm.where(User::class.java).findFirst()
        if (user != null) {
            startActivity(HomeActivity.getLaunchIntent(this))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

