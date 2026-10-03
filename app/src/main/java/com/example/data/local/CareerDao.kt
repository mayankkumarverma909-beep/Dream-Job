package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CareerDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    // Saved Jobs
    @Query("SELECT * FROM saved_jobs ORDER BY id DESC")
    fun getAllSavedJobs(): Flow<List<SavedJobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedJob(job: SavedJobEntity)

    @Query("DELETE FROM saved_jobs WHERE id = :jobId")
    suspend fun removeSavedJob(jobId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_jobs WHERE id = :jobId)")
    fun isJobSaved(jobId: String): Flow<Boolean>

    // Applications
    @Query("SELECT * FROM applications ORDER BY dateApplied DESC")
    fun getAllApplications(): Flow<List<ApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: ApplicationEntity)

    @Query("DELETE FROM applications WHERE id = :appId")
    suspend fun deleteApplication(appId: String)

    @Query("UPDATE applications SET statusName = :newStatus WHERE id = :appId")
    suspend fun updateApplicationStatus(appId: String, newStatus: String)

    // Alerts
    @Query("SELECT * FROM job_alerts")
    fun getAllAlerts(): Flow<List<JobAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: JobAlertEntity)

    @Query("DELETE FROM job_alerts WHERE id = :alertId")
    suspend fun deleteAlert(alertId: String)

    // Interview History
    @Query("SELECT * FROM interview_history ORDER BY timestamp DESC")
    fun getInterviewHistory(): Flow<List<InterviewHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterviewHistory(history: InterviewHistoryEntity)
}
