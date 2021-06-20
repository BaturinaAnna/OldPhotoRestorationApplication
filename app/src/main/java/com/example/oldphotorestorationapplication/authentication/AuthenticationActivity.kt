package com.example.oldphotorestorationapplication.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.databinding.AuthenticationBinding
import com.example.oldphotorestorationapplication.galleries.GalleriesActivity

class AuthenticationActivity: AppCompatActivity() {

    private lateinit var binding: AuthenticationBinding
    private val authenticationSignInFragment = SignInFragment()
    private val authenticationSignUpFragment = SignUpFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthenticationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init(){
        setCurrentFragment(authenticationSignInFragment)
    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_auth_container, fragment)
            commit()
        }
    }

    internal fun goToGalleryActivity(){
        val intent = Intent(this, GalleriesActivity::class.java)
        startActivity(intent)
        finish()
    }

    internal fun goToSignUp() {
        setCurrentFragment(authenticationSignUpFragment)
    }

    internal fun goToSignIn() {
        setCurrentFragment(authenticationSignInFragment)
    }
}