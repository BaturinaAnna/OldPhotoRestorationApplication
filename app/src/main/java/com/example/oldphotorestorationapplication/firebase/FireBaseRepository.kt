package com.example.oldphotorestorationapplication.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.*


class FireBaseRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun checkIfCurrentUser(): Boolean{
        val currentUser = firebaseAuth.currentUser
        return currentUser != null
    }

    fun signUpUser(email: String, password: String): MutableLiveData<AuthResult<FirebaseUser?>>{
        val authResult: MutableLiveData<AuthResult<FirebaseUser?>> = MutableLiveData(AuthResult.Error(null))
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                authResult.setValue(AuthResult.Success(firebaseAuth.currentUser))
            }
            .addOnFailureListener {
                authResult.setValue(AuthResult.Error(it.message))
            }
        return authResult
    }


    fun signInUser(email: String, password: String): MutableLiveData<AuthResult<FirebaseUser?>> {
        val authResult: MutableLiveData<AuthResult<FirebaseUser?>> = MutableLiveData(AuthResult.Error(null))
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                authResult.setValue(AuthResult.Success(firebaseAuth.currentUser))
            }
            .addOnFailureListener {
                authResult.setValue(AuthResult.Error(it.message))
            }
        return authResult
    }

    fun signOut(){
        firebaseAuth.signOut()
    }

}