package com.example.oldphotorestorationapplication.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.oldphotorestorationapplication.R
import com.example.oldphotorestorationapplication.databinding.AuthenticationSignUpFragmentBinding
import com.example.oldphotorestorationapplication.firebaseAuth.AuthResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.authentication_sign_up_fragment) {

    private lateinit var binding: AuthenticationSignUpFragmentBinding
    private lateinit var mViewModel: AuthenticationViewModel
    private val validation = Validation()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AuthenticationSignUpFragmentBinding.inflate(layoutInflater)
        val view = binding.root
        init()
        return view
    }

    private fun init() {
        mViewModel = ViewModelProvider(this).get(AuthenticationViewModel::class.java)
        if (mViewModel.checkIfCurrentUser()) {
            (activity as AuthenticationActivity).goToGalleryActivity()
        }

        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            if (validate(email, password)) {
                mViewModel.signUpUser(email, password).observe(viewLifecycleOwner) { authResult ->
                    when (authResult) {
                        is AuthResult.Success -> {
                            (activity as AuthenticationActivity).goToGalleryActivity()
                        }
                        is AuthResult.Error ->
                            authResult.message?.let {
                                Toast.makeText(this.context, authResult.message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
            }
        }

        binding.goToSignIn.setOnClickListener {
            (activity as AuthenticationActivity).goToSignIn()
        }
    }

    private fun validate(email: String, password: String): Boolean {
        if (!validation.validateEmail(email)) {
            Toast.makeText(this.context, "Email is not valid", Toast.LENGTH_SHORT).show()
            binding.editTextTextEmailAddress.setText("")
            return false
        } else if (!validation.validatePassword(password)) {
            Toast.makeText(this.context, "Password is not valid", Toast.LENGTH_SHORT).show()
            binding.editTextTextPassword.setText("")
            return false
        }
        return true
    }
}
