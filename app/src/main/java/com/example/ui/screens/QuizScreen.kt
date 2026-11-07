package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Account
import com.example.data.model.Quiz
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

@Composable
fun QuizScreen(
    currentUser: Account,
    repository: ClassmateRepository,
    activeQuiz: Quiz?,
    accounts: List<Account>
) {
    var showCreateQuizDialog by remember { mutableStateOf(false) }
    var declaringWinnerForUser by remember { mutableStateOf<Account?>(null) }
    var selectedOption by remember { mutableIntStateOf(-1) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val isAdmin = currentUser.isAdminOrOneDayAdmin
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(scrollState)
    ) {
        // Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AlumniNavyDark),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = AlumniGoldAccent,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Alumni Quiz Arena",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Compete to win 1-Day Admin Status! 👑",
                                color = AlumniGoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (isAdmin) {
                        Button(
                            onClick = { showCreateQuizDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AlumniGold),
                            modifier = Modifier.testTag("btn_conduct_new_quiz")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Quiz", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "App Policy: Admin conducts quizzes. The winner receives full 1-Day Admin moderation & portal management privileges!",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Current 1-Day Admin Winner Banner
        val currentWinner = accounts.find { it.effectiveRole == Role.ONE_DAY_ADMIN }
        if (currentWinner != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFEF3C7)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AlumniGoldAccent)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(AlumniGoldAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Reigning 1-Day Admin",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            RoleBadge(role = Role.ONE_DAY_ADMIN)
                        }
                        Text(
                            text = "${currentWinner.name} (${currentWinner.id})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF78350F)
                        )
                        if (activeQuiz?.winnerCaption != null) {
                            Text(
                                text = "\"${activeQuiz.winnerCaption}\"",
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeQuiz == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Quiz,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No active quiz at the moment.",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Check back soon when the Admin posts a new trivia contest!",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            // Active Quiz Card
            val mySubmission = activeQuiz.submissions[currentUser.id]
            val hasSubmitted = mySubmission != null

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (activeQuiz.isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (activeQuiz.isActive) "ACTIVE QUIZ ●" else "CONCLUDED",
                                color = if (activeQuiz.isActive) Color(0xFF166534) else Color(0xFF991B1B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "${activeQuiz.submissions.size} Submissions",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = activeQuiz.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activeQuiz.caption,
                        fontSize = 12.sp,
                        color = AlumniBlue,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Question Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = AlumniBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeQuiz.question,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Options
                    activeQuiz.options.forEachIndexed { index, option ->
                        val isSelected = if (hasSubmitted) mySubmission == index else selectedOption == index
                        val isCorrect = index == activeQuiz.correctOptionIndex

                        val borderColor = when {
                            hasSubmitted && isSelected && isCorrect -> AlumniEmerald
                            hasSubmitted && isSelected && !isCorrect -> AlumniRed
                            isSelected -> AlumniBlue
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        val bgColor = when {
                            hasSubmitted && isSelected && isCorrect -> Color(0xFFDCFCE7)
                            hasSubmitted && isSelected && !isCorrect -> Color(0xFFFEE2E2)
                            isSelected -> AlumniBlue.copy(alpha = 0.1f)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
                                .clickable(enabled = !hasSubmitted && activeQuiz.isActive) {
                                    selectedOption = index
                                },
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = if (!hasSubmitted && activeQuiz.isActive) { { selectedOption = index } } else null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = option,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isAdmin) {
                                    if (isCorrect) {
                                        Text(
                                            text = "(Correct Key)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AlumniEmerald
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!hasSubmitted && activeQuiz.isActive) {
                        Button(
                            onClick = {
                                if (selectedOption >= 0) {
                                    val res = repository.submitQuizAnswer(activeQuiz.id, currentUser.id, selectedOption)
                                    res.onSuccess {
                                        feedbackMessage = "Your answer was submitted! Admin will evaluate and crown the 1-Day Admin."
                                    }
                                }
                            },
                            enabled = selectedOption >= 0,
                            colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_submit_quiz_answer")
                        ) {
                            Text("Submit My Answer", fontWeight = FontWeight.Bold)
                        }
                    } else if (hasSubmitted) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AlumniEmerald, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Your response is locked in! Awaiting Admin's declaration.",
                                fontSize = 12.sp,
                                color = AlumniEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (feedbackMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = feedbackMessage ?: "",
                            fontSize = 12.sp,
                            color = AlumniBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Submissions & Leaderboard (Especially useful for Admin)
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Participant Submissions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (isAdmin) "Review classmate responses and crown the 1-Day Admin winner!" else "Submissions recorded for this contest.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (activeQuiz.submissions.isEmpty()) {
                        Text(
                            text = "No classmates have answered yet.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        activeQuiz.submissions.forEach { (studentId, choiceIndex) ->
                            val respondent = accounts.find { it.id == studentId }
                            val isCorrect = choiceIndex == activeQuiz.correctOptionIndex

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCorrect) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AvatarCircle(name = respondent?.name ?: studentId, size = 36)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "${respondent?.name ?: studentId} (${studentId})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Answer: ${activeQuiz.options.getOrElse(choiceIndex) { "Option $choiceIndex" }}",
                                                fontSize = 11.sp,
                                                color = if (isCorrect) AlumniEmerald else AlumniRed,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    if (isAdmin && respondent != null && respondent.id != currentUser.id) {
                                        Button(
                                            onClick = { declaringWinnerForUser = respondent },
                                            colors = ButtonDefaults.buttonColors(containerColor = AlumniGoldAccent),
                                            modifier = Modifier.testTag("btn_crown_winner_${respondent.id}")
                                        ) {
                                            Text("Crown 👑", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
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

    // Modal to create a new Quiz (Admin Only)
    if (showCreateQuizDialog) {
        CreateQuizModal(
            onDismiss = { showCreateQuizDialog = false },
            onCreate = { title, question, options, correctIdx, caption ->
                repository.createQuiz(title, question, options, correctIdx, caption)
                showCreateQuizDialog = false
            }
        )
    }

    // Modal to declare winner & enter caption
    if (declaringWinnerForUser != null && activeQuiz != null) {
        val winner = declaringWinnerForUser!!
        var captionText by remember { mutableStateOf("Congratulations ${winner.name} for winning the Alumni Quiz! You are now 1-Day Admin 👑") }

        AlertDialog(
            onDismissRequest = { declaringWinnerForUser = null },
            icon = {
                Icon(Icons.Default.Star, contentDescription = null, tint = AlumniGoldAccent, modifier = Modifier.size(32.dp))
            },
            title = {
                Text("Crown 1-Day Admin Winner", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Awarding 1-Day Admin privileges to ${winner.name} (${winner.id}) for 24 hours.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = captionText,
                        onValueChange = { captionText = it },
                        label = { Text("Winner Announcement Caption") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.declareQuizWinner(activeQuiz.id, winner.id, captionText)
                        declaringWinnerForUser = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlumniGoldAccent)
                ) {
                    Text("Award 1-Day Admin 👑", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { declaringWinnerForUser = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CreateQuizModal(
    onDismiss: () -> Unit,
    onCreate: (title: String, question: String, options: List<String>, correctIndex: Int, caption: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }
    var opt0 by remember { mutableStateOf("") }
    var opt1 by remember { mutableStateOf("") }
    var opt2 by remember { mutableStateOf("") }
    var opt3 by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var caption by remember { mutableStateOf("Winner receives 1-Day Admin Status!") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Conduct New Alumni Quiz", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Enter Quiz Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Enter Quiz Question") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Options (select the radio button of the correct answer):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = correctIndex == 0, onClick = { correctIndex = 0 })
                    OutlinedTextField(value = opt0, onValueChange = { opt0 = it }, label = { Text("Option 1") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = correctIndex == 1, onClick = { correctIndex = 1 })
                    OutlinedTextField(value = opt1, onValueChange = { opt1 = it }, label = { Text("Option 2") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = correctIndex == 2, onClick = { correctIndex = 2 })
                    OutlinedTextField(value = opt2, onValueChange = { opt2 = it }, label = { Text("Option 3") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = correctIndex == 3, onClick = { correctIndex = 3 })
                    OutlinedTextField(value = opt3, onValueChange = { opt3 = it }, label = { Text("Option 4") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Enter Prize Caption") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(errorMsg ?: "", color = AlumniRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val options = listOf(opt0, opt1, opt2, opt3).filter { it.isNotBlank() }
                    if (title.isBlank() || question.isBlank() || options.size < 2) {
                        errorMsg = "Please enter a title, question, and at least 2 options."
                    } else {
                        onCreate(title, question, options, correctIndex.coerceAtMost(options.size - 1), caption)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                modifier = Modifier.testTag("submit_create_quiz_button")
            ) {
                Text("Publish Quiz")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
