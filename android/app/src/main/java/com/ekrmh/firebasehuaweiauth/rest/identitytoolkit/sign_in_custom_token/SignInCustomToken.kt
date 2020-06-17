package com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.sign_in_custom_token

class SignInCustomTokenRequest(
    val token: String,
    val returnSecureToken: Boolean = true
)

data class SignInCustomTokenResponse(
    val idToken: String,
    val refreshToken: String,
    val expiresIn: String,
    val isNewUser: Boolean
)