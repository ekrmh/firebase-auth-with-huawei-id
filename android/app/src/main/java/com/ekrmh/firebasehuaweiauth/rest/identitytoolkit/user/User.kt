package com.ekrmh.firebasehuaweiauth.rest.identitytoolkit.user

data class UserRequest(
    val idToken: String
)


data class UserResponse(
    val users: List<User>
)
data class User(
    val localId:	String,
    val email:	String,
    val emailVerified: Boolean,
    val displayName:	String,
    val photoUrl:	String,
    val validSince:	String,
    val disabled:	Boolean,
    val lastLoginAt: String,
    val createdAt:	String,
    val customAuth:	Boolean
)
