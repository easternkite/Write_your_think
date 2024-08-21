package com.multimedia.writeyourthink.Util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.multimedia.writeyourthink.R

fun FirebaseAuth.handleAccessTokenForLogin(
    credential: AuthCredential,
    onLoginComplete: () -> Unit,
    onLoginFailed: () -> Unit = {}
) {
    signInWithCredential(credential)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("Login", "Login Completed.")
                onLoginComplete()
            }
            else {
                Log.d("Login", "Login failed.")
                onLoginFailed()
            }
        }
}

fun Context.showFailToast() = Toast.makeText(
    this,
    getString(R.string.login_failed),
    Toast.LENGTH_SHORT
).show()