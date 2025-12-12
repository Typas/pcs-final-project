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
    fun testUserEqualsAndHashCode() {
        val user1 = User("1234567890", "John Doe")
        val user2 = User("1234567890", "John Doe")
        val user3 = User("0987654321", "John Doe") // Different phone
        val user4 = User("1234567890", "Jane Doe") // Different name
        val userNullName1 = User("1234567890", null)
        val userNullName2 = User("1234567890", null)

        // Test equality
        assertEquals(user1, user1)
        assertEquals(user1, user2)
        assertEquals(user1.hashCode(), user2.hashCode())

        // Test inequality for each property
        assertNotEquals(user1, user3)
        assertNotEquals(user1.hashCode(), user3.hashCode())
        assertNotEquals(user1, user4)
        assertNotEquals(user1.hashCode(), user4.hashCode())

        // Test cases with null name
        assertEquals(userNullName1, userNullName2)
        assertEquals(userNullName1.hashCode(), userNullName2.hashCode())
        assertNotEquals(user1, userNullName1)
        assertNotEquals(userNullName1, user1)
        assertNotEquals(user1.hashCode(), userNullName1.hashCode())

        // Test against null and different types
        assertNotEquals(user1, null)
        assertNotEquals(user1, Any())
    }

    @Test
    fun testUserCopy() {
        val user1 = User("1234567890", "John Doe")
        val user2 = user1.copy()
        val user3 = user1.copy(name = "Jane Doe")

        assertEquals(user1, user2)
        assertNotEquals(user1, user3)
        assertEquals("1234567890", user3.phone)
        assertEquals("Jane Doe", user3.name)
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
    fun testVerificationRecordEqualsAndHashCode() {
        val record1 = VerificationRecord("hash1", "123", "source1", "message1", 1L)
        val record2 = VerificationRecord("hash1", "123", "source1", "message1", 1L)
        val record3 = VerificationRecord("hash2", "123", "source1", "message1", 1L)
        val record4 = VerificationRecord("hash1", "456", "source1", "message1", 1L)
        val record5 = VerificationRecord("hash1", "123", "source2", "message1", 1L)
        val record6 = VerificationRecord("hash1", "123", "source1", "message2", 1L)
        val record7 = VerificationRecord("hash1", "123", "source1", "message1", 2L)

        // Test equality
        assertEquals(record1, record1)
        assertEquals(record1, record2)
        assertEquals(record1.hashCode(), record2.hashCode())

        // Test inequality for each property
        assertNotEquals(record1, record3)
        assertNotEquals(record1, record4)
        assertNotEquals(record1, record5)
        assertNotEquals(record1, record6)
        assertNotEquals(record1, record7)

        // Test against null and different types
        assertNotEquals(record1, null)
        assertNotEquals(record1, Any())
    }

    @Test
    fun testVerificationRecordCopy() {
        val record1 = VerificationRecord("hash1", "123", "source1", "message1", 1L)
        val record2 = record1.copy()
        val record3 = record1.copy(hash = "hash2")

        assertEquals(record1, record2)
        assertNotEquals(record1, record3)
        assertEquals("hash2", record3.hash)
    }

    @Test
    fun testIncomingMessage() {
        val msg = IncomingMessage(
            name = "John Doe",
            phone = "1234567890",
            message = "Hello, world!",
            verifyRequests = 0uL,
            timeToLive = System.currentTimeMillis() + 1000 * 60 * 5 // 5 minutes
        )

        assertEquals("John Doe", msg.name)
        assertEquals("1234567890", msg.phone)
        assertEquals("Hello, world!", msg.message)
        assertEquals(0uL, msg.verifyRequests)
        assertTrue(msg.timeToLive > System.currentTimeMillis())
    }

    @Test
    fun testIncomingMessageEqualsAndHashCode() {
        val msg1 = IncomingMessage("John", "123", "msg", 1uL, 1L)
        val msg2 = IncomingMessage("John", "123", "msg", 1uL, 1L)
        val msg3 = IncomingMessage("Jane", "123", "msg", 1uL, 1L)
        val msg4 = IncomingMessage("John", "456", "msg", 1uL, 1L)
        val msg5 = IncomingMessage("John", "123", "msg2", 1uL, 1L)
        val msg6 = IncomingMessage("John", "123", "msg", 2uL, 1L)
        val msg7 = IncomingMessage("John", "123", "msg", 1uL, 2L)
        val msgDifferentFlags = IncomingMessage("John", "123", "msg", 3uL, 1L)
        val msgZeroFlags = IncomingMessage("John", "123", "msg", 0uL, 1L)
        val msgNullName1 = IncomingMessage(null, "123", "msg", 1uL, 1L)
        val msgNullName2 = IncomingMessage(null, "123", "msg", 1uL, 1L)

        // Test equality
        assertEquals(msg1, msg1)
        assertEquals(msg1, msg2)
        assertEquals(msg1.hashCode(), msg2.hashCode())

        // Test inequality for each property
        assertNotEquals(msg1, msg3)
        assertNotEquals(msg1, msg4)
        assertNotEquals(msg1, msg5)
        assertNotEquals(msg1, msg6)
        assertNotEquals(msg1, msg7)
        assertNotEquals(msg1, msgDifferentFlags)
        assertNotEquals(msg1, msgZeroFlags)

        // Test cases with null name
        assertEquals(msgNullName1, msgNullName2)
        assertEquals(msgNullName1.hashCode(), msgNullName2.hashCode())
        assertNotEquals(msg1, msgNullName1)
        assertNotEquals(msgNullName1, msg1)
        assertNotEquals(msg1.hashCode(), msgNullName1.hashCode())


        // Test against null and different types
        assertNotEquals(msg1, null)
        assertNotEquals(msg1, Any())
    }

    @Test
    fun testIncomingMessageCopy() {
        val msg1 = IncomingMessage("John", "123", "msg", 1uL, 1L)
        val msg2 = msg1.copy()
        val msg3 = msg1.copy(name = "Jane")

        assertEquals(msg1, msg2)
        assertNotEquals(msg1, msg3)
        assertEquals("Jane", msg3.name)
    }
}