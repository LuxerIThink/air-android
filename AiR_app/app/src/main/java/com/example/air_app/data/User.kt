package com.example.air_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
    val ip: String,
    val port: Int,
)