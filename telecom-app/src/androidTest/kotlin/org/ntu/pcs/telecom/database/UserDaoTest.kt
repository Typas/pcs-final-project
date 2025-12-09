package org.ntu.pcs.telecom.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.util.Log
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert.*
import org.ntu.pcs.telecom.models.User

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetUser() = runBlocking {
        Log.d("UserDaoTest", ">>> Running insertAndGetUser test")
        val user = User("1234567890", "Test User")
        userDao.insertUser(user)
        val byPhone = userDao.getUserByPhone("1234567890")
        assertEquals(byPhone, user)
    }

    @Test
    @Throws(Exception::class)
    fun getUserByNameAndPhone() = runBlocking {
        val user = User("1234567890", "Test User")
        userDao.insertUser(user)
        val byPhoneAndName = userDao.getUserByPhoneAndName("1234567890", "Test User")
        assertEquals(byPhoneAndName, user)
    }
}
