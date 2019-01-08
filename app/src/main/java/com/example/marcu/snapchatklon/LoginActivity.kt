package com.example.marcu.snapchatklon

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.marcu.snapchatklon.showProgress

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){
    override fun onResume() {
        super.onResume()
        password.setText("")
        email.setText("")
        auth.signOut()
    }

    private var mAuthTask: UserLoginTask? = null
    private lateinit var auth: FirebaseAuth

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })



        email.setText("")
        password.setText("")

        auth = FirebaseAuth.getInstance()

        email_sign_in_button.setOnClickListener { attemptLogin() }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private fun attemptLogin() {
        if (mAuthTask != null) {
            return
        }

        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(passwordStr)) {
            password.error = getString(R.string.error_invalid_password)
            focusView = password
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            email.error = getString(R.string.error_field_required)
            focusView = email
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true, login_form, login_progress)
            mAuthTask = UserLoginTask(emailStr, passwordStr)
            mAuthTask!!.execute(null as Void?)
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) :
        AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void): Boolean {
            try {
                auth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            login()
                        } else {
                            auth.createUserWithEmailAndPassword(mEmail, mPassword)
                                .addOnCompleteListener(this@LoginActivity) { task ->
                                    if (task.isSuccessful) {
                                        FirebaseDatabase.getInstance().getReference().child("users").child(task.result!!.user.uid)
                                            .child("email").setValue(email.text.toString())
                                        login()
                                    } else {
                                        Toast
                                            .makeText(applicationContext,"Login nicht erfolgreich", Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }
                        }
                    }
                return true
            } catch (e: InterruptedException) {
                return false
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false, login_form, login_progress)
        }
    }

    fun login(){
        val intent = Intent(applicationContext, SnapsActivity::class.java)
        startActivity(intent)
        showProgress(false, login_form, login_progress)
        finish()
    }

}



