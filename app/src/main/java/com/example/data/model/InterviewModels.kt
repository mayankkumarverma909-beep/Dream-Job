package com.example.data.model

data class InterviewConfig(
    val role: String = "HR Executive",
    val industry: String = "IT & Tech Services",
    val experienceLevel: String = "Fresher (0-1 yrs)",
    val interviewType: InterviewType = InterviewType.HR_INTERVIEW
)

enum class InterviewType(val displayName: String) {
    HR_INTERVIEW("HR Screening Round"),
    TECHNICAL_INTERVIEW("Role & Domain Round"),
    BEHAVIORAL_INTERVIEW("Behavioral (STAR Method)"),
    MANAGERIAL_INTERVIEW("Managerial & Fitment"),
    CAMPUS_MOCK("Campus Placement Mock")
}

data class InterviewQuestion(
    val id: Int,
    val questionText: String,
    val category: String, // "Introduction", "Technical", "Behavioral", "Situational", "Closing"
    val tip: String,
    val idealStarFramework: String? = null
)

data class InterviewTurn(
    val question: InterviewQuestion,
    val studentResponse: String = "",
    val feedback: TurnFeedback? = null
)

data class TurnFeedback(
    val clarityScore: Int = 85,
    val relevanceScore: Int = 90,
    val confidenceIndicator: String = "High",
    val fillerWordsDetected: List<String> = emptyList(),
    val quickTip: String = ""
)

data class InterviewPerformanceReport(
    val overallScore: Int = 82,
    val communicationScore: Int = 84,
    val answerQualityScore: Int = 80,
    val roleKnowledgeScore: Int = 85,
    val confidenceSignalScore: Int = 78,
    val clarityScore: Int = 86,
    val concisenessScore: Int = 75,
    val starMethodUsageScore: Int = 80,
    val speechPaceWpm: Int = 135, // Words per minute
    val fillerWordCount: Int = 4, // e.g. "um", "like", "actually"
    val eyeContactScore: Int = 88,
    val postureIndicator: String = "Upright & Attentive",
    val whatYouDidWell: List<String> = listOf(
        "Strong professional opening with clear articulation of your HR internship experience.",
        "Demonstrated solid familiarity with recruitment lifecycle and Excel reporting.",
        "Good vocal modulation and professional terminology."
    ),
    val whatToImprove: List<String> = listOf(
        "Structure behavioral answers more strictly using Situation-Task-Action-Result (STAR).",
        "Minimize filler words like 'actually' and 'you know' when pausing to think.",
        "Quantify your accomplishments more boldly (e.g. mention exact candidate turnaround times)."
    ),
    val betterAnswerExample: BetterAnswerExample = BetterAnswerExample(
        question = "Tell me about a time you handled a difficult situation with a candidate.",
        sampleAnswer = "During our campus drive at TalentBridge (Situation), a shortlisted candidate declined the initial offer citing conflicting exam schedules (Task). I scheduled a quick 1-on-1 video call, listened actively to understand their exam timeline, and collaborated with the hiring manager to offer a flexible 2-week deferred start date (Action). As a result, the candidate accepted the offer, maintaining our 95% drive closure rate (Result)."
    ),
    val questionsToPracticeAgain: List<String> = listOf(
        "Why should we hire you over other candidates?",
        "Describe how you handle conflicting priorities in a fast-paced environment."
    ),
    val recommendedLearningResources: List<String> = listOf(
        "STAR Interview Mastery Guide (CareerMate Library)",
        "Handling Tough Behavioral Questions in HR",
        "Body Language & Executive Presence for Video Interviews"
    )
)

data class BetterAnswerExample(
    val question: String,
    val sampleAnswer: String
)
