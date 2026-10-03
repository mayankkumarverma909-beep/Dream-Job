package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.*
import com.example.ui.components.AnimatedInterviewerAvatar
import com.example.ui.components.RadialScoreIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewScreen(
    interviewConfig: InterviewConfig,
    isInterviewActive: Boolean,
    questions: List<InterviewQuestion>,
    currentQuestionIndex: Int,
    isRecordingVoice: Boolean,
    voiceTranscript: String,
    elapsedSeconds: Int,
    report: InterviewPerformanceReport?,
    isEvaluating: Boolean,
    onStartInterview: (InterviewConfig) -> Unit,
    onToggleVoiceRecording: () -> Unit,
    onSubmitAnswer: (String) -> Unit,
    onRetryInterview: () -> Unit,
    onOpenCoach: () -> Unit
) {
    val context = LocalContext.current
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            onToggleVoiceRecording()
        }
    }

    // Sub-Screen 1: Report View
    if (report != null) {
        InterviewReportView(
            report = report,
            onRetry = onRetryInterview,
            onOpenCoach = onOpenCoach
        )
        return
    }

    // Sub-Screen 2: Live Simulator
    if (isInterviewActive) {
        val currentQ = questions.getOrNull(currentQuestionIndex) ?: return
        LiveInterviewSimulatorView(
            question = currentQ,
            questionNumber = currentQuestionIndex + 1,
            totalQuestions = questions.size,
            elapsedSeconds = elapsedSeconds,
            isRecordingVoice = isRecordingVoice,
            voiceTranscript = voiceTranscript,
            isEvaluating = isEvaluating,
            onToggleRecording = {
                if (hasAudioPermission) {
                    onToggleVoiceRecording()
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            onSubmitAnswer = onSubmitAnswer
        )
        return
    }

    // Sub-Screen 3: Setup View
    InterviewSetupView(
        initialConfig = interviewConfig,
        onStart = onStartInterview,
        onOpenCoach = onOpenCoach
    )
}

@Composable
private fun InterviewSetupView(
    initialConfig: InterviewConfig,
    onStart: (InterviewConfig) -> Unit,
    onOpenCoach: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(initialConfig.role) }
    var selectedType by remember { mutableStateOf(initialConfig.interviewType) }
    var selectedLevel by remember { mutableStateOf(initialConfig.experienceLevel) }

    val roles = listOf("HR Executive", "Talent Acquisition Associate", "HR Generalist", "People Operations Trainee")
    val levels = listOf("Fresher (0-1 yrs)", "1-2 years experience", "Internship Candidate")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
            .testTag("interview_setup_screen")
    ) {
        // Banner Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Virtual Interview Simulator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Practice with an interactive animated interviewer who asks adaptive questions, evaluates STAR method answers, speech clarity, filler words, and body language cues.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Role Selection
        Text("Target Job Role", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        roles.forEach { role ->
            Surface(
                color = if (selectedRole == role) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (selectedRole == role) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedRole = role }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    RadioButton(selected = selectedRole == role, onClick = { selectedRole = role })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(role, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interview Type
        Text("Interview Round / Format", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        InterviewType.values().forEach { type ->
            Surface(
                color = if (selectedType == type) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (selectedType == type) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedType = type }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    RadioButton(selected = selectedType == type, onClick = { selectedType = type })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(type.displayName, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        Button(
            onClick = {
                onStart(
                    InterviewConfig(
                        role = selectedRole,
                        interviewType = selectedType,
                        experienceLevel = selectedLevel
                    )
                )
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("start_mock_interview_btn")
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Begin Mock Interview (5 Questions)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onOpenCoach,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().testTag("open_interview_coach_btn")
        ) {
            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Practice Q&A with AI Coach", fontSize = 13.sp)
        }
    }
}

@Composable
private fun LiveInterviewSimulatorView(
    question: InterviewQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    elapsedSeconds: Int,
    isRecordingVoice: Boolean,
    voiceTranscript: String,
    isEvaluating: Boolean,
    onToggleRecording: () -> Unit,
    onSubmitAnswer: (String) -> Unit
) {
    var manualAnswerText by remember { mutableStateOf("") }
    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timerString = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("live_interview_simulator")
    ) {
        // Status & Timer Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Question $questionNumber of $totalQuestions",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            // Timer with Live Recording Pulse
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isRecordingVoice) Color(0xFFEF4444) else Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = timerString,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Virtual Interviewer Avatar & Video Presentation Coaching Signal Box
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated AI Interviewer Avatar
            AnimatedInterviewerAvatar(
                isSpeaking = !isRecordingVoice && voiceTranscript.isEmpty(),
                isListening = isRecordingVoice,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Candidate Video Preview & Coaching Cues Box
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                modifier = Modifier
                    .width(130.dp)
                    .height(130.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Video Coaching", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Eye Contact: Good", fontSize = 9.sp, color = Color(0xFF38BDF8))
                        Text("Posture: Upright", fontSize = 9.sp, color = Color(0xFF34D399))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = question.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = question.questionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // AI Coach Tip
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tip: ${question.tip}",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Voice Answer / Manual Text Answer
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your Answer", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    // Mic Record Button
                    FilledTonalButton(
                        onClick = onToggleRecording,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isRecordingVoice) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.primaryContainer,
                            contentColor = if (isRecordingVoice) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("toggle_voice_btn")
                    ) {
                        Icon(
                            imageVector = if (isRecordingVoice) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isRecordingVoice) "Stop Speaking" else "Speak Answer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val displayedAnswer = if (voiceTranscript.isNotEmpty()) voiceTranscript else manualAnswerText

                OutlinedTextField(
                    value = displayedAnswer,
                    onValueChange = { manualAnswerText = it },
                    placeholder = {
                        Text(
                            text = if (isRecordingVoice) "Listening to your voice... Speak clearly using STAR framework." else "Tap 'Speak Answer' to talk, or type your response here...",
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("answer_input_field"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val answer = if (voiceTranscript.isNotEmpty()) voiceTranscript else manualAnswerText
                        onSubmitAnswer(if (answer.isBlank()) "Standard competent answer following STAR framework" else answer)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("submit_answer_proceed_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isEvaluating) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Analyzing Responses...")
                    } else {
                        Text(if (questionNumber < totalQuestions) "Submit & Next Question →" else "Finish Interview & Generate Report 📊")
                    }
                }
            }
        }
    }
}

@Composable
private fun InterviewReportView(
    report: InterviewPerformanceReport,
    onRetry: () -> Unit,
    onOpenCoach: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("interview_performance_report")
    ) {
        // Report Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Interview Performance Report", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Comprehensive speech, content & confidence breakdown", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overall Score Card
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
                RadialScoreIndicator(score = report.overallScore, size = 80.dp, label = "Readiness")
                Spacer(modifier = Modifier.width(18.dp))
                Column {
                    Text(
                        text = "Great Performance! 🎯",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF10B981)
                    )
                    Text(
                        text = "Your communication and role knowledge align well with HR Executive expectations. Focus on reducing filler words.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Metrics Grid (Communication, Answer Quality, Role Knowledge, STAR method)
        Text("Key Competency Scores", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            MetricPill("Communication", "${report.communicationScore}%", Modifier.weight(1f))
            MetricPill("Answer Quality", "${report.answerQualityScore}%", Modifier.weight(1f))
            MetricPill("STAR Method", "${report.starMethodUsageScore}%", Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            MetricPill("Speaking Pace", "${report.speechPaceWpm} WPM", Modifier.weight(1f))
            MetricPill("Filler Words", "${report.fillerWordCount} detected", Modifier.weight(1f))
            MetricPill("Eye Contact", "${report.eyeContactScore}%", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // What you did well
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("What You Did Well", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                report.whatYouDidWell.forEach { point ->
                    Text("• $point", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // What you should improve
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Areas For Improvement", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                report.whatToImprove.forEach { point ->
                    Text("• $point", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Better Answer Example (STAR format)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🌟 Model STAR Answer Example", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(report.betterAnswerExample.question, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = report.betterAnswerExample.sampleAnswer,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 17.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Retry & Coaching Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).testTag("retry_interview_btn")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Retry Interview")
            }

            OutlinedButton(
                onClick = onOpenCoach,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("Ask AI Coach")
            }
        }
    }
}

@Composable
private fun MetricPill(title: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
        ) {
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
