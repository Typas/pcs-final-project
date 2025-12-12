package org.ntu.pcs.telecom.server

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert.*
import org.ntu.pcs.telecom.database.AppDatabase
import org.ntu.pcs.telecom.database.UserDao
import org.ntu.pcs.telecom.database.VerificationDao
import org.ntu.pcs.telecom.models.User
import org.ntu.pcs.telecom.models.IncomingMessage
import org.ntu.pcs.telecom.server.telecomModule
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class ServerIntegrationTest {
    private lateinit var db: AppDatabase
    private lateinit var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()

        // Pre-populate user
        runBlocking {
            val userDao = db.userDao()
            val user = User("1234567890", "Test User")
            userDao.insertUser(user)
        }
        server = embeddedServer(io.ktor.server.cio.CIO, port = 6973) {
            telecomModule(db.userDao(), db.verificationDao())
        }.start(wait = false)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        if (::server.isInitialized) {
            server.stop(100L, 100L)
        }
    }

    @Test
    fun testMessageEndpointSuccess() = runBlocking {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson {
                }
            }
        }
        val message = IncomingMessage("Test User", "1234567890", "Test Message", 0uL, 3600000)

        val response: HttpResponse = client.post("http://localhost:6973/message") {
            contentType(ContentType.Application.Json)
            setBody(message)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(response.bodyAsText())
    }

    @Test
    fun testMessageEndpointPhoneNotFound() = runBlocking {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson {
                }
            }
        }
        val message = IncomingMessage("Test User", "0987654321", "Test Message", 0uL, 3600000)

        val response: HttpResponse = client.post("http://localhost:6973/message") {
            contentType(ContentType.Application.Json)
            setBody(message)
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testMessageEndpointNameMismatch() = runBlocking {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson {
                }
            }
        }
        val message = IncomingMessage("Wrong User", "1234567890", "Test Message", 0uL, 3600000)

        val response: HttpResponse = client.post("http://localhost:6973/message") {
            contentType(ContentType.Application.Json)
            setBody(message)
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }
}
