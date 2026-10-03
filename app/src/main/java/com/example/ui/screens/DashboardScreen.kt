package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JobListing
import com.example.data.model.StudentProfile
import com.example.ui.components.JobCard
import com.example.ui.components.RadialScoreIndicator

@Composable
fun DashboardScreen(
    profile: StudentProfile,
    jobs: List<JobListing>,
    savedJobs: List<JobListing>,
    atsScore: Int,
    onNavigateToJobs: () -> Unit,
    onNavigateToInterview: () -> Unit,
    onNavigateToResume: () -> Unit,
    onNavigateToAts: () -> Unit,
    onNavigateToSkillGap: () -> Unit,
    onNavigateToApplications: () -> Unit,
    onJobDetails: (JobListing) -> Unit,
    onTailorResume: (JobListing) -> Unit,
    onToggleSaveJob: (JobListing, Boolean) -> Unit
) {
    val completeness = profile.calculateCompleteness()

    // Daily tasks state
    var task1Done by remember { mutableStateOf(false) }
    var task2Done by remember { mutableStateOf(true) }
    var task3Done by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Motivating Greeting & Headline Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF312E81), Color(0xFF4F46E5), Color(0xFF0284C7))
                            ),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Good Morning, ${profile.fullName.split(" ").firstOrNull() ?: "Student"} 👋",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${profile.degree} • ${profile.specialization}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE0E7FF)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Fresher Batch '26", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "“Your AI Career Partner — Find. Prepare. Get Hired.”",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Discover jobs, optimize your resume, practice interviews and build the skills your dream job demands — all in one place.",
                            fontSize = 12.sp,
                            color = Color(0xFFE0E7FF),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // CTAs
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = onNavigateToJobs,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF312E81)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("cta_find_my_dream_job")
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Find Dream Job", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            FilledTonalButton(
                                onClick = onNavigateToInterview,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF1E1B4B).copy(alpha = 0.6f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("cta_practice_interview")
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Practice Interview", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Career Progress Overview Cards (Profile Completion, ATS, Interview, Skill)
        item {
            Text(
                text = "Your Career Progress",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Completion
                ProgressMetricCard(
                    title = "Profile Complete",
                    value = "$completeness%",
                    subtitle = "78% Complete",
                    color = Color(0xFF4F46E5),
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToResume
                )
                // Resume ATS Score
                ProgressMetricCard(
                    title = "Resume ATS",
                    value = "$atsScore/100",
                    subtitle = "Keyword match 88%",
                    color = Color(0xFF059669),
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAts
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Interview Readiness
                ProgressMetricCard(
                    title = "Interview Prep",
                    value = "74%",
                    subtitle = "HR Round Ready",
                    color = Color(0xFFD97706),
                    icon = Icons.Default.Psychology,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToInterview
                )
                // Skill Readiness
                ProgressMetricCard(
                    title = "Skill Gap",
                    value = "68%",
                    subtitle = "3 Skills to learn",
                    color = Color(0xFF7C3AED),
                    icon = Icons.Default.Insights,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSkillGap
                )
            }
        }

        // Today's Career Tasks Checklist
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Today, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Today's Career Tasks", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Text(
                            text = "${listOf(task1Done, task2Done, task3Done).count { it }}/3 Done",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TaskItemRow(
                        title = "Apply to 3 matching HR Executive jobs in Noida",
                        isDone = task1Done,
                        onToggle = { task1Done = !task1Done }
                    )
                    TaskItemRow(
                        title = "Practice 5 behavioral STAR questions with AI Interviewer",
                        isDone = task2Done,
                        onToggle = { task2Done = !task2Done }
                    )
                    TaskItemRow(
                        title = "Complete Advanced Excel Pivot Tables module",
                        isDone = task3Done,
                        onToggle = { task3Done = !task3Done }
                    )
                }
            }
        }

        // Continue Your Preparation - Quick Actions
        item {
            Text(
                text = "Continue Your Preparation",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    PrepActionCard(
                        title = "Mock Interview",
                        description = "Practice with AI Avatar",
                        icon = Icons.Default.Videocam,
                        tint = Color(0xFF4F46E5),
                        onClick = onNavigateToInterview
                    )
                }
                item {
                    PrepActionCard(
                        title = "ATS Resume Scan",
                        description = "Check ATS 84/100 Compatibility",
                        icon = Icons.Default.Speed,
                        tint = Color(0xFF059669),
                        onClick = onNavigateToAts
                    )
                }
                item {
                    PrepActionCard(
                        title = "Skill Gap Analyzer",
                        description = "What Am I Missing?",
                        icon = Icons.Default.TrendingUp,
                        tint = Color(0xFF7C3AED),
                        onClick = onNavigateToSkillGap
                    )
                }
                item {
                    PrepActionCard(
                        title = "Application Tracker",
                        description = "View 4 Active Applications",
                        icon = Icons.Default.AssignmentTurnedIn,
                        tint = Color(0xFF0284C7),
                        onClick = onNavigateToApplications
                    )
                }
            }
        }

        // Recommended For You Jobs Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Recommended For You",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Aggregated from Naukri, LinkedIn & Indeed",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = onNavigateToJobs) {
                    Text("View All (${jobs.size})", fontSize = 12.sp)
                }
            }
        }

        // Top 3 Recommended Job Cards
        items(jobs.take(3)) { job ->
            val isSaved = savedJobs.any { it.id == job.id }
            JobCard(
                job = job,
                isSaved = isSaved,
                onSaveToggle = { onToggleSaveJob(job, isSaved) },
                onViewDetails = { onJobDetails(job) },
                onTailorResume = { onTailorResume(job) }
            )
        }
    }
}

@Composable
private fun ProgressMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f))
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(15.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TaskItemRow(title: String, isDone: Boolean, onToggle: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 6.dp)
    ) {
        Checkbox(checked = isDone, onCheckedChange = { onToggle() })
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PrepActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint.copy(alpha = 0.12f))
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
