package com.example.data.model

enum class ApplicationStatus(val label: String, val colorHex: Long) {
    SAVED("Saved", 0xFF64748B),
    APPLIED("Applied", 0xFF3B82F6),
    ASSESSMENT("Assessment", 0xFF8B5CF6),
    INTERVIEW("Interview Scheduled", 0xFFF59E0B),
    HR_ROUND("HR Round", 0xFFEC4899),
    FINAL_ROUND("Final Round", 0xFF6366F1),
    OFFER("Offer Received", 0xFF10B981),
    REJECTED("Not Selected", 0xFFEF4444)
}

data class ApplicationItem(
    val id: String,
    val jobTitle: String,
    val company: String,
    val location: String,
    val salary: String,
    val platform: String,
    val dateApplied: String,
    val status: ApplicationStatus,
    val notes: String = "",
    val nextRoundDate: String? = null
)
