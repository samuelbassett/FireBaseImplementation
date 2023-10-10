package com.tc.firebaseimplementation

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.service.autofill.UserData
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.tc.firebaseimplementation.data.UserProfile
import com.tc.firebaseimplementation.databinding.ActivityDashboardBinding
import com.tc.firebaseimplementation.databinding.ActivityMainBinding

class DashboardActivity : AppCompatActivity() {

    lateinit var analytics: FirebaseAnalytics
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityDashboardBinding
    lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        analytics = Firebase.analytics
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("users").child(auth.currentUser?.uid.toString())

        binding = ActivityDashboardBinding.inflate(layoutInflater)

        val container = binding.rootView

        val anim = container.background as AnimationDrawable
        anim.start()
        anim.setEnterFadeDuration(10000)
        anim.setExitFadeDuration(5000)

        binding.apply {
            buttonLogout.setOnClickListener {
                auth.signOut()
                goToLogin()
            }
        }

        setContentView(binding.root)
    }

    fun getUserProfile(userId: String, onComplete: (UserProfile?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userProfile = dataSnapshot.getValue(UserProfile::class.java)
                onComplete(userProfile)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onComplete(null)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateSignedInUser(currentUser)
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun updateSignedInUser(user: FirebaseUser?) {
        Toast.makeText(this, user?.email ?: "USER IS ALREADY HERE!", Toast.LENGTH_SHORT).show()
    }
}