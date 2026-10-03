package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CareerRepository(private val dao: CareerDao) {

    // Default Seed Jobs (Realistically modeled with genuine external portal URLs & platform designations)
    val demoJobs: List<JobListing> = listOf(
        JobListing(
            id = "job-naukri-101",
            title = "HR Executive (Talent Acquisition & Ops)",
            company = "NexGen Infotech Solutions",
            location = "Noida, Sector 62",
            salary = "₹4.5–6.0 LPA",
            experience = "0–2 years (Freshers Welcome)",
            jobType = "Full-time",
            workMode = "Hybrid",
            sourcePlatform = "Naukri",
            datePosted = "Today",
            skills = listOf("Recruitment", "HR Operations", "Excel", "Communication", "Sourcing"),
            description = "Seeking a driven HR Executive fresher or early-career professional to join our Noida campus team. Key responsibilities include posting openings on job boards, shortlisting candidate CVs, conducting preliminary screening calls, organizing assessment rounds, and coordinating new-hire documentation.",
            applyUrl = "https://www.naukri.com/job-listings-hr-executive-noida",
            aiMatchPercent = 94,
            matchBreakdown = MatchBreakdown(skillsMatch = 96, educationMatch = 100, experienceMatch = 90, locationMatch = 100, roleMatch = 95),
            whyMatches = listOf(
                "Your MBA in HR directly matches the requested degree.",
                "Your internship in talent acquisition covers 100% of candidate screening tasks.",
                "Your location preference is Noida (Job is Sector 62)."
            ),
            whatMissing = listOf(
                "Experience with automated ATS software like Darwinbox or BambooHR.",
                "Statutory compliance basics (PF/ESI)."
            )
        ),
        JobListing(
            id = "job-linkedin-202",
            title = "Associate HR Specialist",
            company = "Cognizant Technology",
            location = "Noida / Gurugram",
            salary = "₹5.0–7.2 LPA",
            experience = "Fresher (2025/2026 Batch)",
            jobType = "Full-time",
            workMode = "Hybrid",
            sourcePlatform = "LinkedIn",
            datePosted = "1 day ago",
            skills = listOf("HR Operations", "Candidate Screening", "Communication", "Excel", "HR Analytics"),
            description = "Cognizant is hiring fresh MBA graduates for our People Operations team. You will support global workforce management, employee onboarding cohorts, quarterly HR metrics dashboarding, and candidate experience optimization.",
            applyUrl = "https://www.linkedin.com/jobs/view/hr-specialist-noida",
            aiMatchPercent = 91,
            matchBreakdown = MatchBreakdown(skillsMatch = 88, educationMatch = 100, experienceMatch = 85, locationMatch = 95, roleMatch = 92),
            whyMatches = listOf(
                "Target batch 2025/2026 matches your graduation year.",
                "Strong requirement for MS Excel and employee lifecycle management."
            ),
            whatMissing = listOf(
                "Knowledge of Power BI / HR Analytics dashboards."
            )
        ),
        JobListing(
            id = "job-internshala-303",
            title = "Human Resources & Talent Management Intern",
            company = "Razorpay",
            location = "Remote",
            salary = "₹25,000–35,000 / month (PPO Eligible)",
            experience = "0 years",
            jobType = "Internship",
            workMode = "Remote",
            sourcePlatform = "Internshala",
            datePosted = "2 days ago",
            skills = listOf("Talent Sourcing", "Interview Scheduling", "Excel", "Employer Branding"),
            description = "Join India's leading FinTech innovator for a 6-month intensive HR internship with direct Pre-Placement Offer (PPO) evaluation. Help organize university hiring drives, review technical assessments, and support HR tech initiatives.",
            applyUrl = "https://internshala.com/internship/detail/human-resources-remote",
            aiMatchPercent = 96,
            matchBreakdown = MatchBreakdown(skillsMatch = 98, educationMatch = 95, experienceMatch = 100, locationMatch = 100, roleMatch = 95),
            whyMatches = listOf(
                "100% remote work mode matches your preferences.",
                "High PPO conversion rate for top performers.",
                "Directly utilizes your talent sourcing and communication skills."
            ),
            whatMissing = listOf(
                "Tech stack screening knowledge (basic Java/Python vocabulary)."
            )
        ),
        JobListing(
            id = "job-indeed-404",
            title = "Junior Talent Acquisition Partner",
            company = "Zomato",
            location = "Gurugram / Delhi NCR",
            salary = "₹4.8–6.5 LPA",
            experience = "0–1 years",
            jobType = "Full-time",
            workMode = "On-site",
            sourcePlatform = "Indeed",
            datePosted = "3 days ago",
            skills = listOf("Recruitment", "Negotiation", "Communication", "Excel", "Campus Hiring"),
            description = "Zomato is seeking dynamic HR freshers who love engaging with young talent, organizing dynamic hiring hackathons, and managing swift turnaround interview processes.",
            applyUrl = "https://in.indeed.com/viewjob?jk=zomato-talent-acquisition",
            aiMatchPercent = 89,
            matchBreakdown = MatchBreakdown(skillsMatch = 90, educationMatch = 95, experienceMatch = 80, locationMatch = 90, roleMatch = 90),
            whyMatches = listOf(
                "Your placement coordinator experience is a huge plus for campus recruitment drives."
            ),
            whatMissing = listOf(
                "High-volume fast-paced salary negotiation experience."
            )
        ),
        JobListing(
            id = "job-glassdoor-505",
            title = "People & Culture Coordinator",
            company = "Swiggy",
            location = "Bengaluru / Noida",
            salary = "₹5.5–7.5 LPA",
            experience = "0–2 years",
            jobType = "Full-time",
            workMode = "Hybrid",
            sourcePlatform = "Glassdoor",
            datePosted = "4 days ago",
            skills = listOf("HR Operations", "Employee Engagement", "Excel", "Onboarding", "Conflict Resolution"),
            description = "Passionate about culture building and employee happiness? Coordinate wellness campaigns, survey feedback loops, buddy programs, and HR helpdesk ticketing.",
            applyUrl = "https://www.glassdoor.co.in/job-listing/people-operations-coordinator",
            aiMatchPercent = 87,
            matchBreakdown = MatchBreakdown(skillsMatch = 85, educationMatch = 95, experienceMatch = 80, locationMatch = 85, roleMatch = 90),
            whyMatches = listOf(
                "Excellent fit for candidate engagement and culture focus."
            ),
            whatMissing = listOf(
                "Familiarity with Slack bots and engagement analytics."
            )
        ),
        JobListing(
            id = "job-foundit-606",
            title = "HR Generalist Trainee",
            company = "Tata Consultancy Services (TCS)",
            location = "Noida",
            salary = "₹4.2–5.5 LPA",
            experience = "Fresher (0-1 years)",
            jobType = "Full-time",
            workMode = "On-site",
            sourcePlatform = "Foundit",
            datePosted = "Just now",
            skills = listOf("Statutory Compliance", "Recruitment", "Excel", "Documentation", "Communication"),
            description = "Entry-level HR generalist role in TCS Noida. Assist HR Business Partners in handling personnel files, attendance records, leaves reconciliation, and exit clearance protocols.",
            applyUrl = "https://www.foundit.in/job/tcs-hr-generalist-noida",
            aiMatchPercent = 88,
            matchBreakdown = MatchBreakdown(skillsMatch = 86, educationMatch = 100, experienceMatch = 85, locationMatch = 100, roleMatch = 85),
            whyMatches = listOf(
                "Prestigious brand value with structured training academy for freshers."
            ),
            whatMissing = listOf(
                "Statutory compliance knowledge."
            )
        ),
        JobListing(
            id = "job-career-707",
            title = "University Recruitment Associate",
            company = "Wipro Careers",
            location = "Delhi NCR / Greater Noida",
            salary = "₹4.0–5.2 LPA",
            experience = "0–1 years",
            jobType = "Full-time",
            workMode = "Hybrid",
            sourcePlatform = "Company Career",
            datePosted = "1 day ago",
            skills = listOf("Campus Hiring", "Event Coordination", "Excel", "Aptitude Tests Admin"),
            description = "Official Wipro career opening. Coordinate logistics for 100+ partner universities across North India, manage online assessment portals, and oversee orientation weeks.",
            applyUrl = "https://careers.wipro.com/jobs/university-relations-associate",
            aiMatchPercent = 92,
            matchBreakdown = MatchBreakdown(skillsMatch = 94, educationMatch = 95, experienceMatch = 90, locationMatch = 95, roleMatch = 90),
            whyMatches = listOf(
                "Your placement cell coordination role matches 1-to-1 with university hiring needs."
            ),
            whatMissing = listOf(
                "Candidate CRM data entry certification."
            )
        )
    )

    // User Profile Flow
    val userProfile: Flow<StudentProfile> = dao.getUserProfile().map { entity ->
        if (entity == null) {
            StudentProfile()
        } else {
            StudentProfile(
                fullName = entity.fullName,
                email = entity.email,
                phone = entity.phone,
                currentEducation = entity.currentEducation,
                degree = entity.degree,
                specialization = entity.specialization,
                college = entity.college,
                graduationYear = entity.graduationYear,
                workExperience = entity.workExperience,
                internshipExperience = entity.internshipExperience,
                skills = entity.skillsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                preferredRoles = entity.preferredRolesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                preferredIndustries = entity.preferredIndustriesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                preferredLocations = entity.preferredLocationsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                workModePreference = entity.workModePreference,
                expectedSalary = entity.expectedSalary,
                linkedInUrl = entity.linkedInUrl,
                githubOrPortfolioUrl = entity.githubOrPortfolioUrl,
                resumeUploaded = entity.resumeUploaded
            )
        }
    }

    suspend fun saveProfile(profile: StudentProfile) {
        val entity = UserProfileEntity(
            id = 1,
            fullName = profile.fullName,
            email = profile.email,
            phone = profile.phone,
            currentEducation = profile.currentEducation,
            degree = profile.degree,
            specialization = profile.specialization,
            college = profile.college,
            graduationYear = profile.graduationYear,
            workExperience = profile.workExperience,
            internshipExperience = profile.internshipExperience,
            skillsCsv = profile.skills.joinToString(", "),
            preferredRolesCsv = profile.preferredRoles.joinToString(", "),
            preferredIndustriesCsv = profile.preferredIndustries.joinToString(", "),
            preferredLocationsCsv = profile.preferredLocations.joinToString(", "),
            workModePreference = profile.workModePreference,
            expectedSalary = profile.expectedSalary,
            linkedInUrl = profile.linkedInUrl,
            githubOrPortfolioUrl = profile.githubOrPortfolioUrl,
            resumeUploaded = profile.resumeUploaded
        )
        dao.saveUserProfile(entity)
    }

    // Saved Jobs Flow
    val savedJobs: Flow<List<JobListing>> = dao.getAllSavedJobs().map { list ->
        list.map { entity ->
            JobListing(
                id = entity.id,
                title = entity.title,
                company = entity.company,
                location = entity.location,
                salary = entity.salary,
                experience = entity.experience,
                jobType = entity.jobType,
                workMode = entity.workMode,
                sourcePlatform = entity.sourcePlatform,
                datePosted = entity.datePosted,
                skills = entity.skillsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                description = "",
                applyUrl = entity.applyUrl,
                aiMatchPercent = entity.aiMatchPercent,
                isDemoData = true
            )
        }
    }

    suspend fun toggleSaveJob(job: JobListing, isSaved: Boolean) {
        if (isSaved) {
            dao.removeSavedJob(job.id)
        } else {
            dao.insertSavedJob(
                SavedJobEntity(
                    id = job.id,
                    title = job.title,
                    company = job.company,
                    location = job.location,
                    salary = job.salary,
                    experience = job.experience,
                    jobType = job.jobType,
                    workMode = job.workMode,
                    sourcePlatform = job.sourcePlatform,
                    datePosted = job.datePosted,
                    skillsCsv = job.skills.joinToString(", "),
                    applyUrl = job.applyUrl,
                    aiMatchPercent = job.aiMatchPercent
                )
            )
        }
    }

    fun isJobSaved(jobId: String): Flow<Boolean> = dao.isJobSaved(jobId)

    // Applications Flow
    val applications: Flow<List<ApplicationItem>> = dao.getAllApplications().map { list ->
        if (list.isEmpty()) {
            getInitialDemoApplications()
        } else {
            list.map { entity ->
                ApplicationItem(
                    id = entity.id,
                    jobTitle = entity.jobTitle,
                    company = entity.company,
                    location = entity.location,
                    salary = entity.salary,
                    platform = entity.platform,
                    dateApplied = entity.dateApplied,
                    status = try { ApplicationStatus.valueOf(entity.statusName) } catch (e: Exception) { ApplicationStatus.APPLIED },
                    notes = entity.notes,
                    nextRoundDate = entity.nextRoundDate
                )
            }
        }
    }

    suspend fun addApplication(app: ApplicationItem) {
        dao.insertApplication(
            ApplicationEntity(
                id = app.id,
                jobTitle = app.jobTitle,
                company = app.company,
                location = app.location,
                salary = app.salary,
                platform = app.platform,
                dateApplied = app.dateApplied,
                statusName = app.status.name,
                notes = app.notes,
                nextRoundDate = app.nextRoundDate
            )
        )
    }

    suspend fun updateAppStatus(appId: String, newStatus: ApplicationStatus) {
        dao.updateApplicationStatus(appId, newStatus.name)
    }

    suspend fun deleteApp(appId: String) {
        dao.deleteApplication(appId)
    }

    private fun getInitialDemoApplications(): List<ApplicationItem> {
        return listOf(
            ApplicationItem(
                id = "app-1",
                jobTitle = "HR Executive",
                company = "NexGen Infotech Solutions",
                location = "Noida",
                salary = "₹5 LPA",
                platform = "Naukri",
                dateApplied = "Oct 01, 2026",
                status = ApplicationStatus.INTERVIEW,
                notes = "Technical round cleared. HR video interview scheduled.",
                nextRoundDate = "Tomorrow, 3:00 PM"
            ),
            ApplicationItem(
                id = "app-2",
                jobTitle = "HR Intern (Talent Acquisition)",
                company = "Razorpay",
                location = "Remote",
                salary = "₹30,000/mo",
                platform = "Internshala",
                dateApplied = "Sep 28, 2026",
                status = ApplicationStatus.ASSESSMENT,
                notes = "Completed Excel assignment and candidate scenario test.",
                nextRoundDate = "Pending Review"
            ),
            ApplicationItem(
                id = "app-3",
                jobTitle = "Associate HR Specialist",
                company = "Cognizant",
                location = "Noida",
                salary = "₹5.5 LPA",
                platform = "LinkedIn",
                dateApplied = "Sep 25, 2026",
                status = ApplicationStatus.APPLIED,
                notes = "Applied via Easy Apply with customized resume.",
                nextRoundDate = null
            ),
            ApplicationItem(
                id = "app-4",
                jobTitle = "People Coordinator",
                company = "Swiggy",
                location = "Bengaluru",
                salary = "₹6 LPA",
                platform = "Glassdoor",
                dateApplied = "Sep 20, 2026",
                status = ApplicationStatus.SAVED,
                notes = "Saved for referral reachout.",
                nextRoundDate = null
            )
        )
    }

    // Job Alerts
    val jobAlerts: Flow<List<JobAlert>> = dao.getAllAlerts().map { list ->
        if (list.isEmpty()) {
            listOf(
                JobAlert(
                    id = "alert-1",
                    query = "HR Executive / Fresher",
                    location = "Noida & Delhi NCR",
                    experience = "0–2 years",
                    expectedSalary = "₹4–7 LPA",
                    frequency = "Daily Alert",
                    isActive = true,
                    matchedJobsCount = 8
                ),
                JobAlert(
                    id = "alert-2",
                    query = "HR Remote Internship",
                    location = "Remote",
                    experience = "0 years",
                    expectedSalary = "₹20k–35k/mo",
                    frequency = "Instant Alert",
                    isActive = true,
                    matchedJobsCount = 5
                )
            )
        } else {
            list.map {
                JobAlert(
                    id = it.id,
                    query = it.query,
                    location = it.location,
                    experience = it.experience,
                    expectedSalary = it.expectedSalary,
                    frequency = it.frequency,
                    isActive = it.isActive
                )
            }
        }
    }

    suspend fun addJobAlert(alert: JobAlert) {
        dao.insertAlert(
            JobAlertEntity(
                id = alert.id,
                query = alert.query,
                location = alert.location,
                experience = alert.experience,
                expectedSalary = alert.expectedSalary,
                frequency = alert.frequency,
                isActive = alert.isActive
            )
        )
    }

    suspend fun removeJobAlert(alertId: String) {
        dao.deleteAlert(alertId)
    }
}
