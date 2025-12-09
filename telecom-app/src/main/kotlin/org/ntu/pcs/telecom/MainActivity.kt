package org.ntu.pcs.telecom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import org.ntu.pcs.telecom.database.AppDatabase
import org.ntu.pcs.telecom.server.KtorServer
import org.ntu.pcs.telecom.worker.ExpirationWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val server = KtorServer(db)
            server.start()
        }

        scheduleExpirationWorker()
    }

    private fun scheduleExpirationWorker() {
        val expirationWorkRequest =
            PeriodicWorkRequestBuilder<ExpirationWorker>(1, TimeUnit.HOURS)
                .build()

        WorkManager.getInstance(applicationContext).enqueue(expirationWorkRequest)
    }
}
