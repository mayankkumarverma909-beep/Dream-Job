package com.example.data.model

data class JobAlert(
    val id: String,
    val query: String,
    val location: String,
    val experience: String,
    val expectedSalary: String,
    val frequency: String, // Instant, Daily, Weekly Digest
    val isActive: Boolean = true,
    val matchedJobsCount: Int = 12
)
