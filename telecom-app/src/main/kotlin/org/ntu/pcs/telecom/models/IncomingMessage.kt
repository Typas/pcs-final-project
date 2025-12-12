package org.ntu.pcs.telecom.models

data class IncomingMessage(
    val name: String?,
    val phone: String,
    val message: String,
    val verifyRequests: ULong,
    val timeToLive: Long
)
