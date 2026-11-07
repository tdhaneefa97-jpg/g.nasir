package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Account
import com.example.data.model.ActivityLog
import com.example.data.model.ApprovalStatus
import com.example.data.model.ChatMessage
import com.example.data.model.Election
import com.example.data.model.Quiz
import com.example.data.repository.ClassmateRepository
import com.example.ui.components.AvatarCircle
import com.example.ui.components.PhoneConfirmationDialog
import com.example.ui.components.PolicyManualDialog
import com.example.ui.components.RoleBadge
import com.example.ui.theme.AlumniBlue
import com.example.ui.theme.AlumniEmerald
import com.example.ui.theme.AlumniGold
import com.example.ui.theme.AlumniGoldAccent
import com.example.ui.theme.AlumniNavyDark
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import com.example.ui.theme.AlumniRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    currentUser: Account,
    impersonatedUser: Account?,
    repository: ClassmateRepository,
    accounts: List<Account>,
    chatMessages: List<ChatMessage>,
    activeQuiz: Quiz?,
    activeElection: Election?,
    activityLogs: List<ActivityLog>,
    isNearVotingDays: Boolean,
    onLogout: () -> Unit
) {
    var selectedBottomNav by remember { mutableIntStateOf(0) }
    var showPolicyManual by remember { mutableStateOf(false) }
    var showStudentProfileDialog by remember { mutableStateOf(false) }
    var showBattleGame by remember { mutableStateOf(false) }
    var battleGameAutoMatchmake by remember { mutableStateOf(false) }

    // Effective user to display (impersonated if active, or current user)
    val activeUser = impersonatedUser ?: currentUser
    val isAdmin = currentUser.isAdminOrOneDayAdmin

    // Phone confirmation check: if current user hasn't confirmed their phone yet, prompt them!
    val needsPhoneConfirmation = !currentUser.isPhoneConfirmed && currentUser.approvalStatus == ApprovalStatus.APPROVED

    val pendingApprovalCount = accounts.count { it.approvalStatus == ApprovalStatus.PENDING_APPROVAL }

    if (showBattleGame) {
        BattleGameScreen(
            currentUser = activeUser,
            allAccounts = accounts,
            autoStartMatchmaking = battleGameAutoMatchmake,
            onBack = {
                showBattleGame = false
                battleGameAutoMatchmake = false
            }
        )
        return
    }

    Scaffold(
        topBar = {
            Column {
                // Impersonation Banner
                if (impersonatedUser != null) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Admin Mode: Viewing as ${impersonatedUser.name} (${impersonatedUser.id})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF78350F)
                                )
                            }
                            Text(
                                text = "Exit View ✕",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = AlumniRed,
                                modifier = Modifier
                                    .clickable { repository.stopImpersonating() }
                                    .testTag("btn_exit_impersonate")
                            )
                        }
                    }
                }

                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { showStudentProfileDialog = true }
                                .testTag("top_bar_student_account_badge")
                        ) {
                            AvatarCircle(name = activeUser.name, size = 34)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = activeUser.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("🎮", fontSize = 11.sp)
                                }
                                Text(
                                    text = "${activeUser.id} • ${activeUser.effectiveRole.displayTitle} • UID:${activeUser.uid}",
                                    fontSize = 10.sp,
                                    color = AlumniGoldAccent
                                )
                            }
                        }
                    },
                    actions = {
                        // Prominent Game Button on Student Account
                        Button(
                            onClick = { showBattleGame = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("btn_top_bar_battle_game")
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🎮 Game", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                        }

                        IconButton(
                            onClick = { showPolicyManual = true },
                            modifier = Modifier.testTag("top_bar_policy_btn")
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = "Policy Manual", tint = Color.White)
                        }
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.testTag("top_bar_logout_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Log Out", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AlumniNavyDark
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = selectedBottomNav == 0,
                    onClick = { selectedBottomNav = 0 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Chats") },
                    label = { Text("Chats", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_chats")
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 1,
                    onClick = { selectedBottomNav = 1 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Directory") },
                    label = { Text("Classmates", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_directory")
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 2,
                    onClick = { selectedBottomNav = 2 },
                    icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Game", tint = if (selectedBottomNav == 2) Color(0xFFEA580C) else Color.Unspecified) },
                    label = { Text("Game 🎮", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_battle_game")
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 3,
                    onClick = { selectedBottomNav = 3 },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Quiz") },
                    label = { Text("Quiz", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_quiz")
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 4,
                    onClick = { selectedBottomNav = 4 },
                    icon = { Icon(Icons.Default.HowToVote, contentDescription = "Elections") },
                    label = { Text("Elections", fontSize = 10.sp) },
                    modifier = Modifier.testTag("nav_elections")
                )
                if (isAdmin) {
                    NavigationBarItem(
                        selected = selectedBottomNav == 5,
                        onClick = { selectedBottomNav = 5 },
                        icon = {
                            if (pendingApprovalCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = AlumniRed) {
                                            Text(pendingApprovalCount.toString())
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                                }
                            } else {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                            }
                        },
                        label = { Text("Admin", fontSize = 10.sp) },
                        modifier = Modifier.testTag("nav_admin")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Dashboard Battle Game Button (Only visible for authenticated users)
            val isAuthenticated = currentUser.approvalStatus == ApprovalStatus.APPROVED && currentUser.id.isNotBlank()
            if (isAuthenticated && selectedBottomNav != 2) {
                Surface(
                    color = Color(0xFF1E1B4B),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dashboard_battle_game_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    battleGameAutoMatchmake = false
                                    showBattleGame = true
                                }
                        ) {
                            Surface(
                                color = Color(0xFFEA580C),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Free Fire 3D Battle Game", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Surface(
                                        color = AlumniGold,
                                        shape = RoundedCornerShape(3.dp)
                                    ) {
                                        Text("3D LOBBY", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 7.sp, modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp))
                                    }
                                }
                                Text("Real Players 4v4 Squad • 999,999 💎 Free", color = AlumniGoldAccent, fontSize = 9.sp)
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = {
                                    battleGameAutoMatchmake = false
                                    showBattleGame = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("dashboard_battle_game_button")
                            ) {
                                Text("🎮 Battle Game", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    battleGameAutoMatchmake = true
                                    showBattleGame = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB45309)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("dashboard_quick_match_button")
                            ) {
                                Text("⚡ Match", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedBottomNav) {
                    0 -> {
                        ChatScreen(
                            currentUser = activeUser,
                            repository = repository,
                            allAccounts = accounts,
                            messages = chatMessages
                        )
                    }
                    1 -> {
                        DirectoryScreen(
                            currentUser = activeUser,
                            repository = repository,
                            accounts = accounts,
                            onStartPrivateChat = { partnerId ->
                                selectedBottomNav = 0 // Navigate to chats
                            },
                            onOpenBattleGame = { showBattleGame = true }
                        )
                    }
                    2 -> {
                        BattleGameScreen(
                            currentUser = activeUser,
                            allAccounts = accounts,
                            autoStartMatchmaking = false,
                            onBack = { selectedBottomNav = 0 }
                        )
                    }
                    3 -> {
                        QuizScreen(
                            currentUser = activeUser,
                            repository = repository,
                            activeQuiz = activeQuiz,
                            accounts = accounts
                        )
                    }
                    4 -> {
                        ElectionScreen(
                            currentUser = activeUser,
                            repository = repository,
                            election = activeElection,
                            isNearVotingDays = isNearVotingDays,
                            allAccounts = accounts
                        )
                    }
                    5 -> {
                        if (isAdmin) {
                            AdminPanelScreen(
                                currentUser = currentUser,
                                repository = repository,
                                accounts = accounts,
                                activityLogs = activityLogs
                            )
                        }
                    }
                }
            }
        }
    }

    // Student Account Profile Dialog with Free Fire Info & Game Launch Button
    if (showStudentProfileDialog) {
        AlertDialog(
            onDismissRequest = { showStudentProfileDialog = false },
            modifier = Modifier.testTag("student_account_profile_dialog"),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarCircle(name = activeUser.name, size = 38)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(activeUser.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("UID: ${activeUser.uid} • ${activeUser.id}", fontSize = 11.sp, color = AlumniBlue)
                    }
                }
            },
            text = {
                Column {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("🎮 FREE FIRE ACCOUNT", color = AlumniGoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("Lv.${activeUser.level.coerceAtLeast(55)} • ${activeUser.rank} 🏆", color = AlumniEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Guild: VISWAM 7A WARRIORS", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("💎 Diamonds: 999,999", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("🪙 Coins: 999,999", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("All Guns, Bundles & Characters Unlocked (100% Free) ✓", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            showStudentProfileDialog = false
                            showBattleGame = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_student_account_game_launch")
                    ) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🎮 ENTER FREE FIRE BATTLE GAME", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { showStudentProfileDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Mandatory Phone Confirmation Dialog if not yet confirmed
    if (needsPhoneConfirmation) {
        PhoneConfirmationDialog(
            account = currentUser,
            onConfirm = { phoneInput ->
                repository.confirmPhoneNumber(currentUser.id, phoneInput)
            },
            onCancel = onLogout
        )
    }

    // Policy Manual Dialog
    if (showPolicyManual) {
        PolicyManualDialog(onDismiss = { showPolicyManual = false })
    }
}
