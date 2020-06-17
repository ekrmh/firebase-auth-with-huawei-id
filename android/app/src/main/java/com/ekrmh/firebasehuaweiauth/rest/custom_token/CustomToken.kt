package com.ekrmh.firebasehuaweiauth.rest.custom_token

data class CustomTokenRequest(
    val id_token: String,
    val uid: String,
    val email: String,
    val name: String,
    val profilePicture: String
)

data class CustomTokenResponse(
    val firebase_token: String
)