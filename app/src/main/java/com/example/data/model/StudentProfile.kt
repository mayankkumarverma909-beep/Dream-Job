package com.example.data.model

data class StudentProfile(
    val fullName: String = "Rahul Sharma",
    val email: String = "rahul.sharma@example.com",
    val phone: String = "+91 98765 43210",
    val currentEducation: String = "Post Graduate",
    val degree: String = "MBA / PGDM",
    val specialization: String = "Human Resources",
    val college: String = "Amity Business School",
    val graduationYear: String = "2026",
    val workExperience: String = "Fresher (0 years)",
    val internshipExperience: String = "HR Intern at TalentBridge Solutions (3 months) - Handled sourcing, onboarding & employee engagement",
    val skills: List<String> = listOf("Recruitment", "HR Operations", "Excel", "Communication", "Talent Sourcing", "Interview Scheduling"),
    val preferredRoles: List<String> = listOf("HR Executive", "Talent Acquisition Associate", "HR Generalist", "HR Analyst"),
    val preferredIndustries: List<String> = listOf("Information Technology", "FinTech", "Consulting", "EdTech"),
    val preferredLocations: List<String> = listOf("Noida", "Delhi NCR", "Gurgaon", "Bengaluru"),
    val workModePreference: String = "Hybrid", // Remote, Hybrid, On-site
    val expectedSalary: String = "₹4–6 LPA",
    val linkedInUrl: String = "https://linkedin.com/in/rahul-sharma-hr",
    val githubOrPortfolioUrl: String = "https://rahul-hr-portfolio.vercel.app",
    val resumeUploaded: Boolean = true,
    val resumeFileName: String = "Rahul_Sharma_HR_Resume.pdf"
) {
    fun calculateCompleteness(): Int {
        var score = 0
        if (fullName.isNotBlank()) score += 10
        if (email.isNotBlank()) score += 10
        if (degree.isNotBlank() && specialization.isNotBlank()) score += 15
        if (college.isNotBlank()) score += 10
        if (skills.size >= 4) score += 20 else if (skills.isNotEmpty()) score += 10
        if (internshipExperience.isNotBlank()) score += 15
        if (preferredRoles.isNotEmpty()) score += 10
        if (resumeUploaded) score += 10
        return score.coerceIn(0, 100)
    }

    fun getImprovementSuggestions(): List<String> {
        val list = mutableListOf<String>()
        if (skills.size < 6) list.add("Add 2 more technical/analytical skills (e.g. Advanced Excel, HRMS)")
        if (preferredLocations.size < 3) list.add("Expand your preferred job locations for 35% more reach")
        if (githubOrPortfolioUrl.isBlank()) list.add("Add a link to your case studies or portfolio")
        if (!resumeUploaded) list.add("Upload your latest PDF/DOCX resume")
        if (list.isEmpty()) {
            list.add("Great job! Your profile is highly competitive for entry-level HR roles.")
        }
        return list
    }
}
