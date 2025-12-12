package org.ntu.pcs.telecom.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import io.ktor.client.*
import org.ntu.pcs.telecom.database.AppDatabase

class ExpirationWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val appDatabase: AppDatabase,
    private val httpClient: HttpClient
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val verificationDao = appDatabase.verificationDao()
        val expirationTask = ExpirationTask(verificationDao, httpClient)
        expirationTask.execute()
        return ListenableWorker.Result.success()
    }
}
