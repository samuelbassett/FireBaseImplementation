package com.tc.firebaseimplementation

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.tc.firebaseimplementation.data.UserProfile
import com.tc.firebaseimplementation.databinding.ActivityMainBinding
import com.tc.firebaseimplementation.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {

    lateinit var analytics: FirebaseAnalytics
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        analytics = Firebase.analytics
        auth = Firebase.auth
        binding = ActivitySignupBinding.inflate(layoutInflater)

        val container = binding.rootView

        val anim = container.background as AnimationDrawable
        anim.start()
        anim.setEnterFadeDuration(10000)
        anim.setExitFadeDuration(5000)

        binding.apply {
            buttonSignup.setOnClickListener {
                if (editTextEmailAddress.text.isNotEmpty() &&
                    editTextPassword.text.isNotEmpty() &&
                    firstName.text.isNotEmpty() &&
                    lastName.text.isNotEmpty()
                ) {
                    if (isValidEmail(editTextEmailAddress.text.toString())) {
                        registerUser(
                            editTextEmailAddress.text.toString(),
                            editTextPassword.text.toString(),
                            firstName.text.toString(),
                            lastName.text.toString()
                        )
                    } else {
                        notValidEmail()
                    }
                }
            }

            signInHere.setOnClickListener {
                goToSignIn()
            }

            setContentView(binding.root)
        }
    }

    private fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("$firstName $lastName")
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // User profile updated, now store additional data
                                val userProfile = UserProfile(firstName, lastName, email)
                                saveUserProfile(user?.uid, userProfile)
                                goToSignIn()
                            } else {
                                Toast.makeText(this, "Failed to create profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                }
            }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}".toRegex()
        return emailPattern.matches(email)
    }

    private fun notValidEmail() {
        Toast.makeText(this, "Not a Valid Email", Toast.LENGTH_SHORT).show()
    }

    private fun goToSignIn() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun saveUserProfile(userId: String?, userProfile: UserProfile) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        userId?.let {
            usersRef.child(it).setValue(userProfile)
        }
    }
}