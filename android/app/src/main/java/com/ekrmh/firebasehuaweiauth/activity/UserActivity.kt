package com.ekrmh.firebasehuaweiauth.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ekrmh.firebasehuaweiauth.R
import com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.FirebaseAuthService
import com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.user.*
import com.ekrmh.firebasehuaweiauth.util.NetworkSingleton
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import kotlinx.android.synthetic.main.activity_user.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val token = intent?.getStringExtra(TOKEN_DATA)
        // Get user details
        token?.let {
            getUserDetails(
                UserRequest(
                    it
                )
            )
        }
    }

    private fun getUserDetails(request: UserRequest) {
        val authService = NetworkSingleton.getInstance().create(FirebaseAuthService::class.java)
        authService.getUser(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful.not()) {
                    Toast.makeText(
                        this@UserActivity,
                        "${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                response.body()?.users?.firstOrNull()?.let {
                    updateUI(it)
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    this@UserActivity,
                    "Failure: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        buttonSignout.setOnClickListener {
            val authParams =
                HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()

            val service = HuaweiIdAuthManager.getService(this, authParams)
            service.signOut()
            service.cancelAuthorization().addOnSuccessListener {
                NetworkSingleton.changeApiUrltoHeroku()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

        }

    }

    private fun updateUI(user: User) {
        textViewName.text = user.displayName
        textViewUID.text = user.localId
        textViewEmail.text = user.email
        Glide.with(this@UserActivity).load(user.photoUrl).error(R.drawable.ic_baseline_error_24).into(imageView)
    }

    companion object {
        val TOKEN_DATA = "TOKEN_DATA"
    }
}