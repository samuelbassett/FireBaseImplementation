package com.tc.firebaseimplementation

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.tc.firebaseimplementation.data.UserProfile
import com.tc.firebaseimplementation.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var analytics: FirebaseAnalytics
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        analytics = Firebase.analytics
        auth = Firebase.auth
        binding = ActivityMainBinding.inflate(layoutInflater)

        val container = binding.rootView

        val anim = container.background as AnimationDrawable
        anim.start()
        anim.setEnterFadeDuration(10000)
        anim.setExitFadeDuration(5000)

        binding.apply {
            buttonLogin.setOnClickListener {
                if (editTextEmailAddress.text.isNotEmpty() &&
                    editTextPassword.text.isNotEmpty()
                ) {
                    if (isValidEmail(editTextEmailAddress.text.toString())) {
                        auth.signInWithEmailAndPassword(
                            binding.editTextEmailAddress.text.toString(),
                            binding.editTextPassword.text.toString()
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                goToDashboard()
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    } else {
                        notValidEmail()
                    }
                }
            }

            buttonSignup.setOnClickListener {
                goToSignUp()
            }

            setContentView(binding.root)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}".toRegex()
        return emailPattern.matches(email)
    }

    private fun notValidEmail() {
        Toast.makeText(this, "Not a Valid Email", Toast.LENGTH_SHORT).show()
    }

    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goToDashboard()
        }
    }
}