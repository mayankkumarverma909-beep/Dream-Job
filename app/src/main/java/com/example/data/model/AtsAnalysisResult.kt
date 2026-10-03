package com.example.data.model

data class AtsAnalysisResult(
    val atsCompatibilityScore: Int = 84, // 0-100
    val targetRole: String = "HR Executive",
    val keywordMatchPercent: Int = 88,
    val formattingScore: Int = 92,
    val skillsAlignmentScore: Int = 79,
    val experienceRelevanceScore: Int = 86,
    val matchedKeywords: List<String> = listOf("Recruitment", "HR Operations", "Excel", "Candidate Sourcing", "Interview Scheduling", "Onboarding"),
    val missingKeywords: List<String> = listOf("HRMS", "Advanced Excel (VLOOKUP, Pivot Tables)", "HR Analytics", "Statutory Compliance"),
    val parsingIssues: List<String> = listOf(
        "Header text size is slightly compact; consider 16pt for major sections.",
        "Ensure bullet points start with strong action verbs (e.g., 'Spearheaded', 'Optimized', 'Negotiated')."
    ),
    val actionableImprovements: List<String> = listOf(
        "Add your experience with HR Operations and Onboarding in the summary, as they appear frequently in target job descriptions.",
        "Quantify your Excel achievements (e.g., 'Streamlined candidate data across 500+ applicants using Pivot Tables').",
        "Include knowledge of basic HRMS tools (like Zoho People, Darwinbox or Workday)."
    ),
    val disclaimer: String = "Note: This ATS score is an optimization guideline based on semantic matching and formatting standards. It does not guarantee passing any specific employer's proprietary ATS scanner."
)
