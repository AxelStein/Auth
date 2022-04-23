package com.axel_stein.auth

import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers(
        "Accept: application/json;odata=verbose",
        "Content-Type: application/json;odata=verbose",
        "Content-Length:0",
    )
    @POST("sites/techsvn/_api/contextinfo")
    fun auth(): Call<TokenResult>
}