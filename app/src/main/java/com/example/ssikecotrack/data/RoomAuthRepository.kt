package com.example.ssikecotrack.data

import com.example.ssikecotrack.auth.AuthRepository
import com.example.ssikecotrack.auth.InMemoryAuthRepository
import com.example.ssikecotrack.auth.User

class RoomAuthRepository(private val userDao: UserDao) : AuthRepository {

    // Keeps Gad's demo account (demo@ssik.com / 123456) working
    private suspend fun ensureDemoUser() {
        val demoEmail = "demo@ssik.com"
        if (userDao.getByEmail(demoEmail) == null) {
            userDao.insert(
                UserEntity(
                    fullName = "Demo User",
                    email = demoEmail,
                    passwordHash = InMemoryAuthRepository.hashPassword("123456")
                )
            )
        }
    }

    override suspend fun registerUser(
        fullName: String,
        email: String,
        rawPassword: String
    ): Result<User> {
        ensureDemoUser()
        val cleanEmail = email.trim().lowercase()
        if (userDao.getByEmail(cleanEmail) != null) {
            return Result.failure(Exception("An account with this email already exists."))
        }
        val entity = UserEntity(
            fullName = fullName.trim(),
            email = cleanEmail,
            passwordHash = InMemoryAuthRepository.hashPassword(rawPassword)
        )
        val newId = userDao.insert(entity).toInt()
        return Result.success(entity.copy(id = newId).toUser())
    }

    override suspend fun loginUser(email: String, rawPassword: String): Result<User> {
        ensureDemoUser()
        val cleanEmail = email.trim().lowercase()
        val hashed = InMemoryAuthRepository.hashPassword(rawPassword)
        val user = userDao.getByEmail(cleanEmail)
        if (user == null || user.passwordHash != hashed) {
            return Result.failure(Exception("Invalid email or password."))
        }
        return Result.success(user.toUser())
    }
}