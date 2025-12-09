package org.ntu.pcs.telecom.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val phone: String,
    val name: String?
)
