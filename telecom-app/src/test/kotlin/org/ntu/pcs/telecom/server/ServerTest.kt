package org.ntu.pcs.telecom.server

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.testing.*
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.ntu.pcs.telecom.database.UserDao
import org.ntu.pcs.telecom.database.VerificationDao
import org.ntu.pcs.telecom.models.IncomingMessage
import org.ntu.pcs.telecom.models.User
import org.ntu.pcs.telecom.server.telecomModule
import org.ntu.pcs.telecom.server.generateUniqueHash

class ServerTest {

    private val mockUserDao: UserDao = mock(UserDao::class.java)
    private val mockVerificationDao: VerificationDao = mock(VerificationDao::class.java)

    @Test
    fun testMessageEndpointSuccess() = testApplication {
        application {
            telecomModule(mockUserDao, mockVerificationDao)
        }
        val client = createClient {
            install(ContentNegotiation) {
                gson {
                    registerTypeAdapter(ByteArray::class.java, org.ntu.pcs.telecom.util.ByteArrayAdapter())
                }
            }
        }

        val testUser = User("1234567890", "Test User")
        `when`(mockUserDao.getUserByPhone("1234567890")).thenReturn(testUser)

        val message = IncomingMessage("Test User", "1234567890", "Test Message", byteArrayOf(), 3600000)

        val response = client.post("/message") {
            contentType(ContentType.Application.Json)
            setBody(message)
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testMessageEndpointPhoneNotFound() = testApplication {
        application {
            telecomModule(mockUserDao, mockVerificationDao)
        }
        val client = createClient {
            install(ContentNegotiation) {
                gson {
                    registerTypeAdapter(ByteArray::class.java, org.ntu.pcs.telecom.util.ByteArrayAdapter())
                }
            }
        }

        `when`(mockUserDao.getUserByPhone("0987654321")).thenReturn(null)

        val message = IncomingMessage("Test User", "0987654321", "Test Message", byteArrayOf(), 3600000)

        val response = client.post("/message") {
            contentType(ContentType.Application.Json)
            setBody(message)
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testMessageEndpointNameMismatch() = testApplication {
        application {
            telecomModule(mockUserDao, mockVerificationDao)
        }
        val client = createClient {
            install(ContentNegotiation) {
                gson {
                    registerTypeAdapter(ByteArray::class.java, org.ntu.pcs.telecom.util.ByteArrayAdapter())
                }
            }
        }

        val testUser = User("1234567890", "Test User")
        `when`(mockUserDao.getUserByPhone("1234567890")).thenReturn(testUser)

        val message = IncomingMessage("Wrong User", "1234567890", "Test Message", byteArrayOf(), 3600000)

        val response = client.post("/message") {
            contentType(ContentType.Application.Json)
            setBody(message)
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun testGenerateUniqueHash() {
        val phone = "1234567890"
        val message = "Test message"

        val hash1 = generateUniqueHash(phone, message)
        val hash2 = generateUniqueHash(phone, message)

        assertNotNull(hash1)
        assertNotNull(hash2)
        assertNotEquals(hash1, hash2)
        assertEquals(64, hash1.length)
    }
}

