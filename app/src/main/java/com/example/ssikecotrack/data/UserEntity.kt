package com.example.ssikecotrack.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.ssikecotrack.auth.User

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String
) {
    fun toUser() = User(
        id = id,
        fullName = fullName,
        email = email,
        passwordHash = passwordHash
    )
}