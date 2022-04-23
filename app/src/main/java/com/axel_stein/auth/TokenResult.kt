package com.axel_stein.auth

data class TokenResult(
    val d: Obj
)

data class Obj(
    val GetContextWebInformation: GetContextWebInformation
)

data class GetContextWebInformation(
    val FormDigestValue: String
)
