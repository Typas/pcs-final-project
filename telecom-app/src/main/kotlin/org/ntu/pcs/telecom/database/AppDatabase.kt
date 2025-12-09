package org.ntu.pcs.telecom.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.ntu.pcs.telecom.models.User
import org.ntu.pcs.telecom.models.VerificationRecord

@Database(entities = [VerificationRecord::class, User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun verificationDao(): VerificationDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "telecom_database"
                )
                    .fallbackToDestructiveMigration(true) // For development purposes only
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
