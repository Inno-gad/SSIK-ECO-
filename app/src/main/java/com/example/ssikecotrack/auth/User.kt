package com.example.ssikecotrack.auth

data class User(
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String
)