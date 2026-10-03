package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        SavedJobEntity::class,
        ApplicationEntity::class,
        JobAlertEntity::class,
        InterviewHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CareerDatabase : RoomDatabase() {
    abstract fun careerDao(): CareerDao

    companion object {
        @Volatile
        private var INSTANCE: CareerDatabase? = null

        fun getDatabase(context: Context): CareerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CareerDatabase::class.java,
                    "careermate_career_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
