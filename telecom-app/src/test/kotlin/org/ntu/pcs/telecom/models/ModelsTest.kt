package org.ntu.pcs.telecom.models

import org.junit.Test
import org.junit.Assert.*

class ModelsTest {

    @Test
    fun testUser() {
        val user = User("1234567890", "John Doe")
        assertEquals("1234567890", user.phone)
        assertEquals("John Doe", user.name)
        
        val user2 = User("1234567890", "John Doe")
        assertEquals(user, user2)
    }

    @Test
    fun testVerificationRecord() {
        val record = VerificationRecord(
            hash = "hash123",
            phone = "1234567890",
            source = "192.168.1.1",
            message = "Verify me",
            timeToLive = 1000L
        )
        assertEquals("hash123", record.hash)
        assertEquals("1234567890", record.phone)
        assertEquals("192.168.1.1", record.source)
        assertEquals("Verify me", record.message)
        assertEquals(1000L, record.timeToLive)
    }

    @Test
    fun testIncomingMessage() {
        val msg = IncomingMessage(
            name = "John",
            phone = "1234567890",
            message = "Verify me",
            verifyRequests = byteArrayOf(1, 2, 3),
            timeToLive = 3000L
        )
        assertEquals("1234567890", msg.phone)
        assertEquals("Verify me", msg.message)
        assertEquals(3000L, msg.timeToLive)
        assertEquals("John", msg.name)
        assertArrayEquals(byteArrayOf(1, 2, 3), msg.verifyRequests)
    }
}
