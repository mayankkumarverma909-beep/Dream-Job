package com.example.data.model

data class SkillGapModel(
    val targetRole: String = "HR Executive / HR Generalist",
    val requiredSkills: List<String> = listOf("Recruitment", "Excel", "HR Operations", "HRMS Tools", "HR Analytics", "Labor Laws & Statutory", "Employee Relations"),
    val userSkills: List<String> = listOf("Recruitment", "Excel", "HR Operations", "Communication", "Candidate Screening"),
    val matchedSkills: List<String> = listOf("Recruitment", "Excel", "HR Operations"),
    val missingSkills: List<String> = listOf("HR Analytics", "HRMS Tools (Darwinbox/Zoho)", "Labor Laws & Compliance", "Employee Relations"),
    val readinessPercentage: Int = 68,
    val learningRoadmap: List<RoadmapStage> = listOf(
        RoadmapStage(
            stageNumber = 1,
            title = "Master Advanced Excel for HR",
            description = "Learn XLOOKUP, Nested IFs, Dynamic Pivot Tables, and attrition metrics dashboarding.",
            duration = "1-2 Weeks",
            isCompleted = true,
            resources = listOf("Excel for HR Analytics Bootcamp", "Free Practice Data Sets")
        ),
        RoadmapStage(
            stageNumber = 2,
            title = "Learn Core HRMS & Statutory Basics",
            description = "Understand PF, ESI, Gratuity calculations, and hands-on overview of tools like Zoho People.",
            duration = "2 Weeks",
            isCompleted = false,
            resources = listOf("Statutory Compliance 101 for Freshers", "HRMS Sandbox Tutorials")
        ),
        RoadmapStage(
            stageNumber = 3,
            title = "Improve Interview Communication & STAR Method",
            description = "Practice 15+ scenario-based behavioral questions with CareerMate AI Virtual Interviewer.",
            duration = "1 Week",
            isCompleted = false,
            resources = listOf("Interactive Virtual Mock Interview", "STAR Framework Cheat Sheet")
        ),
        RoadmapStage(
            stageNumber = 4,
            title = "Optimize Resume & Achieve 85+ ATS Score",
            description = "Align bullet points to target job keywords and highlight internship accomplishments.",
            duration = "3 Days",
            isCompleted = true,
            resources = listOf("CareerMate ATS Optimizer", "HR Executive Resume Template")
        ),
        RoadmapStage(
            stageNumber = 5,
            title = "Apply to 10+ Matched Jobs Daily",
            description = "Target entry-level openings on Naukri, LinkedIn, and Indeed using direct 1-click apply links.",
            duration = "Ongoing",
            isCompleted = false,
            resources = listOf("Aggregated Jobs Feed", "Application Tracker")
        )
    )
)

data class RoadmapStage(
    val stageNumber: Int,
    val title: String,
    val description: String,
    val duration: String,
    val isCompleted: Boolean,
    val resources: List<String>
)
