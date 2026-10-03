package com.example.data.model

data class JobListing(
    val id: String,
    val title: String,
    val company: String,
    val location: String,
    val salary: String,
    val experience: String,
    val jobType: String, // Full-time, Internship, Contract
    val workMode: String, // Remote, Hybrid, On-site
    val sourcePlatform: String, // Naukri, LinkedIn, Indeed, Glassdoor, Internshala, Foundit, Company Career
    val datePosted: String,
    val skills: List<String>,
    val description: String,
    val applyUrl: String,
    val aiMatchPercent: Int,
    val isDemoData: Boolean = true,
    val matchBreakdown: MatchBreakdown = MatchBreakdown(),
    val whyMatches: List<String> = emptyList(),
    val whatMissing: List<String> = emptyList()
)

data class MatchBreakdown(
    val skillsMatch: Int = 92,
    val educationMatch: Int = 95,
    val experienceMatch: Int = 85,
    val locationMatch: Int = 100,
    val roleMatch: Int = 90
)
