package com.example.mainapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class BaseActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val app = FirebaseApp.getInstance("first")


        mAuth = FirebaseAuth.getInstance(app)


    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?){

        if(currentUser==null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        else{

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}