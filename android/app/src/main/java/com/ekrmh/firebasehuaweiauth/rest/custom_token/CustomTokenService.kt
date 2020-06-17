package com.ekrmh.firebasehuaweiauth.rest.custom_token

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CustomTokenService {
    @POST("createCustomToken")
    fun getToken(@Body request: CustomTokenRequest): Call<CustomTokenResponse>
}