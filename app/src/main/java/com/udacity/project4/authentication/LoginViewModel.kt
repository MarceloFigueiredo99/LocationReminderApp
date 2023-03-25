package com.udacity.project4.authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

private const val TAG = "#LRM LoginViewModel"

class LoginViewModel : ViewModel() {
    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        Log.i(TAG, "Auth state is ${if(user != null) "AUTHENTICATED" else "UNAUTHENTICATED"}")
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}
