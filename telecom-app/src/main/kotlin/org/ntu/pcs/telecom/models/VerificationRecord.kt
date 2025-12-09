package org.ntu.pcs.telecom.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verification_records")
data class VerificationRecord(
    @PrimaryKey
    val hash: String,
    val phone: String,
    val source: String, // e.g., IP address of the client
    val message: String, // The message to be sent to the phone
    val timeToLive: Long // timestamp when this record expires
)
