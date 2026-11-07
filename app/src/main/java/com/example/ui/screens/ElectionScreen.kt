package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Account
import com.example.data.model.AdminReport
import com.example.data.model.CandidateProfile
import com.example.data.model.Election
import com.example.data.model.ExamQuestion
import com.example.data.model.Role
import com.example.data.repository.ClassmateRepository
import com.example.ui.components.AvatarCircle
import com.example.ui.components.RoleBadge
import com.example.ui.theme.AlumniBlue
import com.example.ui.theme.AlumniEmerald
import com.example.ui.theme.AlumniGold
import com.example.ui.theme.AlumniGoldAccent
import com.example.ui.theme.AlumniNavyDark
import com.example.ui.theme.AlumniRed
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ElectionScreen(
    currentUser: Account,
    repository: ClassmateRepository,
    election: Election?,
    isNearVotingDays: Boolean,
    allAccounts: List<Account>
) {
    var showNewElectionDialog by remember { mutableStateOf(false) }
    var showAiExamDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var votingFeedback by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Live countdown ticker
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = System.currentTimeMillis()
        }
    }

    val isHeadAdmin = currentUser.isHeadAdmin
    val isTemporaryAdmin = currentUser.isTemporaryAdmin
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(scrollState)
    ) {
        // Hero Header & 2027 Countdown
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AlumniNavyDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AlumniGoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HowToVote,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin Elections 2027",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Annual Democratic Election • Year 2027 🗳️",
                                color = AlumniGoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (isHeadAdmin) {
                        Button(
                            onClick = { showNewElectionDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AlumniGoldAccent),
                            modifier = Modifier.testTag("btn_launch_election")
                        ) {
                            Text("Reset / New", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2027 Countdown Box
                val targetTime = election?.votingEndsAt ?: (System.currentTimeMillis() + 365L * 86_400_000L)
                val diffMillis = (targetTime - currentTime).coerceAtLeast(0L)
                val daysLeft = diffMillis / (1000 * 60 * 60 * 24)
                val hoursLeft = (diffMillis / (1000 * 60 * 60)) % 24
                val minsLeft = (diffMillis / (1000 * 60)) % 60
                val secsLeft = (diffMillis / 1000) % 60

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = AlumniGoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NEXT ADMIN ELECTION COUNTDOWN (2027)",
                                color = AlumniGoldAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CountdownUnit(number = daysLeft.toString(), label = "DAYS")
                            CountdownUnit(number = hoursLeft.toString(), label = "HOURS")
                            CountdownUnit(number = minsLeft.toString(), label = "MINS")
                            CountdownUnit(number = secsLeft.toString(), label = "SECS")
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Election Date: ${election?.electionDateText ?: "November 14, 2027"}",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mandatory Role Rules Banner
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "⚡ IMPORTANT RULE: WHO CAN MODIFY WHAT",
                            fontWeight = FontWeight.Bold,
                            color = AlumniGoldAccent,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• HEAD ADMIN: Permanent Owner. Only Head Admin can modify App, Code, Logo, Name & Settings. Cannot be removed.\n• TEMPORARY ADMIN (1 Year): Winner can ONLY Add New Members & Modify Game Skins (Bundles, Guns, Cars). Cannot modify App.\n• ALL STUDENTS: Full access to play with 999999 Diamonds & Coins.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 10.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI 1-Week Takeover Emergency Banner
        if (election?.isAiHandlingDueToReport == true) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AlumniRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = AlumniRed, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🤖 AI IS HANDLING APP FOR 1 WEEK!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF7F1D1D)
                        )
                        Text(
                            text = "Temporary Admin was removed for violating app rules. Classmate AI is taking care of the app for 7 days until the next election.",
                            fontSize = 11.sp,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Temporary Admin Warning Banner (if viewed by temporary admin)
        if (isTemporaryAdmin) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AlumniEmerald, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You are logged in as Temporary Admin. You have access to Add Members and Modify Game Skins only. You cannot modify the app.",
                        fontSize = 11.sp,
                        color = Color(0xFF166534),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // AI Question Paper & Exam Launch Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AlumniBlue, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("AI Question Paper & Exam", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AlumniNavyDark)
                            Text("Classmate AI conducts and grades the Exam", fontSize = 11.sp, color = AlumniBlue)
                        }
                    }

                    Button(
                        onClick = { showAiExamDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                        modifier = Modifier.testTag("btn_launch_ai_exam")
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Take AI Exam", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AI creates all exam questions about App Rules, Admin Duties, Game Skins, and Anti-Abuse handling. AI checks 'Can Take Care Score'. Only the TOP 3 scoring students can stand for 2027 Election!",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Top 3 Candidates List
        val candidateProfiles = election?.candidateProfiles ?: emptyList()
        val totalVotes = election?.votes?.size ?: 0
        val hasVoted = election?.votes?.containsKey(currentUser.id) == true ||
                election?.votes?.containsKey(currentUser.phone) == true ||
                election?.votes?.containsKey(currentUser.uid) == true
        val votedCandidateId = election?.votes?.get(currentUser.id)
            ?: election?.votes?.get(currentUser.phone)
            ?: election?.votes?.get(currentUser.uid)

        Text(
            text = "Candidates: Top 3 Exam Scorers (${candidateProfiles.size} / 3 Standing)",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "AI selected candidates with the highest Exam Score & Care Capability. 1 Student = 1 Vote.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (candidateProfiles.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.HowToVote, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("No candidates standing yet", fontWeight = FontWeight.SemiBold, color = Color.Gray)
                    Text("Tap 'Take AI Exam' above to qualify for the Top 3 roster!", fontSize = 11.sp, color = Color.Gray)
                }
            }
        } else {
            candidateProfiles.forEachIndexed { index, candidate ->
                val voteCount = election?.votes?.values?.count { it == candidate.studentId } ?: 0
                val percentage = if (totalVotes > 0) (voteCount.toFloat() / totalVotes.toFloat()) else 0f
                val isMyVote = votedCandidateId == candidate.studentId

                val rankColor = when (index) {
                    0 -> AlumniGoldAccent
                    1 -> Color(0xFFCBD5E1) // Silver
                    2 -> Color(0xFFB45309) // Bronze
                    else -> AlumniBlue
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .border(
                            width = if (isMyVote) 2.dp else 1.dp,
                            color = if (isMyVote) AlumniEmerald else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(14.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMyVote) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(rankColor.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#${index + 1}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = rankColor
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(candidate.studentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        if (isMyVote) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = AlumniEmerald,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "YOUR VOTE ✓",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "ID: ${candidate.studentId} • Exam: ${candidate.examScore}/100",
                                        fontSize = 11.sp,
                                        color = AlumniBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            if (election?.isActive == true && !hasVoted) {
                                Button(
                                    onClick = {
                                        val res = repository.castVote(currentUser.id, candidate.studentId)
                                        res.onSuccess {
                                            votingFeedback = "Your 2027 vote for ${candidate.studentName} is confirmed and recorded!"
                                            errorMessage = null
                                        }.onFailure { err ->
                                            errorMessage = err.message
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                                    modifier = Modifier.testTag("btn_vote_for_${candidate.studentId}")
                                ) {
                                    Icon(Icons.Default.HowToVote, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Vote", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(
                                    text = "$voteCount votes",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AlumniBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Can Take Care Score Badge
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Can Take Care Score: ${candidate.canTakeCareScore} / 100 • ${candidate.aiEvaluation}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF166534),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { percentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isMyVote) AlumniEmerald else AlumniBlue,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        }

        if (hasVoted) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = AlumniEmerald.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AlumniEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You already voted. Exactly 1 vote allowed per member UID / phone number.",
                        color = AlumniEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage ?: "", color = AlumniRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        if (votingFeedback != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(votingFeedback ?: "", color = AlumniEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Winner Proclamation Banner (if finalized)
        if (election?.isFinalized == true && election.winnerStudentId != null) {
            val winnerAcc = allAccounts.find { it.id == election.winnerStudentId }
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AlumniGoldAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AlumniGold, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("2027 ELECTION WINNER PROCLAIMED!", fontWeight = FontWeight.Bold, color = Color(0xFF92400E), fontSize = 13.sp)
                            Text(
                                "${winnerAcc?.name ?: election.winnerStudentId} is Elected Temporary Admin ⚡",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF78350F)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Temporary Admin Passkey: ${election.winnerTempAdminCode ?: "TEMP-ADMIN-7A24"}",
                        fontWeight = FontWeight.Bold,
                        color = AlumniBlue,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Your Temporary Admin Code Expires On: November 14, 2028 (1 Year Later). After expiry, new election begins.",
                        fontSize = 11.sp,
                        color = Color(0xFF92400E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Winner Can Modify Game Skins Only, Cannot Modify App, Can Add New Members.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFFB45309)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Actions: Report Admin & Head Admin Proclaim
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { showReportDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlumniRed),
                modifier = Modifier.weight(1f).testTag("btn_report_admin")
            ) {
                Icon(Icons.Default.ReportProblem, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Report Admin ⚠️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            if (isHeadAdmin && election?.isActive == true) {
                Button(
                    onClick = {
                        val res = repository.finalizeElection()
                        res.onSuccess { winnerAcc ->
                            votingFeedback = "2027 Election finalized! ${winnerAcc.name} issued Temporary Admin Passkey for 1 Year!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlumniNavyDark),
                    modifier = Modifier.weight(1f).testTag("btn_finalize_election")
                ) {
                    Text("Proclaim Winner 🏆", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Modal: AI Exam
    if (showAiExamDialog) {
        AiExamModal(
            student = currentUser,
            repository = repository,
            onDismiss = { showAiExamDialog = false },
            onCompleted = { score, careScore ->
                votingFeedback = "Exam completed! Exam Score: $score/100, Care Score: $careScore/100. AI evaluated and updated candidates."
                showAiExamDialog = false
            }
        )
    }

    // Modal: Report Admin
    if (showReportDialog) {
        ReportAdminModal(
            currentUser = currentUser,
            allAccounts = allAccounts,
            onSubmit = { reportedAdminId, category, reason ->
                val res = repository.submitAdminReport(currentUser, reportedAdminId, category, reason)
                res.onSuccess { report ->
                    votingFeedback = "Report submitted to Head Admin & AI! ${report.aiActionTaken ?: "AI is reviewing logs."}"
                    showReportDialog = false
                }.onFailure { err ->
                    errorMessage = err.message
                }
            },
            onDismiss = { showReportDialog = false }
        )
    }

    // Modal: Head Admin Launch New Election
    if (showNewElectionDialog) {
        CreateElectionModal(
            allAccounts = allAccounts.filter { it.role != Role.ADMIN },
            onDismiss = { showNewElectionDialog = false },
            onCreate = { title, selectedIds, freezeReg ->
                val res = repository.createElection(title, selectedIds, freezeReg)
                res.onSuccess {
                    showNewElectionDialog = false
                }
            }
        )
    }
}

@Composable
fun CountdownUnit(number: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = number.padStart(2, '0'),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AiExamModal(
    student: Account,
    repository: ClassmateRepository,
    onDismiss: () -> Unit,
    onCompleted: (examScore: Int, careScore: Int) -> Unit
) {
    val questions: List<ExamQuestion> = remember { repository.getAiExamQuestions() }
    val answers = remember { mutableStateMapOf<Int, Int>() }
    var doubtDialogQuestion by remember { mutableStateOf<ExamQuestion?>(null) }
    var doubtAnswerText by remember { mutableStateOf<String?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var finalResult by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = AlumniBlue, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Exam • 2027 Admin Qualification", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                if (!isSubmitted) {
                    Text(
                        text = "Conducted by Classmate AI. Answer all questions to prove you can take care of the app. If you score in the Top 3, you can stand for Admin Election!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    for ((index, q) in questions.withIndex()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Q${index + 1}. ${q.question}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                for ((optIndex, optText) in q.options.withIndex()) {
                                    val isSelected = answers[q.id] == optIndex
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { answers[q.id] = optIndex }
                                            .padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { answers[q.id] = optIndex }
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = optText, fontSize = 11.sp)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            doubtDialogQuestion = q
                                            doubtAnswerText = repository.askAiDoubt(q, "Can you explain this question?")
                                        }
                                    ) {
                                        Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Ask AI Doubt", fontSize = 10.sp, color = AlumniBlue)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Result Screen
                    val (score, careScore) = finalResult ?: Pair(0, 0)
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AlumniEmerald, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("AI Exam Result Evaluated!", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Exam Score: $score / 100", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AlumniBlue)
                        Text(
                            "Can Take Care Score: $careScore / 100",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (careScore >= 85) AlumniEmerald else AlumniRed
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = if (careScore >= 85) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (careScore >= 85)
                                    "AI DECISION: HE CAN TAKE CARE OF APP! Promoted to candidate consideration roster."
                                else
                                    "AI DECISION: He cannot take care of app yet. Care score below 85% requirement.",
                                color = if (careScore >= 85) Color(0xFF166534) else Color(0xFF7F1D1D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isSubmitted) {
                Button(
                    onClick = {
                        val result = repository.submitAiExam(student, answers.toMap())
                        finalResult = result
                        isSubmitted = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                    modifier = Modifier.testTag("btn_submit_ai_exam")
                ) {
                    Text("Submit to AI for Grading")
                }
            } else {
                Button(
                    onClick = {
                        finalResult?.let { (score, care) -> onCompleted(score, care) } ?: onDismiss()
                    }
                ) {
                    Text("Close & View Candidates")
                }
            }
        },
        dismissButton = {
            if (!isSubmitted) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )

    // Doubt Dialog
    if (doubtDialogQuestion != null) {
        AlertDialog(
            onDismissRequest = { doubtDialogQuestion = null },
            icon = { Icon(Icons.Default.HelpOutline, contentDescription = null, tint = AlumniBlue) },
            title = { Text("Classmate AI Doubt Clearing", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = doubtAnswerText ?: "AI can explain the topic, but cannot give the direct option answer to ensure election exam fairness.",
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { doubtDialogQuestion = null }) {
                    Text("Understood")
                }
            }
        )
    }
}

@Composable
fun ReportAdminModal(
    currentUser: Account,
    allAccounts: List<Account>,
    onSubmit: (reportedAdminId: String, category: String, reason: String) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = listOf(
        "Trying To Modify App",
        "Abuse",
        "Cheating",
        "Wrong Ban",
        "Stealing Diamonds",
        "Bad Behaviour"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var reasonText by remember { mutableStateOf("") }
    var selectedAdminId by remember { mutableStateOf("ADMIN-001") }

    val adminCandidates = allAccounts.filter { it.role == Role.ADMIN || it.role == Role.TEMPORARY_ADMIN }
    if (adminCandidates.isNotEmpty() && !adminCandidates.any { it.id == selectedAdminId }) {
        selectedAdminId = adminCandidates.first().id
    }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.ReportProblem, contentDescription = null, tint = AlumniRed, modifier = Modifier.size(28.dp)) },
        title = {
            Text("Report Admin to Head Admin & AI", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Rule: If a Temporary Admin does bad things or tries to modify the app, students can report them. If verified, AI removes them and takes over for 1 week before a new election.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("Select Violation Category:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))

                categories.forEach { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(cat, fontSize = 12.sp, fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Describe what happened in detail") },
                    modifier = Modifier.fillMaxWidth().testTag("report_reason_input"),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reasonText.isNotBlank()) {
                        onSubmit(selectedAdminId, selectedCategory, reasonText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AlumniRed),
                modifier = Modifier.testTag("btn_submit_report")
            ) {
                Text("Submit Report")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CreateElectionModal(
    allAccounts: List<Account>,
    onDismiss: () -> Unit,
    onCreate: (title: String, selectedCandidateIds: List<String>, freezeRegistrations: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("Annual Admin Election 2027") }
    var freezeRegistrations by remember { mutableStateOf(true) }
    val selectedIds = remember { mutableStateListOf<String>() }
    var errorText by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Launch Admin Election 2027", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Rule: Maximum 3 candidates allowed. Winner receives Temporary Admin code for 1 year to Add Members and Modify Game Skins only.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Election Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Near Voting Days Lock", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Freeze new ID creation during voting period", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(checked = freezeRegistrations, onCheckedChange = { freezeRegistrations = it })
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Select Candidates (${selectedIds.size} / 3 Max):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (selectedIds.size > 3) AlumniRed else AlumniBlue
                )
                Spacer(modifier = Modifier.height(6.dp))

                allAccounts.forEach { acc ->
                    val isChecked = selectedIds.contains(acc.id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isChecked) selectedIds.remove(acc.id)
                                else if (selectedIds.size < 3) selectedIds.add(acc.id)
                                else errorText = "Maximum 3 candidates allowed!"
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { check ->
                                if (check) {
                                    if (selectedIds.size < 3) selectedIds.add(acc.id)
                                    else errorText = "Maximum 3 candidates allowed!"
                                } else {
                                    selectedIds.remove(acc.id)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${acc.name} (${acc.id})", fontSize = 13.sp)
                    }
                }

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(errorText ?: "", color = AlumniRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedIds.isEmpty()) {
                        errorText = "Please select at least 1 candidate."
                    } else if (selectedIds.size > 3) {
                        errorText = "Maximum 3 candidates allowed!"
                    } else {
                        onCreate(title, selectedIds.toList(), freezeRegistrations)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AlumniNavyDark),
                modifier = Modifier.testTag("btn_submit_create_election")
            ) {
                Text("Start Election")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
