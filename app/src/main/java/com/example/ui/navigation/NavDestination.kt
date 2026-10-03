package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val showInBottomBar: Boolean = true
) {
    DASHBOARD("dashboard", "Home", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, true),
    JOBS("jobs", "Find Jobs", Icons.Filled.Work, Icons.Outlined.WorkOutline, true),
    INTERVIEW("interview", "AI Interview", Icons.Filled.Videocam, Icons.Outlined.Videocam, true),
    RESUME("resume", "Resume", Icons.Filled.Description, Icons.Outlined.Description, true),
    ATS_CHECKER("ats_checker", "ATS Checker", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, false),
    SKILL_GAP("skill_gap", "Skill Gap", Icons.Filled.Insights, Icons.Outlined.Insights, false),
    APPLICATIONS("applications", "Applications", Icons.Filled.AssignmentTurnedIn, Icons.Outlined.Assignment, true),
    PROFILE("profile", "Profile", Icons.Filled.Person, Icons.Outlined.PersonOutline, false),
    INTERVIEW_COACH("interview_coach", "AI Coach", Icons.Filled.Psychology, Icons.Outlined.Psychology, false)
}
