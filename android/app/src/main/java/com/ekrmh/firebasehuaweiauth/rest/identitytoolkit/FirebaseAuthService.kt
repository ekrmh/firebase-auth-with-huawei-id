package com.ekrmh.firebasehuaweiauth.rest.identitytoolkit

import com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.sign_in_custom_token.SignInCustomTokenRequest
import com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.sign_in_custom_token.SignInCustomTokenResponse
import com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.user.UserRequest
import com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.user.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FirebaseAuthService {
    @POST("./accounts:signInWithCustomToken")
    fun signInWithCustomToken(@Body request: SignInCustomTokenRequest): Call<SignInCustomTokenResponse>

    @POST("./accounts:lookup")
    fun getUser(@Body request: UserRequest): Call<UserResponse>
}