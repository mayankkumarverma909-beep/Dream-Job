package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResumeScreen(
    resume: ResumeData,
    selectedTemplate: ResumeTemplate,
    tailoredJob: JobListing?,
    tailoredResumeText: String?,
    isTailoringLoading: Boolean,
    onTemplateSelect: (ResumeTemplate) -> Unit,
    onNavigateToAts: () -> Unit
) {
    val context = LocalContext.current
    var isTailorModalOpen by remember { mutableStateOf(tailoredJob != null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("resume_builder_screen")
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("AI Resume Builder", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Professional templates & ATS optimization", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Export Share Action
            FilledTonalButton(
                onClick = {
                    val exportText = buildString {
                        appendLine("${resume.fullName} | ${resume.title}")
                        appendLine("${resume.email} | ${resume.phone} | ${resume.location}")
                        appendLine("\nPROFESSIONAL SUMMARY:")
                        appendLine(resume.summary)
                        appendLine("\nEXPERIENCE:")
                        resume.experiences.forEach { exp ->
                            appendLine("${exp.role} - ${exp.company} (${exp.duration})")
                            exp.bullets.forEach { appendLine("• $it") }
                        }
                        appendLine("\nEDUCATION:")
                        resume.education.forEach { edu ->
                            appendLine("${edu.degree} - ${edu.institution} (${edu.period}) - ${edu.grade}")
                        }
                        appendLine("\nCORE SKILLS:")
                        appendLine(resume.skills.joinToString(", "))
                    }
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, exportText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Export CareerMate Resume"))
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("export_resume_btn")
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export PDF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Template Selector Tabs
        Text("Select Resume Template", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ResumeTemplate.values().forEach { template ->
                FilterChip(
                    selected = selectedTemplate == template,
                    onClick = { onTemplateSelect(template) },
                    label = {
                        Text(
                            text = when (template) {
                                ResumeTemplate.MODERN_TECH -> "Modern Tech"
                                ResumeTemplate.EXECUTIVE_CLEAN -> "Executive Clean"
                                ResumeTemplate.CREATIVE_MINIMAL -> "Creative Minimal"
                            },
                            fontSize = 11.sp
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AI Tailoring Alert Banner (if tailored for a specific job)
        if (tailoredJob != null || tailoredResumeText != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Resume Tailored for:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = tailoredJob?.company ?: "Target Job",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Keywords aligned with: ${tailoredJob?.title ?: "HR Executive"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    if (isTailoringLoading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating tailored bullet points...", fontSize = 12.sp)
                        }
                    } else if (tailoredResumeText != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = tailoredResumeText,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Live Resume Preview Document
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Resume Header
                Text(
                    text = resume.fullName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = resume.title,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${resume.email} • ${resume.phone} • ${resume.location}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Professional Summary
                ResumeSectionHeader("Professional Summary")
                Text(
                    text = resume.summary,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Experience / Internships
                ResumeSectionHeader("Experience & Internships")
                resume.experiences.forEach { exp ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(exp.role, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(exp.duration, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("${exp.company} • ${exp.location}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        exp.bullets.forEach { bullet ->
                            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                Text("• ", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Text(bullet, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Projects
                ResumeSectionHeader("Academic & Live Projects")
                resume.projects.forEach { proj ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(proj.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(proj.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                        Text("Tools: ${proj.tools}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Education
                ResumeSectionHeader("Education")
                resume.education.forEach { edu ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(edu.degree, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(edu.institution, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(edu.period, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(edu.grade, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Skills
                ResumeSectionHeader("Core Competencies & Tools")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    resume.skills.forEach { skill ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = skill,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick ATS Test CTA
        Button(
            onClick = onNavigateToAts,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("check_resume_ats_btn")
        ) {
            Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Run ATS Resume Compatibility Scan")
        }
    }
}

@Composable
private fun ResumeSectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
        HorizontalDivider(modifier = Modifier.padding(top = 2.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    }
}
