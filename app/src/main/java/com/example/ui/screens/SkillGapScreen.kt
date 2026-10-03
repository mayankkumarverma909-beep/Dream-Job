package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SkillGapModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillGapScreen(
    skillGap: SkillGapModel,
    onToggleStage: (Int) -> Unit,
    onNavigateToInterview: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("skill_gap_screen")
    ) {
        // Header
        Text("What Am I Missing?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Compare your skills against target roles & follow your roadmap", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(14.dp))

        // Target Role Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Target Career Role", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(skillGap.targetRole, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${skillGap.readinessPercentage}% Ready",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // You Have
                Text("Skills You Have (${skillGap.userSkills.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF15803D))
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    skillGap.userSkills.forEach { skill ->
                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(skill, fontSize = 11.sp, color = Color(0xFF15803D))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // You Should Learn
                Text("Skills You Should Learn (${skillGap.missingSkills.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFFB45309))
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    skillGap.missingSkills.forEach { skill ->
                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(skill, fontSize = 11.sp, color = Color(0xFFB45309))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // AI Career Roadmap Timeline
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Career Roadmap", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                text = "${skillGap.learningRoadmap.count { it.isCompleted }}/${skillGap.learningRoadmap.size} Stages Cleared",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Roadmap Stages List
        skillGap.learningRoadmap.forEach { stage ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onToggleStage(stage.stageNumber) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = stage.isCompleted,
                        onCheckedChange = { onToggleStage(stage.stageNumber) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Stage ${stage.stageNumber}: ${stage.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (stage.isCompleted) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stage.duration,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        Text(stage.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)

                        if (stage.resources.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                stage.resources.forEach { res ->
                                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(4.dp)) {
                                        Text("📚 $res", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
