package org.ntu.pcs.telecom.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import org.ntu.pcs.telecom.database.AppDatabase

class ExpirationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val TAG = "ExpirationWorker"

    override suspend fun doWork(): Result {
        Log.d(TAG, "Checking for expired records...")

        val db = AppDatabase.getDatabase(applicationContext)
        val verificationDao = db.verificationDao()
        val client = HttpClient(CIO)

        val expiredRecords = verificationDao.getExpiredRecords(System.currentTimeMillis())

        for (record in expiredRecords) {
            Log.d(TAG, "Record with hash ${record.hash} has expired. Notifying source and deleting.")

            try {
                client.post("http://${record.source}:8081/notify") {
                    setBody("{\"hash\":\"${record.hash}\", \"status\":\"expired\"}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send expiration notice to source: ${e.message}")
            }

            verificationDao.deleteByHash(record.hash)
        }

        client.close()
        return Result.success()
    }
}
