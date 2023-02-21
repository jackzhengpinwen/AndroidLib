package com.zpw.net.login

data class LoginRequest(
    val account: String,
    val password: String,
    val client_type: String
)