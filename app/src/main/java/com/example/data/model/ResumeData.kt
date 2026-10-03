package com.example.data.model

data class ResumeData(
    val fullName: String = "Rahul Sharma",
    val title: String = "Aspiring HR Executive | MBA HR Graduate",
    val email: String = "rahul.sharma@example.com",
    val phone: String = "+91 98765 43210",
    val location: String = "Noida, India",
    val summary: String = "Energetic MBA (HR) graduate with hands-on internship experience in full-cycle recruitment, candidate screening, and HR operations. Proficient in MS Excel, ATS navigation, and cross-functional communication.",
    val education: List<EducationItem> = listOf(
        EducationItem("MBA in Human Resources", "Amity Business School, Noida", "2024 - 2026", "CGPA: 8.4/10"),
        EducationItem("B.Com (Honours)", "Delhi University", "2021 - 2024", "First Division (76%)")
    ),
    val experiences: List<ExperienceItem> = listOf(
        ExperienceItem(
            role = "Human Resources Intern",
            company = "TalentBridge Solutions",
            location = "Noida",
            duration = "May 2025 - Aug 2025",
            bullets = listOf(
                "Sourced and screened 150+ candidate profiles across LinkedIn and Naukri for technical and sales roles.",
                "Coordinated end-to-end interview rounds for 45 shortlisted candidates with hiring managers.",
                "Assisted in onboarding 20+ new hires, drafting offer documentation and orientation decks.",
                "Maintained candidate tracker in Excel with pivot tables, improving reporting speed by 30%."
            )
        )
    ),
    val projects: List<ProjectItem> = listOf(
        ProjectItem(
            name = "Campus Recruitment Drive Automation",
            description = "Designed an Excel-based automated candidate evaluation matrix reducing screening time by 40% during college placement season.",
            tools = "Advanced Excel, Google Forms, HR Analytics"
        )
    ),
    val skills: List<String> = listOf("Talent Acquisition", "HR Operations", "Candidate Screening", "Microsoft Excel", "HRMS Basics", "Effective Communication", "Payroll Overview", "Onboarding"),
    val certifications: List<String> = listOf("SHRM Student Member Certification (2025)", "Excel for HR Professionals - Coursera (2025)"),
    val achievements: List<String> = listOf("Elected Placement Cell Student Coordinator (2025-2026)", "Winner of Inter-College Case Study Competition on Employee Retention")
)

data class EducationItem(
    val degree: String,
    val institution: String,
    val period: String,
    val grade: String
)

data class ExperienceItem(
    val role: String,
    val company: String,
    val location: String,
    val duration: String,
    val bullets: List<String>
)

data class ProjectItem(
    val name: String,
    val description: String,
    val tools: String
)

enum class ResumeTemplate {
    MODERN_TECH,
    EXECUTIVE_CLEAN,
    CREATIVE_MINIMAL
}
