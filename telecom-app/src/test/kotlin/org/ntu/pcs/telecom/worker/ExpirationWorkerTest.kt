package org.ntu.pcs.telecom.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.test.core.app.ApplicationProvider
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExpirationWorkerTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testDoWork() = runTest {
        // Since ExpirationWorker depends on AppDatabase.getDatabase which is a singleton using ApplicationContext,
        // and it creates a real HTTP client, this unit test is limited without dependency injection refactoring.
        // However, we can test that the worker can be instantiated and returns Success in a basic scenario
        // if the database is accessible (which Robolectric provides context for).

        val worker = TestListenableWorkerBuilder<ExpirationWorker>(context)
            .build()

        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success(), result)
    }
}
