package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JobListing
import com.example.ui.components.JobCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    jobs: List<JobListing>,
    savedJobs: List<JobListing>,
    searchQuery: String,
    selectedPlatform: String,
    selectedWorkMode: String,
    selectedJobType: String,
    activeNaturalSummary: String?,
    onQueryChange: (String) -> Unit,
    onPlatformChange: (String) -> Unit,
    onWorkModeChange: (String) -> Unit,
    onJobTypeChange: (String) -> Unit,
    onResetFilters: () -> Unit,
    onCreateAlertClick: () -> Unit,
    onJobDetails: (JobListing) -> Unit,
    onTailorResume: (JobListing) -> Unit,
    onToggleSaveJob: (JobListing, Boolean) -> Unit
) {
    val platforms = listOf("All", "Naukri", "LinkedIn", "Indeed", "Internshala", "Glassdoor", "Foundit", "Company Career")
    val workModes = listOf("All", "Remote", "Hybrid", "On-site")
    val jobTypes = listOf("All", "Full-time", "Internship")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("jobs_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Header & Natural Language Input
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Unified Job Aggregator",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Search across Naukri, LinkedIn, Indeed & Internshala",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = onCreateAlertClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("create_alert_btn")
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create Alert", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Box with Natural Language indicator
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = {
                        Text("Try: 'Fresher HR job in Noida above 5 LPA' or 'Remote HR internship'", fontSize = 12.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = onResetFilters) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("job_search_input")
                )

                // Parsed natural filter badge
                if (activeNaturalSummary != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Parsed Search: $activeNaturalSummary",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1D4ED8)
                            )
                        }
                    }
                }
            }
        }

        // Platform Filter Chips
        item {
            Column {
                Text("Source Platform", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(platforms) { platform ->
                        FilterChip(
                            selected = selectedPlatform == platform,
                            onClick = { onPlatformChange(platform) },
                            label = { Text(platform, fontSize = 11.sp) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("platform_filter_$platform")
                        )
                    }
                }
            }
        }

        // Work Mode & Job Type Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Work Mode", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(workModes) { mode ->
                            FilterChip(
                                selected = selectedWorkMode == mode,
                                onClick = { onWorkModeChange(mode) },
                                label = { Text(mode, fontSize = 10.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Job Type", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(jobTypes) { type ->
                            FilterChip(
                                selected = selectedJobType == type,
                                onClick = { onJobTypeChange(type) },
                                label = { Text(type, fontSize = 10.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Result count & Disclaimer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${jobs.size} Opportunities Found",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (selectedPlatform != "All" || selectedWorkMode != "All" || searchQuery.isNotEmpty()) {
                    Text(
                        text = "Reset All",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable { onResetFilters() }
                    )
                }
            }
        }

        // Jobs Listing
        if (jobs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    ) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No matching jobs found", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Try clearing specific filters or searching with broader keywords like 'HR' or 'Talent'.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onResetFilters) {
                            Text("Reset Filters")
                        }
                    }
                }
            }
        } else {
            items(jobs) { job ->
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

        // Legal & Source Notice
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🔒 Compliance Notice: CareerMate AI respects platform terms of service. Job openings are aggregated via official integration points and permitted public feeds. Clicking 'Apply Now' directs candidates straight to the original employer/platform website.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
