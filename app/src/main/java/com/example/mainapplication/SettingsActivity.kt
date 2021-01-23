package com.example.mainapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.fragment_my_profile.*

class SettingsActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val app = FirebaseApp.getInstance("first")
        mAuth = FirebaseAuth.getInstance(app)

        exitTv.setOnClickListener {

            mAuth?.signOut()


            App.writeSharedPreferences(this,"isLogged","false")
            finish()
            startActivity(Intent(this,LoginActivity::class.java))
        }
        favourLt.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        //view.showMyProfile.setOnClickListener { startActivity(Intent(activity,LoginActivity::class.java)) }

    }
}