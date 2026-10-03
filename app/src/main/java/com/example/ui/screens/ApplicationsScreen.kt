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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ApplicationItem
import com.example.data.model.ApplicationStatus

@Composable
fun ApplicationsScreen(
    applications: List<ApplicationItem>,
    onAddApplication: (title: String, company: String, loc: String, salary: String, platform: String, status: ApplicationStatus) -> Unit,
    onUpdateStatus: (String, ApplicationStatus) -> Unit,
    onDeleteApplication: (String) -> Unit
) {
    var selectedFilterStatus by remember { mutableStateOf<ApplicationStatus?>(null) }
    var isAddModalOpen by remember { mutableStateOf(false) }

    val totalApplied = applications.count { it.status != ApplicationStatus.SAVED }
    val totalInterviews = applications.count {
        it.status == ApplicationStatus.INTERVIEW || it.status == ApplicationStatus.HR_ROUND || it.status == ApplicationStatus.FINAL_ROUND
    }
    val totalOffers = applications.count { it.status == ApplicationStatus.OFFER }
    val responseRate = if (totalApplied > 0) ((totalInterviews + totalOffers).toFloat() / totalApplied * 100).toInt() else 0

    val filteredList = if (selectedFilterStatus == null) {
        applications
    } else {
        applications.filter { it.status == selectedFilterStatus }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("applications_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Application Tracker", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Track job stages, interview rounds & offers", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = { isAddModalOpen = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_application_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Job")
                }
            }
        }

        // Stats Row (Applications, Interviews, Offers, Response Rate)
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                AppStatCard("Applied", "$totalApplied", Color(0xFF3B82F6), Modifier.weight(1f))
                AppStatCard("Interviews", "$totalInterviews", Color(0xFFF59E0B), Modifier.weight(1f))
                AppStatCard("Offers", "$totalOffers", Color(0xFF10B981), Modifier.weight(1f))
                AppStatCard("Response", "$responseRate%", Color(0xFF8B5CF6), Modifier.weight(1f))
            }
        }

        // Status Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedFilterStatus == null,
                        onClick = { selectedFilterStatus = null },
                        label = { Text("All (${applications.size})", fontSize = 11.sp) }
                    )
                }
                items(ApplicationStatus.values()) { status ->
                    val count = applications.count { it.status == status }
                    FilterChip(
                        selected = selectedFilterStatus == status,
                        onClick = { selectedFilterStatus = if (selectedFilterStatus == status) null else status },
                        label = { Text("${status.label} ($count)", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Applications List
        if (filteredList.isEmpty()) {
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
                        Icon(Icons.Default.AssignmentLate, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No applications in this category", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Track your applications by clicking 'Add Job' above.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(filteredList) { app ->
                ApplicationCard(
                    app = app,
                    onStatusChange = { newStatus -> onUpdateStatus(app.id, newStatus) },
                    onDelete = { onDeleteApplication(app.id) }
                )
            }
        }
    }

    // Add Application Dialog
    if (isAddModalOpen) {
        AddApplicationDialog(
            onDismiss = { isAddModalOpen = false },
            onSave = { title, comp, loc, sal, plat, stat ->
                onAddApplication(title, comp, loc, sal, plat, stat)
                isAddModalOpen = false
            }
        )
    }
}

@Composable
private fun ApplicationCard(
    app: ApplicationItem,
    onStatusChange: (ApplicationStatus) -> Unit,
    onDelete: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("app_card_${app.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(app.jobTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("${app.company} • ${app.location}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Applied on: ${app.dateApplied} via ${app.platform}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }

                // Status Badge & Dropdown Trigger
                Box {
                    Surface(
                        color = Color(app.status.colorHex).copy(alpha = 0.14f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { expandedMenu = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = app.status.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(app.status.colorHex)
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(app.status.colorHex), modifier = Modifier.size(16.dp))
                        }
                    }

                    DropdownMenu(expanded = expandedMenu, onDismissRequest = { expandedMenu = false }) {
                        ApplicationStatus.values().forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.label, fontSize = 12.sp) },
                                onClick = {
                                    onStatusChange(st)
                                    expandedMenu = false
                                }
                            )
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Delete Entry", color = MaterialTheme.colorScheme.error, fontSize = 12.sp) },
                            onClick = {
                                onDelete()
                                expandedMenu = false
                            }
                        )
                    }
                }
            }

            if (!app.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝 Notes: ${app.notes}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (!app.nextRoundDate.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Next Round: ${app.nextRoundDate}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                }
            }
        }
    }
}

@Composable
private fun AppStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AddApplicationDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, company: String, loc: String, salary: String, platform: String, status: ApplicationStatus) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Noida") }
    var platform by remember { mutableStateOf("LinkedIn") }
    var status by remember { mutableStateOf(ApplicationStatus.APPLIED) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("add_app_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Add New Application", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Job Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && company.isNotBlank()) {
                            onSave(title, company, location, "₹4.5–6 LPA", platform, status)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save to Tracker")
                }
            }
        }
    }
}
