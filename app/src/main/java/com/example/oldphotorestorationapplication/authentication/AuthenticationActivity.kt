package com.example.oldphotorestorationapplication.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.imagepickerlibrary.ImagePickerActivityClass
import com.app.imagepickerlibrary.ImagePickerBottomsheet
import com.app.imagepickerlibrary.bottomSheetActionFragment
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.databinding.AuthenticationBinding
import com.example.oldphotorestorationapplication.databinding.GalleriesBinding
import com.example.oldphotorestorationapplication.galleries.GalleriesActivity
import com.example.oldphotorestorationapplication.gallery.PhotoGalleryFragment
import com.example.oldphotorestorationapplication.people.PeopleGalleryFragment

class AuthenticationActivity: AppCompatActivity() {

    private lateinit var binding: AuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthenticationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init(){
        val authenticationSignInFragment = SignInFragment()
        val authenticationSignUpFragment = SignUpFragment()
        setCurrentFragment(authenticationSignInFragment)
    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_auth_container, fragment)
            commit()
        }
    }
}