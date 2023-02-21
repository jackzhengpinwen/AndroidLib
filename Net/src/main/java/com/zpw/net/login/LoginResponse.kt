package com.zpw.net.login

data class LoginResponse(
    val token : String,
    val refresh_token : String,
    val user_id : String
)