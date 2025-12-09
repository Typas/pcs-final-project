package org.ntu.pcs.telecom.models

data class IncomingMessage(
    val name: String?,
    val phone: String,
    val message: String,
    val verifyRequests: ByteArray,
    val timeToLive: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IncomingMessage

        if (name != other.name) return false
        if (phone != other.phone) return false
        if (message != other.message) return false
        if (!verifyRequests.contentEquals(other.verifyRequests)) return false
        if (timeToLive != other.timeToLive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + phone.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + verifyRequests.contentHashCode()
        result = 31 * result + timeToLive.hashCode()
        return result
    }
}
