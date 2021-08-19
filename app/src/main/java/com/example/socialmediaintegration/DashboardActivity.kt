package com.example.socialmediaintegration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialmediaintegration.databinding.ActivityDashboardBinding
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class DashboardActivity : AppCompatActivity() {
    companion object{
                    private const val RC_SIGN_IN = 120
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var binding : ActivityDashboardBinding
    private lateinit var mcallbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)

        val view = binding.root
        mcallbackManager = CallbackManager.Factory.create();
        setContentView(view)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)
        mAuth = FirebaseAuth.getInstance()

        binding.googlebutton.setOnClickListener {
            signIn()
        }


        FacebookSdk.sdkInitialize(this)
        mcallbackManager = CallbackManager.Factory.create()

       binding.fbButton.setReadPermissions("email", "public_profile")
       binding.fbButton.registerCallback(mcallbackManager, object :
           FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
            }
        })

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if(currentUser!=null){
            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivity(signInIntent)
            finish()
        }
        else{

        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Facebook sign in", "signInWithCredential:success")
                    val currentUser = mAuth.currentUser

                    if (currentUser != null) {
                        val signInIntent = Intent(this, SignInActivity::class.java)
                        startActivity(signInIntent)
                        finish()
                    }
                } else {
                    Log.w("facebook sign in", "signInWithCredential:failure", task.exception)
                   updateUI(null)
                }
            }
    }

    private fun updateUI(user: Any?) {
        if(user!=null){
            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivity(signInIntent)
            finish()
        }
        else{
            Toast.makeText(baseContext, "Authentication failed.",Toast.LENGTH_SHORT).show()
        }
    }



    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            mcallbackManager.onActivityResult(requestCode, resultCode, data)
            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val exception = task.exception
                if(task.isSuccessful){
                    try {
                        val account = task.getResult(ApiException::class.java)!!
                        Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                        firebaseAuthWithGoogle(account.idToken!!)
                    } catch (e: ApiException) {
                        Log.w("SignInActivity", "Google sign in failed", e)
                    }
                }
                else{
                    Log.w("SignInActivity", exception.toString())
                }

            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("SignInActivity", "signInWithCredential:success")
                    val intent = Intent(this,SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)

                }
            }
    }
}