package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.navigation.NavDestination
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CareerViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CareerMateApp()
            }
        }
    }
}

@Composable
fun CareerMateApp(viewModel: CareerViewModel = viewModel()) {
    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }
    var navigationStack by remember { mutableStateOf(listOf(NavDestination.DASHBOARD)) }

    // State from ViewModel
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val filteredJobs by viewModel.filteredJobs.collectAsStateWithLifecycle()
    val savedJobs by viewModel.savedJobs.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedPlatform by viewModel.selectedPlatform.collectAsStateWithLifecycle()
    val selectedWorkMode by viewModel.selectedWorkMode.collectAsStateWithLifecycle()
    val selectedJobType by viewModel.selectedJobType.collectAsStateWithLifecycle()
    val activeNaturalSummary by viewModel.activeNaturalQuerySummary.collectAsStateWithLifecycle()

    val applications by viewModel.applications.collectAsStateWithLifecycle()
    val resume by viewModel.currentResume.collectAsStateWithLifecycle()
    val selectedTemplate by viewModel.selectedTemplate.collectAsStateWithLifecycle()
    val atsResult by viewModel.atsResult.collectAsStateWithLifecycle()
    val isScanningAts by viewModel.isAnalyzingAts.collectAsStateWithLifecycle()
    val skillGapData by viewModel.skillGapData.collectAsStateWithLifecycle()

    // Modals state
    val selectedJobDetail by viewModel.selectedJobForDetail.collectAsStateWithLifecycle()
    val selectedJobTailor by viewModel.selectedJobForTailoring.collectAsStateWithLifecycle()
    val tailoredResumeText by viewModel.tailoredResumeText.collectAsStateWithLifecycle()
    val isTailoringLoading by viewModel.isTailoringLoading.collectAsStateWithLifecycle()

    // Virtual Interview state
    val interviewConfig by viewModel.interviewConfig.collectAsStateWithLifecycle()
    val isInterviewActive by viewModel.isInterviewActive.collectAsStateWithLifecycle()
    val interviewQuestions by viewModel.currentInterviewQuestions.collectAsStateWithLifecycle()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsStateWithLifecycle()
    val voiceTranscript by viewModel.currentVoiceTranscript.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.interviewElapsedSeconds.collectAsStateWithLifecycle()
    val interviewReport by viewModel.interviewReport.collectAsStateWithLifecycle()
    val isEvaluatingInterview by viewModel.isEvaluatingInterview.collectAsStateWithLifecycle()

    // AI Coach & Assistant state
    val coachMessages by viewModel.coachMessages.collectAsStateWithLifecycle()
    val isCoachTyping by viewModel.isCoachTyping.collectAsStateWithLifecycle()
    val isAssistantOpen by viewModel.isAssistantOpen.collectAsStateWithLifecycle()
    val assistantMessages by viewModel.assistantMessages.collectAsStateWithLifecycle()
    val isAssistantThinking by viewModel.isAssistantThinking.collectAsStateWithLifecycle()

    var isCreateAlertOpen by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Navigation Helper
    fun navigateTo(dest: NavDestination) {
        if (currentDestination != dest) {
            navigationStack = navigationStack + dest
            currentDestination = dest
        }
    }

    // Handle back button for sub-screens
    BackHandler(enabled = navigationStack.size > 1) {
        val updated = navigationStack.dropLast(1)
        navigationStack = updated
        currentDestination = updated.lastOrNull() ?: NavDestination.DASHBOARD
    }

    // Snackbars
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("career_mate_root_scaffold"),
        topBar = {
            CareerTopBar(
                profileCompleteness = profile.calculateCompleteness(),
                onAssistantClick = { viewModel.isAssistantOpen.value = true },
                onProfileClick = { navigateTo(NavDestination.PROFILE) }
            )
        },
        bottomBar = {
            CareerBottomNav(
                currentDestination = currentDestination,
                onNavigate = { dest -> navigateTo(dest) }
            )
        },
        floatingActionButton = {
            if (!isAssistantOpen) {
                FloatingActionButton(
                    onClick = { viewModel.isAssistantOpen.value = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("floating_ai_assistant_fab")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "CareerMate AI Copilot")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                NavDestination.DASHBOARD -> {
                    DashboardScreen(
                        profile = profile,
                        jobs = filteredJobs,
                        savedJobs = savedJobs,
                        atsScore = atsResult.atsCompatibilityScore,
                        onNavigateToJobs = { navigateTo(NavDestination.JOBS) },
                        onNavigateToInterview = { navigateTo(NavDestination.INTERVIEW) },
                        onNavigateToResume = { navigateTo(NavDestination.RESUME) },
                        onNavigateToAts = { navigateTo(NavDestination.ATS_CHECKER) },
                        onNavigateToSkillGap = { navigateTo(NavDestination.SKILL_GAP) },
                        onNavigateToApplications = { navigateTo(NavDestination.APPLICATIONS) },
                        onJobDetails = { job -> viewModel.selectedJobForDetail.value = job },
                        onTailorResume = { job ->
                            viewModel.tailorResumeForJob(job)
                            navigateTo(NavDestination.RESUME)
                        },
                        onToggleSaveJob = { job, isSaved -> viewModel.toggleSaveJob(job, isSaved) }
                    )
                }

                NavDestination.JOBS -> {
                    JobsScreen(
                        jobs = filteredJobs,
                        savedJobs = savedJobs,
                        searchQuery = searchQuery,
                        selectedPlatform = selectedPlatform,
                        selectedWorkMode = selectedWorkMode,
                        selectedJobType = selectedJobType,
                        activeNaturalSummary = activeNaturalSummary,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onPlatformChange = { viewModel.selectedPlatform.value = it },
                        onWorkModeChange = { viewModel.selectedWorkMode.value = it },
                        onJobTypeChange = { viewModel.selectedJobType.value = it },
                        onResetFilters = { viewModel.resetFilters() },
                        onCreateAlertClick = { isCreateAlertOpen = true },
                        onJobDetails = { job -> viewModel.selectedJobForDetail.value = job },
                        onTailorResume = { job ->
                            viewModel.tailorResumeForJob(job)
                            navigateTo(NavDestination.RESUME)
                        },
                        onToggleSaveJob = { job, isSaved -> viewModel.toggleSaveJob(job, isSaved) }
                    )
                }

                NavDestination.INTERVIEW -> {
                    InterviewScreen(
                        interviewConfig = interviewConfig,
                        isInterviewActive = isInterviewActive,
                        questions = interviewQuestions,
                        currentQuestionIndex = currentQuestionIndex,
                        isRecordingVoice = isRecordingVoice,
                        voiceTranscript = voiceTranscript,
                        elapsedSeconds = elapsedSeconds,
                        report = interviewReport,
                        isEvaluating = isEvaluatingInterview,
                        onStartInterview = { config -> viewModel.startInterview(config) },
                        onToggleVoiceRecording = { viewModel.toggleVoiceRecording() },
                        onSubmitAnswer = { answer -> viewModel.submitAnswerAndProceed(answer) },
                        onRetryInterview = {
                            viewModel.startInterview(interviewConfig)
                        },
                        onOpenCoach = { navigateTo(NavDestination.INTERVIEW_COACH) }
                    )
                }

                NavDestination.INTERVIEW_COACH -> {
                    InterviewCoachScreen(
                        messages = coachMessages,
                        isTyping = isCoachTyping,
                        onSendMessage = { viewModel.askCoach(it) }
                    )
                }

                NavDestination.RESUME -> {
                    ResumeScreen(
                        resume = resume,
                        selectedTemplate = selectedTemplate,
                        tailoredJob = selectedJobTailor,
                        tailoredResumeText = tailoredResumeText,
                        isTailoringLoading = isTailoringLoading,
                        onTemplateSelect = { viewModel.selectedTemplate.value = it },
                        onNavigateToAts = { navigateTo(NavDestination.ATS_CHECKER) }
                    )
                }

                NavDestination.ATS_CHECKER -> {
                    AtsCheckerScreen(
                        atsResult = atsResult,
                        isScanning = isScanningAts,
                        onRunScan = { jd -> viewModel.runAtsCheck(jd) }
                    )
                }

                NavDestination.SKILL_GAP -> {
                    SkillGapScreen(
                        skillGap = skillGapData,
                        onToggleStage = { stageNum -> viewModel.toggleRoadmapStage(stageNum) },
                        onNavigateToInterview = { navigateTo(NavDestination.INTERVIEW) }
                    )
                }

                NavDestination.APPLICATIONS -> {
                    ApplicationsScreen(
                        applications = applications,
                        onAddApplication = { title, comp, loc, sal, plat, stat ->
                            viewModel.addApplication(title, comp, loc, sal, plat, stat)
                        },
                        onUpdateStatus = { id, stat -> viewModel.updateApplicationStatus(id, stat) },
                        onDeleteApplication = { id -> viewModel.deleteApplication(id) }
                    )
                }

                NavDestination.PROFILE -> {
                    ProfileScreen(
                        profile = profile,
                        onSaveProfile = { viewModel.updateProfile(it) }
                    )
                }
            }

            // Match Breakdown Dialog
            if (selectedJobDetail != null) {
                MatchBreakdownDialog(
                    job = selectedJobDetail!!,
                    onDismiss = { viewModel.selectedJobForDetail.value = null },
                    onTailorResume = {
                        val job = selectedJobDetail!!
                        viewModel.selectedJobForDetail.value = null
                        viewModel.tailorResumeForJob(job)
                        navigateTo(NavDestination.RESUME)
                    }
                )
            }

            // Create Job Alert Dialog
            if (isCreateAlertOpen) {
                CreateJobAlertDialog(
                    initialQuery = if (searchQuery.isNotBlank()) searchQuery else "HR Executive",
                    initialLocation = "Noida",
                    onDismiss = { isCreateAlertOpen = false },
                    onCreateAlert = { q, loc, exp, sal, freq ->
                        viewModel.createJobAlert(q, loc, exp, sal, freq)
                    }
                )
            }

            // Floating AI Assistant Sheet
            if (isAssistantOpen) {
                FloatingAIAssistantSheet(
                    messages = assistantMessages,
                    isThinking = isAssistantThinking,
                    onSendMessage = { viewModel.askAssistant(it) },
                    onNavigateToTab = { tab ->
                        when (tab) {
                            "jobs" -> navigateTo(NavDestination.JOBS)
                            "interview" -> navigateTo(NavDestination.INTERVIEW)
                            "resume" -> navigateTo(NavDestination.RESUME)
                            "ats" -> navigateTo(NavDestination.ATS_CHECKER)
                        }
                    },
                    onDismiss = { viewModel.isAssistantOpen.value = false }
                )
            }
        }
    }
}
