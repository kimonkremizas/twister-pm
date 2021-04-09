package com.example.twisterpm

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.reset_popup.*

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("KIMON", "Login Activity: onCreate")
        setContentView(R.layout.activity_login)
        val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val fAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val resetAlert: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = this.layoutInflater

        loginButton?.setOnClickListener {
            fun onClick(v: View) {
                val email = loginEmail?.text.toString().trim()
                val password = loginPassword?.text.toString().trim()
                if (email.isEmpty()) {
                    loginEmail?.error = "Email is required"
                    return
                }
                if (password.isEmpty()) {
                    loginPassword?.error = "Password is required"
                    return
                }
                if (password.length < 6) {
                    loginPassword?.error = "Password must contain 6 characters or more"
                    return
                }
                progressBar?.visibility = View.VISIBLE

                // Authenticate user
                fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(OnSuccessListener {
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "email")
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                    Toast.makeText(this@LoginActivity, "Successfully logged in", Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, AllMessagesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    //finish();
                }).addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(e: Exception) {
                        Toast.makeText(this@LoginActivity, "Login failed: " + e.message, Toast.LENGTH_LONG).show()
                        progressBar?.visibility = View.GONE
                    }
                })
            }
            onClick(it)
        }
        forgotPasswordTextView?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val view = inflater.inflate(R.layout.reset_popup, null)
                resetAlert.setTitle("Reset Password?")
                        .setPositiveButton("Reset", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {
                                val resetEmail = view.findViewById<EditText>(R.id.resetEmailEditText)
                                if (resetEmail.text.toString().trim().isEmpty()) {
                                    resetEmail.error = "Required field"
                                    return
                                }
                                fAuth.sendPasswordResetEmail(resetEmail.text.toString().trim())
                                        .addOnSuccessListener { Toast.makeText(this@LoginActivity, "Reset e-mail sent", Toast.LENGTH_LONG).show() }
//                                        .addOnSuccessListener(object : OnSuccessListener<Void?> {
//                                    override fun onSuccess(aVoid: Void?) {
//                                        Toast.makeText(this@LoginActivity, "Reset e-mail sent", Toast.LENGTH_LONG).show()
//                                    }
//                                })
                                        .addOnFailureListener(object : OnFailureListener {
                                    override fun onFailure(e: Exception) {
                                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                                    }
                                })
                            }
                        }).setNegativeButton("Cancel", null)
                        .setView(view)
                        .create().show()
            }
        })
        goToRegisterTextView?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        })
        guestButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val intent = Intent(applicationContext, AllMessagesActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}