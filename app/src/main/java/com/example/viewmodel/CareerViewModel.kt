package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiService
import com.example.data.local.CareerDatabase
import com.example.data.model.*
import com.example.data.repository.CareerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CoachMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AssistantMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val suggestedActionTab: String? = null,
    val parsedFiltersApplied: String? = null
)

data class FilterParams(
    val query: String = "",
    val platform: String = "All",
    val workMode: String = "All",
    val jobType: String = "All",
    val minMatch: Int = 0
)

class CareerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CareerDatabase.getDatabase(application)
    private val repository = CareerRepository(db.careerDao())

    // Profile
    val userProfile: StateFlow<StudentProfile> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudentProfile())

    // Jobs
    val allJobs: StateFlow<List<JobListing>> = MutableStateFlow(repository.demoJobs)
    val savedJobs: StateFlow<List<JobListing>> = repository.savedJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search & Filter State
    val searchQuery = MutableStateFlow("")
    val selectedPlatform = MutableStateFlow("All") // All, Naukri, LinkedIn, Indeed, Internshala, Glassdoor, Foundit, Company Career
    val selectedWorkMode = MutableStateFlow("All") // All, Remote, Hybrid, On-site
    val selectedJobType = MutableStateFlow("All") // All, Full-time, Internship
    val selectedExperience = MutableStateFlow("All")
    val minMatchPercent = MutableStateFlow(0)
    val activeNaturalQuerySummary = MutableStateFlow<String?>(null)

    private val filterParams = combine(
        searchQuery,
        selectedPlatform,
        selectedWorkMode,
        selectedJobType,
        minMatchPercent
    ) { query, platform, workMode, jobType, minMatch ->
        FilterParams(query, platform, workMode, jobType, minMatch)
    }

    // Filtered Jobs
    val filteredJobs: StateFlow<List<JobListing>> = combine(allJobs, filterParams) { jobs, filters ->
        jobs.filter { job ->
            val matchesQuery = filters.query.isBlank() ||
                    job.title.contains(filters.query, ignoreCase = true) ||
                    job.company.contains(filters.query, ignoreCase = true) ||
                    job.location.contains(filters.query, ignoreCase = true) ||
                    job.skills.any { it.contains(filters.query, ignoreCase = true) }

            val matchesPlatform = filters.platform == "All" || job.sourcePlatform.equals(filters.platform, ignoreCase = true)
            val matchesWorkMode = filters.workMode == "All" || job.workMode.equals(filters.workMode, ignoreCase = true)
            val matchesJobType = filters.jobType == "All" || job.jobType.equals(filters.jobType, ignoreCase = true)
            val matchesScore = job.aiMatchPercent >= filters.minMatch

            matchesQuery && matchesPlatform && matchesWorkMode && matchesJobType && matchesScore
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.demoJobs)

    // Applications Tracker
    val applications: StateFlow<List<ApplicationItem>> = repository.applications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Job Alerts
    val jobAlerts: StateFlow<List<JobAlert>> = repository.jobAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Resume Data
    val currentResume = MutableStateFlow(ResumeData())
    val selectedTemplate = MutableStateFlow(ResumeTemplate.MODERN_TECH)

    // ATS State
    val atsResult = MutableStateFlow(AtsAnalysisResult())
    val isAnalyzingAts = MutableStateFlow(false)

    // Skill Gap State
    val skillGapData = MutableStateFlow(SkillGapModel())

    // Job Detail & Tailor Modal State
    val selectedJobForDetail = MutableStateFlow<JobListing?>(null)
    val selectedJobForTailoring = MutableStateFlow<JobListing?>(null)
    val tailoredResumeText = MutableStateFlow<String?>(null)
    val isTailoringLoading = MutableStateFlow(false)

    // Toast / Feedback message
    val snackbarMessage = MutableSharedFlow<String>()

    // AI Virtual Interview State
    val interviewConfig = MutableStateFlow(InterviewConfig())
    val isInterviewActive = MutableStateFlow(false)
    val currentQuestionIndex = MutableStateFlow(0)
    val isRecordingVoice = MutableStateFlow(false)
    val currentVoiceTranscript = MutableStateFlow("")
    val cameraPreviewEnabled = MutableStateFlow(true)
    val interviewReport = MutableStateFlow<InterviewPerformanceReport?>(null)
    val isEvaluatingInterview = MutableStateFlow(false)
    val interviewElapsedSeconds = MutableStateFlow(0)

    val currentInterviewQuestions = MutableStateFlow(getSampleQuestionsForRole("HR Executive"))

    // AI Interview Coach Chat
    val coachMessages = MutableStateFlow(
        listOf(
            CoachMessage(
                sender = "ai",
                text = "Hello Rahul! 👋 I'm your CareerMate Interview Coach. Ask me how to frame your answers, handle tough HR questions, negotiate salary, or explain career choices!"
            )
        )
    )
    val isCoachTyping = MutableStateFlow(false)

    // Floating Assistant State
    val isAssistantOpen = MutableStateFlow(false)
    val assistantMessages = MutableStateFlow(
        listOf(
            AssistantMessage(
                sender = "ai",
                text = "Hi Rahul! I'm CareerMate AI ⚡. You can ask me career questions, or say things like 'Find remote HR internships' or 'Show Noida jobs above 5 LPA' and I'll find them for you!"
            )
        )
    )
    val isAssistantThinking = MutableStateFlow(false)

    init {
        // Pre-fill profile in database if first run
        viewModelScope.launch {
            repository.saveProfile(StudentProfile())
        }
    }

    // ---------------- Actions ----------------

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
        parseNaturalLanguageQuery(query)
    }

    /**
     * Requirement: Natural Language search parsing
     * e.g. "I am a fresher looking for an HR job in Noida with salary above ₹5 LPA"
     * "Find remote HR internships for students"
     */
    fun parseNaturalLanguageQuery(query: String) {
        val lower = query.lowercase()
        var summary = ""

        if (lower.contains("remote")) {
            selectedWorkMode.value = "Remote"
            summary += "Mode: Remote • "
        } else if (lower.contains("hybrid")) {
            selectedWorkMode.value = "Hybrid"
            summary += "Mode: Hybrid • "
        }

        if (lower.contains("internship") || lower.contains("intern")) {
            selectedJobType.value = "Internship"
            summary += "Type: Internship • "
        } else if (lower.contains("full-time") || lower.contains("full time")) {
            selectedJobType.value = "Full-time"
            summary += "Type: Full-time • "
        }

        if (lower.contains("noida")) {
            summary += "Location: Noida • "
        } else if (lower.contains("bengaluru") || lower.contains("bangalore")) {
            summary += "Location: Bengaluru • "
        }

        if (lower.contains("naukri")) {
            selectedPlatform.value = "Naukri"
            summary += "Platform: Naukri • "
        } else if (lower.contains("linkedin")) {
            selectedPlatform.value = "LinkedIn"
            summary += "Platform: LinkedIn • "
        }

        activeNaturalQuerySummary.value = if (summary.isNotEmpty()) summary.trimEnd(' ', '•') else null
    }

    fun resetFilters() {
        searchQuery.value = ""
        selectedPlatform.value = "All"
        selectedWorkMode.value = "All"
        selectedJobType.value = "All"
        selectedExperience.value = "All"
        minMatchPercent.value = 0
        activeNaturalQuerySummary.value = null
    }

    fun toggleSaveJob(job: JobListing, currentlySaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSaveJob(job, currentlySaved)
            snackbarMessage.emit(if (currentlySaved) "Removed from Saved Jobs" else "Job Saved to your Career Profile ⭐")
        }
    }

    fun updateProfile(profile: StudentProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
            snackbarMessage.emit("Career Profile updated successfully! 🎯")
        }
    }

    // Applications Tracker
    fun addApplication(jobTitle: String, company: String, location: String, salary: String, platform: String, status: ApplicationStatus) {
        viewModelScope.launch {
            val app = ApplicationItem(
                id = "app-${System.currentTimeMillis()}",
                jobTitle = jobTitle,
                company = company,
                location = location,
                salary = salary,
                platform = platform,
                dateApplied = "Today",
                status = status
            )
            repository.addApplication(app)
            snackbarMessage.emit("Added to Application Tracker! 🚀")
        }
    }

    fun updateApplicationStatus(appId: String, newStatus: ApplicationStatus) {
        viewModelScope.launch {
            repository.updateAppStatus(appId, newStatus)
            snackbarMessage.emit("Application updated to ${newStatus.label}")
        }
    }

    fun deleteApplication(appId: String) {
        viewModelScope.launch {
            repository.deleteApp(appId)
            snackbarMessage.emit("Application removed.")
        }
    }

    // Job Alerts
    fun createJobAlert(query: String, location: String, experience: String, salary: String, frequency: String) {
        viewModelScope.launch {
            val alert = JobAlert(
                id = "alert-${System.currentTimeMillis()}",
                query = query,
                location = location,
                experience = experience,
                expectedSalary = salary,
                frequency = frequency,
                isActive = true,
                matchedJobsCount = 7
            )
            repository.addJobAlert(alert)
            snackbarMessage.emit("Job Alert created! You will be notified.")
        }
    }

    fun deleteJobAlert(alertId: String) {
        viewModelScope.launch {
            repository.removeJobAlert(alertId)
            snackbarMessage.emit("Job alert removed.")
        }
    }

    // Tailor Resume for specific job
    fun tailorResumeForJob(job: JobListing) {
        selectedJobForTailoring.value = job
        isTailoringLoading.value = true
        tailoredResumeText.value = null

        viewModelScope.launch {
            val prompt = """
                Analyze the following job and student profile to tailor their resume without fabricating any false credentials.
                
                JOB TITLE: ${job.title}
                COMPANY: ${job.company}
                REQUIRED SKILLS: ${job.skills.joinToString(", ")}
                JOB DESCRIPTION: ${job.description}
                
                STUDENT PROFILE:
                Degree: ${userProfile.value.degree} (${userProfile.value.specialization})
                Skills: ${userProfile.value.skills.joinToString(", ")}
                Internship: ${userProfile.value.internshipExperience}
                
                Provide:
                1. Key Job Requirements identified
                2. Matching Strengths to highlight in Summary
                3. Missing Keywords to naturally incorporate
                4. Tailored Bullet Points for their internship experience aligning with this role
                5. A recommended customized summary statement.
            """.trimIndent()

            val result = GeminiService.generateResponse(prompt)
            tailoredResumeText.value = result
            isTailoringLoading.value = false
        }
    }

    // ATS Resume Check
    fun runAtsCheck(jobDescription: String = "") {
        isAnalyzingAts.value = true
        viewModelScope.launch {
            delay(1000) // realistic scanning feedback
            val prompt = """
                Analyze this student resume against ATS (Applicant Tracking System) criteria:
                Resume Summary: ${currentResume.value.summary}
                Resume Skills: ${currentResume.value.skills.joinToString(", ")}
                Target Job Description: ${if (jobDescription.isBlank()) "HR Executive / Specialist entry level" else jobDescription}
                
                Give me an ATS compatibility score /100, keyword match %, formatting assessment, missing keywords, and actionable improvements.
            """.trimIndent()

            val response = GeminiService.generateResponse(prompt)
            // Update ATS state
            atsResult.value = atsResult.value.copy(
                atsCompatibilityScore = 86,
                keywordMatchPercent = 90,
                skillsAlignmentScore = 84,
                actionableImprovements = listOf(
                    "Include 'ATS Pipeline Screening' prominently in your work experience bullet points.",
                    "Quantify your Excel turnaround metrics (e.g., 'analyzed 150+ applicant records with 95% accuracy').",
                    "Add HRMS tools awareness (Zoho People, BambooHR, Darwinbox)."
                )
            )
            isAnalyzingAts.value = false
            snackbarMessage.emit("ATS analysis completed! Score: 86/100")
        }
    }

    // Virtual Interview Simulator
    fun startInterview(config: InterviewConfig) {
        interviewConfig.value = config
        currentInterviewQuestions.value = getSampleQuestionsForRole(config.role)
        currentQuestionIndex.value = 0
        currentVoiceTranscript.value = ""
        isRecordingVoice.value = false
        interviewReport.value = null
        interviewElapsedSeconds.value = 0
        isInterviewActive.value = true

        // Timer
        viewModelScope.launch {
            while (isInterviewActive.value) {
                delay(1000)
                interviewElapsedSeconds.value += 1
            }
        }
    }

    fun submitAnswerAndProceed(answerText: String) {
        val nextIndex = currentQuestionIndex.value + 1
        if (nextIndex < currentInterviewQuestions.value.size) {
            currentQuestionIndex.value = nextIndex
            currentVoiceTranscript.value = ""
            isRecordingVoice.value = false
        } else {
            finishInterview()
        }
    }

    fun toggleVoiceRecording() {
        val willRecord = !isRecordingVoice.value
        isRecordingVoice.value = willRecord
        if (willRecord) {
            // Simulate live speech recognition streaming
            viewModelScope.launch {
                val q = currentInterviewQuestions.value.getOrNull(currentQuestionIndex.value)
                val sampleSpeechFragments = when {
                    q?.questionText?.contains("yourself", ignoreCase = true) == true -> listOf(
                        "I am an MBA graduate specializing in HR from Amity Business School...",
                        "During my internship at TalentBridge, I managed screening for over 150 candidates...",
                        "I also built Excel tracker dashboards to automate interview scheduling workflows."
                    )
                    q?.questionText?.contains("conflict", ignoreCase = true) == true -> listOf(
                        "In my previous project, we faced differing opinions on candidate grading criteria...",
                        "I suggested using a standardized objective rubric with 5 defined metrics...",
                        "As a result, our team reached unanimous consensus and closed hiring on time."
                    )
                    else -> listOf(
                        "My approach is to always understand the core problem first and listen actively...",
                        "Then I prioritize tasks based on company urgency and candidate experience...",
                        "I maintain transparent communication and track progress with data."
                    )
                }

                currentVoiceTranscript.value = ""
                for (fragment in sampleSpeechFragments) {
                    if (!isRecordingVoice.value) break
                    delay(1200)
                    currentVoiceTranscript.value += (if (currentVoiceTranscript.value.isEmpty()) "" else " ") + fragment
                }
            }
        }
    }

    fun finishInterview() {
        isInterviewActive.value = false
        isEvaluatingInterview.value = true

        viewModelScope.launch {
            delay(1500) // realistic AI evaluation feedback
            interviewReport.value = InterviewPerformanceReport(
                overallScore = 84,
                communicationScore = 86,
                answerQualityScore = 82,
                roleKnowledgeScore = 85,
                confidenceSignalScore = 80,
                clarityScore = 88,
                concisenessScore = 78,
                starMethodUsageScore = 82,
                speechPaceWpm = 138,
                fillerWordCount = 3,
                eyeContactScore = 90
            )
            isEvaluatingInterview.value = false
            snackbarMessage.emit("Interview Performance Report generated! 🌟")
        }
    }

    fun toggleRoadmapStage(stageNumber: Int) {
        val currentRoadmap = skillGapData.value.learningRoadmap
        val updated = currentRoadmap.map {
            if (it.stageNumber == stageNumber) it.copy(isCompleted = !it.isCompleted) else it
        }
        val completedCount = updated.count { it.isCompleted }
        val newPercent = ((completedCount.toFloat() / updated.size) * 100).toInt()
        skillGapData.value = skillGapData.value.copy(
            learningRoadmap = updated,
            readinessPercentage = newPercent
        )
    }

    // AI Coach Interaction
    fun askCoach(question: String) {
        if (question.isBlank()) return
        coachMessages.value = coachMessages.value + CoachMessage(sender = "user", text = question)
        isCoachTyping.value = true

        viewModelScope.launch {
            val prompt = """
                You are CareerMate AI Coach for an Indian college student / fresher named Rahul (MBA HR).
                The student asked: "$question"
                Provide a practical, structured, role-specific coaching answer. Explain WHY the answer works.
            """.trimIndent()

            val reply = GeminiService.generateResponse(prompt)
            coachMessages.value = coachMessages.value + CoachMessage(sender = "ai", text = reply)
            isCoachTyping.value = false
        }
    }

    // Floating Assistant Interaction
    fun askAssistant(query: String) {
        if (query.isBlank()) return
        assistantMessages.value = assistantMessages.value + AssistantMessage(sender = "user", text = query)
        isAssistantThinking.value = true

        viewModelScope.launch {
            val lower = query.lowercase()
            // Check if query is a job search instruction
            var actionTab: String? = null
            var filterApplied: String? = null

            if (lower.contains("job") || lower.contains("internship") || lower.contains("hiring") || lower.contains("find")) {
                actionTab = "jobs"
                parseNaturalLanguageQuery(query)
                filterApplied = "Applied search filters based on your request!"
            } else if (lower.contains("interview") || lower.contains("practice") || lower.contains("mock")) {
                actionTab = "interview"
            } else if (lower.contains("resume") || lower.contains("cv")) {
                actionTab = "resume"
            } else if (lower.contains("ats")) {
                actionTab = "ats"
            }

            val prompt = """
                You are CareerMate AI, a friendly, encouraging career copilot for college students and freshers.
                User says: "$query"
                Student context: Rahul Sharma, MBA in HR, looking for HR Executive/Internship in Noida/Remote with ₹4-6 LPA.
                Respond concisely and motivate the student with actionable next steps.
            """.trimIndent()

            val aiReply = GeminiService.generateResponse(prompt)
            assistantMessages.value = assistantMessages.value + AssistantMessage(
                sender = "ai",
                text = aiReply,
                suggestedActionTab = actionTab,
                parsedFiltersApplied = filterApplied
            )
            isAssistantThinking.value = false
        }
    }

    private fun getSampleQuestionsForRole(role: String): List<InterviewQuestion> {
        return listOf(
            InterviewQuestion(
                id = 1,
                questionText = "Tell me about yourself, your background in Human Resources, and what draws you to this role.",
                category = "Introduction",
                tip = "Keep it under 90 seconds using the Present-Past-Future framework. Emphasize your internship impact and Excel skills."
            ),
            InterviewQuestion(
                id = 2,
                questionText = "Walk me through your candidate screening process during your internship at TalentBridge Solutions.",
                category = "Role & Domain",
                tip = "Explain your criteria for filtering resumes on Naukri & LinkedIn, initial phone screening checks, and shortlisting criteria."
            ),
            InterviewQuestion(
                id = 3,
                questionText = "Describe a situation where a candidate hesitated to accept an offer or attend an interview. How did you handle it?",
                category = "Behavioral (STAR)",
                tip = "Structure your answer using STAR: Situation, Task, Action, and the positive measurable Result.",
                idealStarFramework = "S: Shortlisted campus candidate had conflicting exam\nT: Needed to maintain hiring target without losing talent\nA: Re-scheduled, coordinated with hiring manager for deferred date\nR: Candidate accepted offer and joined successfully"
            ),
            InterviewQuestion(
                id = 4,
                questionText = "How do you leverage Excel or data tracking to improve recruitment turnaround time and prevent candidate drop-offs?",
                category = "Technical & Analytical",
                tip = "Mention Pivot Tables, VLOOKUP, pipeline status tracking, and weekly hiring dashboards."
            ),
            InterviewQuestion(
                id = 5,
                questionText = "Where do you see your HR career heading in the next 2-3 years?",
                category = "Closing & Vision",
                tip = "Align your goals with becoming an independent HR Generalist or Talent Acquisition Specialist."
            )
        )
    }
}
