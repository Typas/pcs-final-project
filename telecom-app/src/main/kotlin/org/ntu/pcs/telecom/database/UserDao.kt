package org.ntu.pcs.telecom.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.ntu.pcs.telecom.models.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE phone = :phone")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users WHERE phone = :phone AND name = :name")
    suspend fun getUserByPhoneAndName(phone: String, name: String): User?
}
