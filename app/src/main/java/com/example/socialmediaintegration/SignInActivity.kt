package com.example.socialmediaintegration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.socialmediaintegration.databinding.ActivitySignInBinding
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding
    //private lateinit var googleSignInClient : GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_sign_in)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        //binding.idtv.text = currentUser?.uid
        binding.nametv.text = currentUser?.displayName
        binding.emailtv.text = currentUser?.email
        val imagedp : ImageView = findViewById(R.id.dp)
        //Glide.with(this).load(currentUser?.photoUrl).into(imagedp);
        mAuth.currentUser?.apply {
            for (userInfo in providerData) {
                if (userInfo.providerId == "facebook.com") {
                    val photoUrl = userInfo.photoUrl
                    Log.d("TAG", photoUrl.toString())
                }
            }
        }
        Glide.with(this).load(currentUser?.photoUrl).into(imagedp)
        //Picasso.get().load(currentUser?.photoUrl).into(imagedp)

        binding.signoutbut.setOnClickListener {
            mAuth.signOut()
            //googleSignInClient.signOut()
            LoginManager.getInstance().logOut()

            logout()
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun logout() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
    }
}