package com.example.ui.screens

import androidx.compose.foundation.background
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
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.FirebaseAuthManager
import com.example.data.model.Account
import com.example.data.model.ActivityLog
import com.example.data.model.ApprovalStatus
import com.example.data.model.Role
import com.example.data.repository.ClassmateRepository
import com.example.ui.components.AvatarCircle
import com.example.ui.components.RoleBadge
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.AlumniBlue
import com.example.ui.theme.AlumniEmerald
import com.example.ui.theme.AlumniGold
import com.example.ui.theme.AlumniGoldAccent
import com.example.ui.theme.AlumniNavyDark
import com.example.ui.theme.AlumniRed
import com.example.ui.viewmodel.AdminAction
import com.example.ui.viewmodel.AdminSection
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.PermissionResult
import com.example.ui.viewmodel.UserRoleManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    currentUser: Account,
    repository: ClassmateRepository,
    accounts: List<Account>,
    activityLogs: List<ActivityLog>,
    adminViewModel: AdminViewModel = viewModel()
) {
    val activeSection by adminViewModel.activeSection.collectAsState()
    val permissionDeniedDialog by adminViewModel.permissionDeniedDialog.collectAsState()

    // New Account Creation Form fields
    var newId by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var newCode by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newBatch by remember { mutableStateOf("Class of 2024") }
    var newIsSecured by remember { mutableStateOf(true) }
    var createFeedback by remember { mutableStateOf<String?>(null) }
    var createError by remember { mutableStateOf<String?>(null) }

    // Admin Credentials Edit state
    val context = LocalContext.current
    var editPasskey by remember { mutableStateOf(repository.adminPasskey) }
    var editSecurityCode by remember { mutableStateOf(repository.adminSecurityCode) }
    var editAdminPhone by remember { mutableStateOf(repository.adminPhone) }
    var phoneUpdateMessage by remember { mutableStateOf<String?>(null) }
    var phoneUpdateError by remember { mutableStateOf<String?>(null) }
    var credUpdateMessage by remember { mutableStateOf<String?>(null) }
    var credUpdateError by remember { mutableStateOf<String?>(null) }
    var showAdminRemoveAllDialog by remember { mutableStateOf(false) }
    var adminActionMessage by remember { mutableStateOf<String?>(null) }

    val pendingAccounts = accounts.filter { it.approvalStatus == ApprovalStatus.PENDING_APPROVAL }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Header
        Surface(
            color = AlumniNavyDark,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = AlumniGoldAccent,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Control Center",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    RoleBadge(role = currentUser.role)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Full administration: approve new registrations, create secured IDs, and monitor all logs.",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
                val fbUser = FirebaseAuthManager.currentUser
                if (fbUser != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Firebase Auth: ${fbUser.displayName ?: fbUser.email} (Verified ✓)",
                        color = AlumniEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Sub Tabs validated by AdminViewModel / UserRoleManager
        PrimaryTabRow(selectedTabIndex = activeSection.tabIndex.coerceIn(0, 4)) {
            Tab(
                selected = activeSection == AdminSection.APPROVALS,
                onClick = { adminViewModel.selectSection(AdminSection.APPROVALS, currentUser) },
                text = {
                    Text(
                        text = "Approvals (${pendingAccounts.size})",
                        fontWeight = FontWeight.SemiBold,
                        color = if (pendingAccounts.isNotEmpty()) AlumniRed else Color.Unspecified
                    )
                },
                modifier = Modifier.testTag("admin_tab_approvals")
            )
            Tab(
                selected = activeSection == AdminSection.CREATE_ID,
                onClick = { adminViewModel.selectSection(AdminSection.CREATE_ID, currentUser) },
                text = { Text("Create ID", fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.testTag("admin_tab_create")
            )
            Tab(
                selected = activeSection == AdminSection.GAME_SKINS,
                onClick = { adminViewModel.selectSection(AdminSection.GAME_SKINS, currentUser) },
                text = { Text("Game Skins", fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.testTag("admin_tab_game_skins")
            )
            Tab(
                selected = activeSection == AdminSection.AUDIT_LOGS,
                onClick = { adminViewModel.selectSection(AdminSection.AUDIT_LOGS, currentUser) },
                text = { Text("Audit Logs", fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.testTag("admin_tab_logs")
            )
            Tab(
                selected = activeSection == AdminSection.SECURITY,
                onClick = { adminViewModel.selectSection(AdminSection.SECURITY, currentUser) },
                text = { Text("Security", fontWeight = FontWeight.SemiBold) },
                modifier = Modifier.testTag("admin_tab_security")
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .verticalScroll(scrollState)
        ) {
            when (activeSection) {
                AdminSection.APPROVALS -> {
                    // Pending Registrations Queue
                    Text(
                        text = "Pending Account Requests",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "App Policy: Anyone can submit their details, but an Admin must approve the registration before login.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (pendingAccounts.isEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = AlumniEmerald, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("No pending approvals", fontWeight = FontWeight.SemiBold)
                                Text("All classmate accounts are currently up to date.", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    } else {
                        pendingAccounts.forEach { pending ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AvatarCircle(name = pending.name, size = 40)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(pending.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Text("Requested ID: ${pending.id}", fontSize = 12.sp, color = AlumniBlue, fontWeight = FontWeight.SemiBold)
                                                Text("Phone: ${pending.phone} • ${pending.batchInfo}", fontSize = 11.sp, color = Color.Gray)
                                            }
                                        }

                                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp)) {
                                            Text(
                                                "PENDING",
                                                color = Color(0xFF92400E),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    val isGoogleLogin = pending.phone.contains("@") || pending.secretCode.startsWith("GGL-")
                                    if (isGoogleLogin) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            color = AlumniBlue.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Phone, contentDescription = null, tint = AlumniBlue, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    "GOOGLE SIGN-IN • SMS APPROVAL REQUEST",
                                                    color = AlumniBlue,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                val smsTarget = if (pending.phone.contains("@")) repository.adminPhone else pending.phone
                                                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                    data = Uri.parse("smsto:$smsTarget")
                                                    putExtra("sms_body", "Hello ${pending.name}, your VISWAM CBSE 7A account request (${pending.id}) has been reviewed by the Admin.")
                                                }
                                                try {
                                                    context.startActivity(smsIntent)
                                                } catch (_: Exception) {}
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlumniBlue),
                                            modifier = Modifier.testTag("btn_sms_student_${pending.id}")
                                        ) {
                                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("SMS", fontSize = 12.sp)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        OutlinedButton(
                                            onClick = { repository.rejectAccount(pending.id) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlumniRed),
                                            modifier = Modifier.testTag("btn_reject_${pending.id}")
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reject", fontSize = 12.sp)
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Button(
                                            onClick = { repository.approveAccount(pending.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = AlumniEmerald),
                                            modifier = Modifier.testTag("btn_approve_${pending.id}")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Approve & Activate", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AdminSection.CREATE_ID -> {
                    // Create New Account Directly
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = AlumniBlue, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Direct Account Creation", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Create a new classmate or admin account directly. Set 'Secured' to protect it from deletion.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = newId,
                                onValueChange = { newId = it },
                                label = { Text("Enter Student ID") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_new_id_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newName,
                                onValueChange = { newName = it },
                                label = { Text("Enter full name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_new_name_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newCode,
                                onValueChange = { newCode = it },
                                label = { Text("Enter code") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_new_code_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newPhone,
                                onValueChange = { newPhone = it },
                                label = { Text("Enter phone number") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_new_phone_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newBatch,
                                onValueChange = { newBatch = it },
                                label = { Text("Class / Batch Info") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Secure Account Toggle
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (newIsSecured) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (newIsSecured) Icons.Default.Lock else Icons.Default.Security,
                                            contentDescription = null,
                                            tint = if (newIsSecured) Color(0xFF166534) else Color.Gray,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (newIsSecured) "Account Secured 🔒" else "Account Unsecured 🔓",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (newIsSecured) Color(0xFF166534) else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (newIsSecured) "Protected! No one can delete this account." else "Can be deleted by Admin.",
                                                fontSize = 11.sp,
                                                color = if (newIsSecured) Color(0xFF166534) else Color.Gray
                                            )
                                        }
                                    }

                                    Switch(
                                        checked = newIsSecured,
                                        onCheckedChange = { newIsSecured = it },
                                        modifier = Modifier.testTag("admin_toggle_secured_switch")
                                    )
                                }
                            }

                            if (createError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(createError ?: "", color = AlumniRed, fontSize = 12.sp)
                            }

                            if (createFeedback != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(createFeedback ?: "", color = AlumniEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val res = repository.addAccountDirectly(
                                        id = newId,
                                        name = newName,
                                        code = newCode,
                                        phone = newPhone,
                                        batch = newBatch,
                                        role = Role.MEMBER,
                                        isSecured = newIsSecured
                                    )
                                    res.onSuccess { acc ->
                                        createFeedback = "Account ${acc.name} (${acc.id}) created and saved in the app!"
                                        createError = null
                                        newId = ""
                                        newName = ""
                                        newCode = ""
                                        newPhone = ""
                                    }.onFailure { err ->
                                        createError = err.message
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AlumniNavyDark),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_admin_save_new_account")
                            ) {
                                Text("Save & Add to App Registry", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                AdminSection.GAME_SKINS -> {
                    // Game Skins Management (Temporary Admin & Head Admin permitted)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = AlumniEmerald, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Game Skins Management", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Temporary Admin & Head Admin Permitted Action", fontSize = 11.sp, color = AlumniEmerald, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Temporary Admin can modify Bundle Skins, Gun Skins, Character Skins, Backpack Skins, Car Skins, and Skateboard Skins.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            val skins = listOf(
                                Triple("AK-47 Dragon Flame", "Gun Skin • Red to Blue", "0 Diamonds / FREE"),
                                Triple("M4A1 Royal Gold", "Gun Skin • Blue to Gold", "0 Diamonds / FREE"),
                                Triple("AWM Dragon Sniper", "Gun Skin • Dragon Skin", "0 Diamonds / FREE"),
                                Triple("Speed King Bundle", "Character Dress • Gold & Black", "0 Diamonds / FREE"),
                                Triple("Red Phoenix Backpack", "Backpack Skin • Dragon Wings", "0 Diamonds / FREE"),
                                Triple("Monster Truck Lava", "Car Skin • Red Jeep & Monster", "0 Diamonds / FREE"),
                                Triple("Lightning Skateboard", "Skateboard Skin • Blue Glow", "0 Diamonds / FREE")
                            )

                            skins.forEach { (name, type, price) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(type, fontSize = 11.sp, color = AlumniBlue)
                                            Text("Price: $price", fontSize = 11.sp, color = AlumniEmerald, fontWeight = FontWeight.SemiBold)
                                        }

                                        Button(
                                            onClick = {
                                                adminActionMessage = "Skin '$name' modified and synced for all players!"
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("Modify Skin", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            if (adminActionMessage != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(adminActionMessage ?: "", color = AlumniEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                AdminSection.AUDIT_LOGS -> {
                    // Audit Logs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Audit & Telemetry Logs", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${activityLogs.size} Events", fontSize = 11.sp, color = Color.Gray)
                    }
                    Text("All logins, phone verifications, role promotions, and security events are shared with Admin.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))

                    val dateFormat = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }

                    activityLogs.forEach { log ->
                        val (icon, tint) = when (log.category) {
                            "LOGIN" -> Pair(Icons.Default.Key, AlumniBlue)
                            "PHONE" -> Pair(Icons.Default.Check, AlumniEmerald)
                            "SECURITY" -> Pair(Icons.Default.Security, AlumniGoldAccent)
                            "ACCOUNT" -> Pair(Icons.Default.PersonAdd, AlumniNavyDark)
                            "REPORT" -> Pair(Icons.Default.Warning, AlumniRed)
                            else -> Pair(Icons.Default.History, Color.Gray)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(log.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(dateFormat.format(Date(log.timestamp)), fontSize = 10.sp, color = Color.Gray)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(log.details, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                AdminSection.SECURITY,
                AdminSection.APP_SETTINGS,
                AdminSection.CODE_EDITOR,
                AdminSection.PLAYER_MANAGEMENT -> {
                    // Security & Master Credentials (HEAD ADMIN ONLY)
                    if (!currentUser.canModifyApp) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                            border = androidx.compose.foundation.BorderStroke(2.dp, AlumniRed)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = AlumniRed, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Permission Blocked", fontWeight = FontWeight.Bold, color = AlumniRed, fontSize = 15.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "You Cannot Modify App. Only Head Admin Can Modify App. You Can Only Add New Members And Modify Game Skins.",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7F1D1D),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Head Admin is the permanent owner and cannot be removed. Temporary Admin can only Add New Members and Modify Game Skins.",
                                    color = Color(0xFF991B1B),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = AlumniGoldAccent, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Dual-Key Master Credentials", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Master credentials used to access the Admin Gate. Only Head Admin can modify these.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Current Master Passkey:", fontSize = 11.sp, color = Color.Gray)
                                    Text(repository.adminPasskey, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AlumniBlue)

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text("Current Security Code:", fontSize = 11.sp, color = Color.Gray)
                                    Text(repository.adminSecurityCode, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AlumniEmerald)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text("Change Admin Credentials:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = editPasskey,
                                onValueChange = {
                                    editPasskey = it
                                    credUpdateError = null
                                    credUpdateMessage = null
                                },
                                label = { Text("Enter new master passkey") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_edit_admin_passkey")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = editSecurityCode,
                                onValueChange = {
                                    editSecurityCode = it
                                    credUpdateError = null
                                    credUpdateMessage = null
                                },
                                label = { Text("Enter new admin code") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_edit_admin_code")
                            )

                            if (credUpdateError != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(credUpdateError ?: "", color = AlumniRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            if (credUpdateMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(credUpdateMessage ?: "", color = AlumniEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    val res = repository.updateAdminCredentials(editPasskey, editSecurityCode)
                                    res.onSuccess {
                                        credUpdateMessage = "Admin Master Passkey and Security Code successfully updated!"
                                    }.onFailure { err ->
                                        credUpdateError = err.message
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AlumniNavyDark),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_save_admin_credentials")
                            ) {
                                Text("Save New Admin Credentials", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = AlumniBlue, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Admin Approval SMS Phone", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Mobile number where student Google login approval SMS requests are sent. Current: ${repository.adminPhone}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = editAdminPhone,
                                onValueChange = {
                                    editAdminPhone = it
                                    phoneUpdateError = null
                                    phoneUpdateMessage = null
                                },
                                label = { Text("Admin phone number") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_admin_phone")
                            )

                            if (phoneUpdateError != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(phoneUpdateError ?: "", color = AlumniRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            if (phoneUpdateMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(phoneUpdateMessage ?: "", color = AlumniEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    val res = repository.updateAdminPhone(editAdminPhone)
                                    res.onSuccess {
                                        phoneUpdateMessage = "Admin phone updated! Approval SMS requests will target $editAdminPhone."
                                        phoneUpdateError = null
                                    }.onFailure { err ->
                                        phoneUpdateError = err.message
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_save_admin_phone")
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Admin Phone for SMS Approvals", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Account Security Summary:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            val securedCount = accounts.count { it.isSecured }
                            val unsecuredCount = accounts.count { !it.isSecured }
                            val confirmedPhones = accounts.count { it.isPhoneConfirmed }

                            Text("• Secured IDs (Protected from deletion): $securedCount", fontSize = 12.sp)
                            Text("• Unsecured IDs (Can be removed): $unsecuredCount", fontSize = 12.sp)
                            Text("• Phone Confirmed Classmates: $confirmedPhones / ${accounts.size}", fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Classmate Registry Reset:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AlumniRed)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Remove all registered classmate accounts from the system. The Head Admin account will remain preserved.", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(10.dp))

                            if (adminActionMessage != null) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = AlumniEmerald.copy(alpha = 0.15f)),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = adminActionMessage ?: "",
                                        color = AlumniEmerald,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = { showAdminRemoveAllDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AlumniRed),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_admin_remove_all_classmates")
                            ) {
                                Icon(Icons.Default.DeleteSweep, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Remove All Classmates", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    }
                }
            }
        }
    }

    if (showAdminRemoveAllDialog) {
        AlertDialog(
            onDismissRequest = { showAdminRemoveAllDialog = false },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = AlumniRed, modifier = Modifier.size(32.dp))
            },
            title = {
                Text("Remove All Classmates?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Are you sure you want to remove all classmate accounts from VISWAM 7A? All student profiles, private messages, and registrations will be cleared. Only the Head Admin account will remain.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = repository.removeAllClassmates()
                        adminActionMessage = "Successfully removed all $count classmate account(s) from the registry."
                        showAdminRemoveAllDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlumniRed),
                    modifier = Modifier.testTag("confirm_admin_remove_all_button")
                ) {
                    Text("Confirm Remove All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAdminRemoveAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (permissionDeniedDialog != null) {
        AlertDialog(
            onDismissRequest = { adminViewModel.dismissPermissionDialog() },
            modifier = Modifier.testTag("permission_denied_dialog"),
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null, tint = AlumniRed, modifier = Modifier.size(32.dp))
            },
            title = {
                Text("Permission Restricted", fontWeight = FontWeight.Bold, color = AlumniRed)
            },
            text = {
                Column {
                    Text(
                        text = permissionDeniedDialog ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF7F1D1D),
                        modifier = Modifier.testTag("permission_denied_message")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Access Rules:\n• Head Admin: Permanent main owner. Full control over app settings, code, credentials, and accounts.\n• Temporary Admin: Permitted ONLY to (A) Add New Members and (B) Modify Game Skins.",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { adminViewModel.dismissPermissionDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = AlumniNavyDark),
                    modifier = Modifier.testTag("permission_denied_dismiss")
                ) {
                    Text("OK")
                }
            }
        )
    }
}

