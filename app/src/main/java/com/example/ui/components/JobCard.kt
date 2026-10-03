package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JobListing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobCard(
    job: JobListing,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onViewDetails: () -> Unit,
    onTailorResume: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Platform Colors
    val platformColor = when (job.sourcePlatform.lowercase()) {
        "naukri" -> Color(0xFF1E40AF)
        "linkedin" -> Color(0xFF0284C7)
        "internshala" -> Color(0xFF059669)
        "indeed" -> Color(0xFF2563EB)
        "glassdoor" -> Color(0xFF16A34A)
        "foundit" -> Color(0xFF7C3AED)
        else -> Color(0xFF4F46E5)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("job_card_${job.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Platform Badge + Demo tag + Save & Share Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Source Platform Badge
                    Surface(
                        color = platformColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Source: ${job.sourcePlatform}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = platformColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    if (job.isDemoData) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Demo Data",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Check out this job opportunity: ${job.title} at ${job.company} (${job.salary}) - ${job.applyUrl}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Job Opening"))
                        },
                        modifier = Modifier.size(36.dp).testTag("share_job_${job.id}")
                    ) {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = "Share Job",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier.size(36.dp).testTag("save_job_${job.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isSaved) "Remove Saved" else "Save Job",
                            modifier = Modifier.size(20.dp),
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Match Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = job.company,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // AI Match Chip / Radial
                Surface(
                    color = if (job.aiMatchPercent >= 90) Color(0xFFDCFCE7) else Color(0xFFE0E7FF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable { onViewDetails() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            tint = if (job.aiMatchPercent >= 90) Color(0xFF15803D) else Color(0xFF4338CA),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${job.aiMatchPercent}% Match",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (job.aiMatchPercent >= 90) Color(0xFF15803D) else Color(0xFF4338CA)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Chips (Location, WorkMode, Exp, Salary)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MetaPill(icon = Icons.Default.LocationOn, text = job.location)
                MetaPill(icon = Icons.Default.WorkOutline, text = "${job.jobType} • ${job.workMode}")
                MetaPill(icon = Icons.Default.Payments, text = job.salary)
                MetaPill(icon = Icons.Default.Schedule, text = "Exp: ${job.experience}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Skills Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                job.skills.take(5).forEach { skill ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = skill,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Apply Now (Direct external platform redirect with disclaimer)
                Button(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Redirecting to official ${job.sourcePlatform} application page...",
                            Toast.LENGTH_SHORT
                        ).show()
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(job.applyUrl))
                        context.startActivity(browserIntent)
                    },
                    modifier = Modifier.weight(1f).testTag("apply_now_${job.id}"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Apply Now", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                // View Details
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f).testTag("view_details_${job.id}"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Match Details", fontSize = 13.sp)
                }

                // Tailor Resume Button
                FilledTonalIconButton(
                    onClick = onTailorResume,
                    modifier = Modifier.size(44.dp).testTag("tailor_btn_${job.id}"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.AutoFixHigh,
                        contentDescription = "Tailor Resume For Job",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetaPill(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}
