package org.ntu.pcs.telecom.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert.*
import org.ntu.pcs.telecom.models.VerificationRecord

@RunWith(AndroidJUnit4::class)
class VerificationDaoTest {
    private lateinit var verificationDao: VerificationDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        verificationDao = db.verificationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetRecord() = runBlocking {
        val record = VerificationRecord("hash", "1234567890", "source", "message", System.currentTimeMillis())
        verificationDao.insert(record)
        val byHash = verificationDao.getRecordByHash("hash")
        assertEquals(byHash, record)
    }

    @Test
    @Throws(Exception::class)
    fun getRecordByHashAndPhone() = runBlocking {
        val record = VerificationRecord("hash", "1234567890", "source", "message", System.currentTimeMillis())
        verificationDao.insert(record)
        val byHashAndPhone = verificationDao.getRecordByHashAndPhone("hash", "1234567890")
        assertEquals(byHashAndPhone, record)
    }

    @Test
    @Throws(Exception::class)
    fun deleteByHash() = runBlocking {
        val record = VerificationRecord("hash", "1234567890", "source", "message", System.currentTimeMillis())
        verificationDao.insert(record)
        verificationDao.deleteByHash("hash")
        val byHash = verificationDao.getRecordByHash("hash")
        assertNull(byHash)
    }

    @Test
    @Throws(Exception::class)
    fun getExpiredRecords() = runBlocking {
        val expiredRecord = VerificationRecord("expired", "1", "s", "m", System.currentTimeMillis() - 1000)
        val validRecord = VerificationRecord("valid", "2", "s", "m", System.currentTimeMillis() + 10000)
        verificationDao.insert(expiredRecord)
        verificationDao.insert(validRecord)
        val expired = verificationDao.getExpiredRecords(System.currentTimeMillis())
        assertEquals(1, expired.size)
        assertEquals(expired[0], expiredRecord)
    }
}
