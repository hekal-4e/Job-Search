package com.example.jobsearchapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>(AuthState.Unauthenticated)
    val authState: LiveData<AuthState> = _authState
    private var currentUser: FirebaseUser? = null
    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
        }else{
            if (auth.currentUser?.isEmailVerified == true) {
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.EmailNotVerified
            }
        }
    }

    fun login(email : String, password : String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    if (auth.currentUser?.isEmailVerified == true) {
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.EmailNotVerified
                    }
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

    fun signup(email : String, password : String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    sendEmailVerification()
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

    fun signupWithProfile(email: String, password: String, name: String, age: String, gender: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User created successfully, now save profile information
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userProfile = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "age" to age,
                            "gender" to gender,
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )
                        // Save to Firestore
                        FirebaseFirestore.getInstance().collection("users")
                            .document(userId)
                            .set(userProfile)
                            .addOnSuccessListener {
                                sendEmailVerification()
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthState.Error("Failed to save profile: ${e.message}")
                            }
                    } else {
                        _authState.value = AuthState.Error("Failed to get user ID")
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Signup failed")
                }
            }
    }

    private fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.VerificationEmailSent
                } else {
                    _authState.value = AuthState.Error("Failed to send verification email: ${task.exception?.message}")
                }
            }
    }

    fun resendVerificationEmail() {
        _authState.value = AuthState.Loading
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.VerificationEmailSent
                } else {
                    _authState.value = AuthState.Error("Failed to resend verification email: ${task.exception?.message}")
                }
            }
    }

    fun refreshVerificationStatus() {
        _authState.value = AuthState.Loading
        auth.currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkAuthStatus()
            } else {
                _authState.value = AuthState.Error("Failed to refresh status: ${task.exception?.message}")
            }
        }
    }

    fun signout() {
        _authState.value = AuthState.Loading

        try {
            // Sign out from Firebase
            auth.signOut()

            // Clear the current user
            currentUser = null

            // Explicitly set state to unauthenticated
            _authState.postValue(AuthState.Unauthenticated)

            // Log the logout for debugging
            Log.d("AuthViewModel", "User signed out successfully, state set to Unauthenticated")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error signing out: ${e.message}")
            _authState.postValue(AuthState.Error("Error signing out: ${e.message}"))
        }
    }

    // Override the onCleared method to ensure cleanup
    override fun onCleared() {
        super.onCleared()
        // Remove any auth state listeners if you've added any
    }


    fun getCurrentUserEmail(): String {
        return auth.currentUser?.email ?: "No Email"
    }
}

sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    object EmailNotVerified : AuthState()
    object VerificationEmailSent : AuthState()
    data class Error(val message : String) : AuthState()
}
