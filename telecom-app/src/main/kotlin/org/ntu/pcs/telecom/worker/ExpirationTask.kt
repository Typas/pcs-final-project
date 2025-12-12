package org.ntu.pcs.telecom.worker


import io.ktor.client.*
import io.ktor.client.request.*
import org.ntu.pcs.telecom.database.VerificationDao

class ExpirationTask(
    private val verificationDao: VerificationDao,
    private val client: HttpClient
) {


    suspend fun execute() {


        val expiredRecords = verificationDao.getExpiredRecords(System.currentTimeMillis())

        for (record in expiredRecords) {


            try {
                client.post("http://${record.source}:8081/notify") {
                    setBody("{\"hash\":\"${record.hash}\", \"status\":\"expired\"}")
                }
            } catch (e: Exception) {

            }

            verificationDao.deleteByHash(record.hash)
        }
        client.close()
    }
}
