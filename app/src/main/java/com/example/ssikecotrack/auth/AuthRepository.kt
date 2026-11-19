package com.example.ssikecotrack.auth

import java.security.MessageDigest

interface AuthRepository {
    suspend fun registerUser(fullName: String, email: String, rawPassword: String): Result<User>
    suspend fun loginUser(email: String, rawPassword: String): Result<User>
}

class InMemoryAuthRepository : AuthRepository {

    companion object {
        // Shared across Login and Register screens during the app session
        private val registeredUsers = mutableListOf(
            User(
                id = 1,
                fullName = "Demo User",
                email = "demo@ssik.com",
                passwordHash = hashPassword("123456")
            )
        )

        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }

    override suspend fun registerUser(
        fullName: String,
        email: String,
        rawPassword: String
    ): Result<User> {
        val cleanEmail = email.trim().lowercase()
        if (registeredUsers.any { it.email == cleanEmail }) {
            return Result.failure(Exception("An account with this email already exists."))
        }

        val newUser = User(
            id = registeredUsers.size + 1,
            fullName = fullName.trim(),
            email = cleanEmail,
            passwordHash = hashPassword(rawPassword)
        )
        registeredUsers.add(newUser)
        return Result.success(newUser)
    }

    override suspend fun loginUser(email: String, rawPassword: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val hashed = hashPassword(rawPassword)
        val user = registeredUsers.find { it.email == cleanEmail && it.passwordHash == hashed }
            ?: return Result.failure(Exception("Invalid email or password."))

        return Result.success(user)
    }
}