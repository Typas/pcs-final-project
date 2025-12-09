package org.ntu.pcs.telecom.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.ntu.pcs.telecom.models.VerificationRecord

@Dao
interface VerificationDao {
    @Insert
    suspend fun insert(record: VerificationRecord)

    @Query("SELECT * FROM verification_records WHERE hash = :hash AND phone = :phone")
    suspend fun getRecordByHashAndPhone(hash: String, phone: String): VerificationRecord?

    @Query("SELECT * FROM verification_records WHERE hash = :hash")
    suspend fun getRecordByHash(hash: String): VerificationRecord?

    @Query("DELETE FROM verification_records WHERE hash = :hash")
    suspend fun deleteByHash(hash: String)

    @Query("SELECT * FROM verification_records WHERE timeToLive <= :currentTime")
    suspend fun getExpiredRecords(currentTime: Long): List<VerificationRecord>
}
