package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AtsAnalysisResult
import com.example.ui.components.RadialScoreIndicator

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AtsCheckerScreen(
    atsResult: AtsAnalysisResult,
    isScanning: Boolean,
    onRunScan: (String) -> Unit
) {
    var targetJdInput by remember {
        mutableStateOf(
            "Seeking an HR Executive / Talent Acquisition Associate with proficiency in candidate sourcing, Excel data tracking, ATS management, and campus drive scheduling."
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("ats_checker_screen")
    ) {
        // Title
        Text("ATS Resume Scanner", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Test keyword alignment, parsing readability & ATS score", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(14.dp))

        // Overall ATS Score Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(18.dp)
            ) {
                RadialScoreIndicator(score = atsResult.atsCompatibilityScore, size = 84.dp, label = "ATS Score")
                Spacer(modifier = Modifier.width(18.dp))
                Column {
                    Text(
                        text = "ATS Compatibility: ${atsResult.atsCompatibilityScore}/100",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Highly optimized for HR Executive & Talent roles. 88% keywords matched with standard ATS parsers.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Score Breakdown
        Text("ATS Category Breakdown", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        AtsScoreBar("Keyword Relevance Match", atsResult.keywordMatchPercent)
        AtsScoreBar("Resume Formatting & Layout", atsResult.formattingScore)
        AtsScoreBar("Skills & Tech Alignment", atsResult.skillsAlignmentScore)
        AtsScoreBar("Experience Relevance", atsResult.experienceRelevanceScore)

        Spacer(modifier = Modifier.height(16.dp))

        // Job Description Input Box for Live Scan
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Target Job Description (Optional)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Paste specific JD to test custom keyword match", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = targetJdInput,
                    onValueChange = { targetJdInput = it },
                    placeholder = { Text("Paste job description keywords here...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().height(90.dp).testTag("ats_jd_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { onRunScan(targetJdInput) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("run_ats_scan_btn")
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing Resume Against ATS...")
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Re-Scan ATS Compatibility")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Matched & Missing Keywords
        Text("Keyword Analysis", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("✅ Matched Keywords (${atsResult.matchedKeywords.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF10B981))
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    atsResult.matchedKeywords.forEach { kw ->
                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                            Text(kw, fontSize = 11.sp, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("⚠️ Missing Keywords (${atsResult.missingKeywords.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFF59E0B))
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    atsResult.missingKeywords.forEach { kw ->
                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(6.dp)) {
                            Text(kw, fontSize = 11.sp, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Actionable Improvements
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Actionable ATS Recommendations", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                atsResult.actionableImprovements.forEach { tip ->
                    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.ArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(tip, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mandatory Disclaimer
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = atsResult.disclaimer,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun AtsScoreBar(label: String, score: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("$score%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (score >= 85) Color(0xFF10B981) else MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = if (score >= 85) Color(0xFF10B981) else Color(0xFF4F46E5),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
