package com.moufee.boilerfit.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.moufee.boilerfit.R
import com.moufee.boilerfit.User
import com.moufee.boilerfit.repository.UserRepository
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import javax.inject.Inject

const val RC_SIGN_IN: Int = 12312

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {

                userRepository.userExist { exists ->
                    if (exists != null && !exists) {
                        userRepository.createUser(User()) { success ->
                            if (success != null && success) {
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                        }
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    return
                }
                // show some sort of error message?
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        welcome_signin_button.setOnClickListener {
            val providers: List<AuthUI.IdpConfig> = Arrays.asList(AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build())

            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .build(), RC_SIGN_IN)
        }
    }


}
