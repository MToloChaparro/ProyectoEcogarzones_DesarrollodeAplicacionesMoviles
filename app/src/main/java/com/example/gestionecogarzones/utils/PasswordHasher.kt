package com.example.gestionecogarzones.utils

import java.security.MessageDigest

object PasswordHasher {
    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
