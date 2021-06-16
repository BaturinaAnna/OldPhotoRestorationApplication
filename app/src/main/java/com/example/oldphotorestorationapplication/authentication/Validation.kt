package com.example.oldphotorestorationapplication.authentication

import java.util.regex.Pattern

class Validation {
    private val passwordPatterns = arrayListOf(".*[0-9].*",
        ".*[A-Z].*",
        ".*[a-z].*",
        ".*[~!@#\$%\\^&*()\\-_=+\\|\\[{\\]};:'\",<.>/?].*")
    private val PASSWORD_POLICY = """Password should be minimum 8 characters long,
            |should contain at least one capital letter,
            |at least one small letter,
            |at least one number and
            |at least one special character among ~!@#$%^&*()-_=+|[]{};:'\",<.>/?""".trimMargin()

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean{
        var valid = true
        if (password.length < 8) {
            valid = false
        }

        for (exp in passwordPatterns){
            val pattern = Pattern.compile(exp)
            val matcher = pattern.matcher(password)
            if (!matcher.matches()) {
                valid = false
            }
        }
        return valid
    }

}