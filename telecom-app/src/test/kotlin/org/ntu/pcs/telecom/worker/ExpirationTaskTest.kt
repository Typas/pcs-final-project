package org.ntu.pcs.telecom.worker

import io.ktor.client.HttpClient
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.ArgumentMatchers.anyLong
import org.ntu.pcs.telecom.database.VerificationDao
import org.ntu.pcs.telecom.models.VerificationRecord

class ExpirationTaskTest {

    private lateinit var mockVerificationDao: VerificationDao
    private lateinit var mockHttpClient: HttpClient

    @Before
    fun setUp() {
        mockVerificationDao = mock(VerificationDao::class.java)
        mockHttpClient = mock(HttpClient::class.java)
    }

    @Test
    fun `test execute with no expired records`() = runTest {
        `when`(mockVerificationDao.getExpiredRecords(anyLong())).thenReturn(emptyList())

        val expirationTask = ExpirationTask(mockVerificationDao, mockHttpClient)
        expirationTask.execute()

        verify(mockVerificationDao).getExpiredRecords(anyLong())
    }

    @Test
    fun `test execute with one expired record`() = runTest {
        val expiredRecord = VerificationRecord(
            hash = "expired_hash",
            phone = "1234567890",
            source = "test_source",
            message = "test_message",
            timeToLive = System.currentTimeMillis() - 1000
        )
        `when`(mockVerificationDao.getExpiredRecords(anyLong())).thenReturn(listOf(expiredRecord))

        val expirationTask = ExpirationTask(mockVerificationDao, mockHttpClient)
        expirationTask.execute()

        verify(mockVerificationDao).getExpiredRecords(anyLong())
        verify(mockVerificationDao).deleteByHash("expired_hash")
        // We can also verify the HTTP client call if needed
    }
}
