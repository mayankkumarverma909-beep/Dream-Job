package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String,
    val email: String,
    val phone: String,
    val currentEducation: String,
    val degree: String,
    val specialization: String,
    val college: String,
    val graduationYear: String,
    val workExperience: String,
    val internshipExperience: String,
    val skillsCsv: String,
    val preferredRolesCsv: String,
    val preferredIndustriesCsv: String,
    val preferredLocationsCsv: String,
    val workModePreference: String,
    val expectedSalary: String,
    val linkedInUrl: String,
    val githubOrPortfolioUrl: String,
    val resumeUploaded: Boolean
)

@Entity(tableName = "saved_jobs")
data class SavedJobEntity(
    @PrimaryKey val id: String,
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val experience: String,
    val jobType: String,
    val workMode: String,
    val sourcePlatform: String,
    val datePosted: String,
    val skillsCsv: String,
    val applyUrl: String,
    val aiMatchPercent: Int
)

@Entity(tableName = "applications")
data class ApplicationEntity(
    @PrimaryKey val id: String,
    val jobTitle: String,
    val company: String,
    val location: String,
    val salary: String,
    val platform: String,
    val dateApplied: String,
    val statusName: String,
    val notes: String,
    val nextRoundDate: String?
)

@Entity(tableName = "job_alerts")
data class JobAlertEntity(
    @PrimaryKey val id: String,
    val query: String,
    val location: String,
    val experience: String,
    val expectedSalary: String,
    val frequency: String,
    val isActive: Boolean
)

@Entity(tableName = "interview_history")
data class InterviewHistoryEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val role: String,
    val overallScore: Int,
    val communicationScore: Int,
    val answerQualityScore: Int,
    val feedbackSummary: String
)
