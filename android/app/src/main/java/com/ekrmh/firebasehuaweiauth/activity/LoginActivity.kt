package com.ekrmh.firebasehuaweiauth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ekrmh.firebasehuaweiauth.R
import com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.FirebaseAuthService
import com.ekrmh.firebasehuaweiauth.rest.custom_token.CustomTokenRequest
import com.ekrmh.firebasehuaweiauth.rest.custom_token.CustomTokenResponse
import com.ekrmh.firebasehuaweiauth.rest.custom_token.CustomTokenService
import com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.sign_in_custom_token.SignInCustomTokenRequest
import com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.sign_in_custom_token.SignInCustomTokenResponse
import com.ekrmh.firebasehuaweiauth.util.NetworkSingleton
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    lateinit var service: HuaweiIdAuthService
    val REQUEST_CODE = 8888
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authParams =
            HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setProfile()
                .setIdToken()
                .createParams()

        service = HuaweiIdAuthManager.getService(this, authParams)


        //If the user has already logged in, they can log in automatically.
        silentSignIn()


        buttonLogin.setOnClickListener {
            // Start login flow
            startActivityForResult(service.signInIntent, REQUEST_CODE);
        }
    }

    fun silentSignIn() {
        service.silentSignIn().addOnSuccessListener {
            huaweiSignInSuccess(it)
        }
    }

    fun huaweiSignInSuccess(huaweiAccount: AuthHuaweiId) {
        val idToken = huaweiAccount.idToken
        val uid = huaweiAccount.unionId
        val profilePicture = huaweiAccount.avatarUriString
        val name = huaweiAccount.displayName
        val email = huaweiAccount.email ?: ""

        // Get Custom Auth Token from our server
        getCustomTokenFromServer(
            CustomTokenRequest(
                idToken,
                uid,
                email,
                name,
                profilePicture
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)

            authHuaweiIdTask.addOnSuccessListener { huaweiAccount ->
                // Huawei account sign in successful
                huaweiSignInSuccess(huaweiAccount)
            }.addOnFailureListener {
                Toast.makeText(this, "Failure: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCustomTokenFromServer(customTokenRequest: CustomTokenRequest) {
        val customTokenService =
            NetworkSingleton.getInstance().create(CustomTokenService::class.java)
        customTokenService.getToken(customTokenRequest)
            .enqueue(object : Callback<CustomTokenResponse> {
                override fun onResponse(
                    call: Call<CustomTokenResponse>,
                    response: Response<CustomTokenResponse>
                ) {
                    if (response.isSuccessful.not()) {
                        Toast.makeText(
                            this@LoginActivity,
                            "${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    response.body()?.let {

                        // Change BASE_URL and add API_KEY to out next network calls
                        NetworkSingleton.changeApiUrltoFirebase()

                        response.body()?.let {

                            // Custom Auth Token received
                            val token = it.firebase_token

                            // Authenticate with Firebase using a Custom Auth Token
                            signInWithCustomToken(
                                SignInCustomTokenRequest(
                                    token
                                )
                            )
                        }
                    }

                }

                override fun onFailure(call: Call<CustomTokenResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Failure: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun signInWithCustomToken(request: SignInCustomTokenRequest) {
        val authService = NetworkSingleton.getInstance().create(FirebaseAuthService::class.java)
        authService.signInWithCustomToken(request)
            .enqueue(object : Callback<SignInCustomTokenResponse> {
                override fun onResponse(
                    call: Call<SignInCustomTokenResponse>,
                    response: Response<SignInCustomTokenResponse>
                ) {

                    if (response.isSuccessful.not()) {
                        Toast.makeText(
                            this@LoginActivity,
                            "${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }


                    response.body()?.let {

                        // User created on Firebase and sign in successful. Now we can go user activity to get user details.
                        // We will use this idToken to our Firebase Auth network calls
                        val token = it.idToken
                        goToUserActivity(token)
                    }
                }

                override fun onFailure(call: Call<SignInCustomTokenResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Failure: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun goToUserActivity(token: String) {
        val intent = Intent(this@LoginActivity, UserActivity::class.java)
        intent.putExtra(UserActivity.TOKEN_DATA, token)
        startActivity(intent)
        finish()
    }


}