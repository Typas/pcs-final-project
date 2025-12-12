package org.ntu.pcs.telecom.server

import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.origin
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ntu.pcs.telecom.database.AppDatabase
import org.ntu.pcs.telecom.database.UserDao
import org.ntu.pcs.telecom.database.VerificationDao
import org.ntu.pcs.telecom.models.IncomingMessage
import org.ntu.pcs.telecom.models.VerificationRecord
import org.ntu.pcs.telecom.util.ByteArrayAdapter
import java.security.MessageDigest
import java.util.UUID

internal fun generateUniqueHash(phone: String, message: String): String {
    val input = "$phone-$message-${System.currentTimeMillis()}-${UUID.randomUUID()}"
    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.fold("") { str, it -> str + "%02x".format(it) }
}

fun Application.telecomModule(userDao: UserDao, verificationDao: VerificationDao) {
    install(ContentNegotiation) {
        gson {
            registerTypeAdapter(ByteArray::class.java, ByteArrayAdapter())
        }
    }
    routing {
        post("/message") {
            val incomingMessage = call.receive<IncomingMessage>()

            val user = userDao.getUserByPhone(incomingMessage.phone)

            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "Phone number not found.")
                return@post
            }

            if (incomingMessage.name != null && incomingMessage.name != user.name) {
                call.respond(HttpStatusCode.Forbidden, "Name does not match phone number.")
                return@post
            }

            val hash = generateUniqueHash(incomingMessage.phone, incomingMessage.message)
            val verificationRecord = VerificationRecord(
                hash = hash,
                phone = incomingMessage.phone,
                source = call.request.origin.remoteHost,
                message = incomingMessage.message,
                timeToLive = System.currentTimeMillis() + incomingMessage.timeToLive
            )
            verificationDao.insert(verificationRecord)

            // TODO: Send SMS message
            // SmsManager.getDefault().sendTextMessage(incomingMessage.phone, null, incomingMessage.message, null, null)

            call.respond(HttpStatusCode.OK, hash)
        }
    }
}

class KtorServer(private val db: AppDatabase) : Server {

    private val verificationDao = db.verificationDao()
    private val userDao = db.userDao()
    private var engine: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    override fun start() {
        engine = embeddedServer(CIO, port = 6973) {
            telecomModule(userDao, verificationDao)
        }.start(wait = false)
    }

    override fun stop() {
        engine?.stop(100L, 100L)
    }
}
