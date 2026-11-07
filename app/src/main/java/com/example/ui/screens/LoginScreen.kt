package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.data.auth.FirebaseAuthManager
import com.example.data.datastore.AuthDataStore
import com.example.data.model.Account
import com.example.data.repository.ClassmateRepository
import com.example.ui.components.GoogleSignInButton
import com.example.ui.components.PolicyManualDialog
import com.example.ui.theme.AlumniBlue
import com.example.ui.theme.AlumniEmerald
import com.example.ui.theme.AlumniGoldAccent
import com.example.ui.theme.AlumniNavyDark
import com.example.ui.theme.AlumniRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    repository: ClassmateRepository,
    authDataStore: AuthDataStore,
    isNearVotingDays: Boolean,
    onLoginSuccess: (Account) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var firebaseUser by remember { mutableStateOf(FirebaseAuthManager.currentUser) }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Classmate, 1: Admin Dual-Key
    var showPolicyDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showSmsApprovalDialog by remember { mutableStateOf(false) }
    var pendingGoogleUser by remember { mutableStateOf<com.google.firebase.auth.FirebaseUser?>(null) }
    var smsActionStatus by remember { mutableStateOf<String?>(null) }

    // Classmate login fields
    var studentIdInput by remember { mutableStateOf("") }
    var secretCodeInput by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var classmateError by remember { mutableStateOf<String?>(null) }

    // Admin login fields
    var adminPasskeyInput by remember { mutableStateOf("") }
    var adminSecurityCodeInput by remember { mutableStateOf("") }
    var adminRememberMe by remember { mutableStateOf(true) }
    var adminError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        FirebaseAuthManager.authStateFlow().collect { user ->
            firebaseUser = user
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(AlumniNavyDark, Color(0xFF1E3A8A))
                    )
                )
                .padding(top = 40.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = AlumniGoldAccent,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VISWAM School CBSE 7A",
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Classmate Alumni Network & Portal",
                                color = Color(0xFF93C5FD),
                                fontSize = 12.sp
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showPolicyDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlumniGoldAccent),
                        modifier = Modifier.testTag("open_policy_button")
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Manual", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome back, alumni! Enter your credentials to access chats, quizzes, and class activities.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Google Identity Card (Credential Manager + Firebase Auth)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (firebaseUser != null) AlumniEmerald.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (firebaseUser != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = AlumniEmerald, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Google Identity Verified ✓",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = AlumniEmerald
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = firebaseUser?.displayName ?: firebaseUser?.email ?: "Google Account",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (firebaseUser?.email != null) {
                                Text(
                                    text = firebaseUser?.email ?: "",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        TextButton(
                            onClick = {
                                FirebaseAuthManager.signOut(context, credentialManager, coroutineScope) {
                                    firebaseUser = null
                                }
                            }
                        ) {
                            Text("Switch", fontSize = 12.sp, color = AlumniRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val user = firebaseUser
                            if (user != null) {
                                val res = repository.handleGoogleLoginAttempt(user)
                                res.onSuccess { account ->
                                    onLoginSuccess(account)
                                }.onFailure { err ->
                                    if (err.message?.contains("PENDING_ADMIN_APPROVAL") == true) {
                                        pendingGoogleUser = user
                                        showSmsApprovalDialog = true
                                    } else {
                                        classmateError = err.message
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlumniEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_enter_hub_google")
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enter VISWAM 7A Hub with Google", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = AlumniBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Secure Google Authentication",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Authenticate with your Google account via Credential Manager. First-time student access requires Admin SMS approval.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    GoogleSignInButton(
                        onAuthSuccess = { user ->
                            firebaseUser = user
                            val res = repository.handleGoogleLoginAttempt(user)
                            res.onSuccess { account ->
                                onLoginSuccess(account)
                            }.onFailure { err ->
                                if (err.message?.contains("PENDING_ADMIN_APPROVAL") == true) {
                                    pendingGoogleUser = user
                                    showSmsApprovalDialog = true
                                } else {
                                    classmateError = err.message
                                }
                            }
                        },
                        onAuthError = { err ->
                            classmateError = err
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Classmate Login", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_classmate_login")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Admin Gate", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_admin_login")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        if (selectedTab == 0) {
            // Classmate Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Enter Your Classmate Credentials",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Enter your assigned Student ID and Secret Code.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = studentIdInput,
                        onValueChange = {
                            studentIdInput = it
                            classmateError = null
                        },
                        label = { Text("Enter your Student ID") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_student_id")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = secretCodeInput,
                        onValueChange = {
                            secretCodeInput = it
                            classmateError = null
                        },
                        label = { Text("Enter your code") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_secret_code")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { rememberMe = !rememberMe }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            modifier = Modifier.testTag("remember_me_checkbox")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Remember me",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Don't ask code again when opening the app",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (classmateError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = classmateError ?: "",
                            color = AlumniRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (studentIdInput.isBlank() || secretCodeInput.isBlank()) {
                                classmateError = "Please enter both Student ID and Secret Code."
                            } else {
                                val res = repository.loginWithIdAndCode(studentIdInput, secretCodeInput)
                                res.onSuccess { account ->
                                    coroutineScope.launch {
                                        authDataStore.saveClassmateSession(studentIdInput, secretCodeInput, rememberMe)
                                    }
                                    onLoginSuccess(account)
                                }.onFailure { error ->
                                    classmateError = error.message
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_classmate_login")
                    ) {
                        Text("Log In as Classmate", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Registration prompt card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Left school recently?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isNearVotingDays)
                                "Registration frozen during election days."
                            else
                                "Request a new ID (Requires Admin Approval).",
                            fontSize = 11.sp,
                            color = if (isNearVotingDays) AlumniRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showRegisterDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isNearVotingDays) Color.Gray else AlumniNavyDark),
                        modifier = Modifier.testTag("btn_request_new_id")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Join Hub", fontSize = 12.sp)
                    }
                }
            }
        } else {
            // Admin Dual-Key Gate Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = AlumniGoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Dual-Key Security Gate",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Authorized Admins only. Enter the Master Passkey followed by the One-Time Admin Security Code.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = adminPasskeyInput,
                        onValueChange = {
                            adminPasskeyInput = it
                            adminError = null
                        },
                        label = { Text("Enter your passkey") },
                        placeholder = { Text("Enter your passkey") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_admin_passkey")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminSecurityCodeInput,
                        onValueChange = {
                            adminSecurityCodeInput = it
                            adminError = null
                        },
                        label = { Text("Enter your code") },
                        placeholder = { Text("Enter your code") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_admin_code")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { adminRememberMe = !adminRememberMe }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = adminRememberMe,
                            onCheckedChange = { adminRememberMe = it },
                            modifier = Modifier.testTag("admin_remember_me_checkbox")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Remember me",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Don't ask passkey & code again on this device",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (adminError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = adminError ?: "",
                            color = AlumniRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (adminPasskeyInput.isBlank() || adminSecurityCodeInput.isBlank()) {
                                adminError = "Please enter both the Master Passkey and Admin Security Code."
                            } else {
                                val res = repository.adminLogin(adminPasskeyInput, adminSecurityCodeInput)
                                res.onSuccess { adminAccount ->
                                    coroutineScope.launch {
                                        authDataStore.saveAdminSession(adminPasskeyInput, adminSecurityCodeInput, adminRememberMe)
                                    }
                                    onLoginSuccess(adminAccount)
                                }.onFailure { error ->
                                    adminError = error.message
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlumniNavyDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_admin_login")
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Authenticate as Master Admin", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Policy Dialog
    if (showPolicyDialog) {
        PolicyManualDialog(onDismiss = { showPolicyDialog = false })
    }

    // Register / Request New ID Dialog
    if (showRegisterDialog) {
        RegisterIdDialog(
            isNearVotingDays = isNearVotingDays,
            onDismiss = { showRegisterDialog = false },
            onSubmit = { id, name, code, phone, batch ->
                val result = repository.requestRegistration(id, name, code, phone, batch)
                result
            }
        )
    }

    // Google Login Admin SMS Approval Dialog
    if (showSmsApprovalDialog) {
        val user = pendingGoogleUser ?: firebaseUser
        val studentName = user?.displayName ?: "Student"
        val studentEmail = user?.email ?: "Google Account"
        val adminPhone = repository.adminPhone

        AlertDialog(
            onDismissRequest = {
                showSmsApprovalDialog = false
                smsActionStatus = null
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = AlumniBlue,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Admin SMS Approval Required",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "When a student tries to login with their Google account, an approval request must first be sent to the Admin's phone via SMS.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Student: $studentName", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Account: $studentEmail", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Admin Mobile: $adminPhone", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AlumniBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tap below to pre-fill an SMS request directly to the Admin. Once Admin marks your account approved in their Admin Control Center, tap 'Check Approval Status' to enter the hub.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (smsActionStatus != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (smsActionStatus?.contains("approved", ignoreCase = true) == true)
                                    AlumniEmerald.copy(alpha = 0.15f)
                                else
                                    AlumniBlue.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = smsActionStatus ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val smsBody = "VISWAM CBSE 7A Access Request: Student $studentName ($studentEmail) is requesting approval to log in to the 7A Alumni Portal. Please approve my ID in the Admin Panel."
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:$adminPhone")
                                putExtra("sms_body", smsBody)
                            }
                            try {
                                context.startActivity(intent)
                                smsActionStatus = "SMS app opened to send request to Admin ($adminPhone). After sending, tap 'Check Approval Status' once Admin approves."
                            } catch (e: Exception) {
                                smsActionStatus = "Could not launch SMS app: ${e.message}. Please send SMS manually to $adminPhone."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_send_admin_sms")
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Approval Request SMS to Admin", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val targetUser = pendingGoogleUser ?: firebaseUser
                            if (targetUser != null) {
                                val checkRes = repository.handleGoogleLoginAttempt(targetUser)
                                checkRes.onSuccess { account ->
                                    showSmsApprovalDialog = false
                                    smsActionStatus = null
                                    onLoginSuccess(account)
                                }.onFailure { err ->
                                    if (err.message?.contains("PENDING_ADMIN_APPROVAL") == true) {
                                        smsActionStatus = "Request is still PENDING Admin Approval. Please ensure Admin has approved you in the Admin Control Center."
                                    } else {
                                        smsActionStatus = err.message
                                    }
                                }
                            } else {
                                smsActionStatus = "Please sign in with Google first."
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlumniEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_check_approval_status")
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Check Approval Status & Enter Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSmsApprovalDialog = false
                        smsActionStatus = null
                    },
                    modifier = Modifier.testTag("btn_dismiss_sms_dialog")
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun RegisterIdDialog(
    isNearVotingDays: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (id: String, name: String, code: String, phone: String, batch: String) -> Result<Account>
) {
    var idInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var batchInput by remember { mutableStateOf("Class of 2024") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Request New Classmate ID", fontWeight = FontWeight.Bold)
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                if (isNearVotingDays) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                "🚫 Registration Frozen",
                                fontWeight = FontWeight.Bold,
                                color = AlumniRed,
                                fontSize = 13.sp
                            )
                            Text(
                                "Per App Policy Rule #8, in near voting days no one can create or register a new ID. Please wait until elections conclude.",
                                fontSize = 12.sp,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    Text(
                        text = "Fill in your details. New accounts require Admin Permission before login.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = idInput,
                    onValueChange = { idInput = it },
                    label = { Text("Enter desired Student ID") },
                    enabled = !isNearVotingDays && !isSuccess,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_id_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Enter your full name") },
                    enabled = !isNearVotingDays && !isSuccess,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = { codeInput = it },
                    label = { Text("Enter your code") },
                    enabled = !isNearVotingDays && !isSuccess,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_code_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text("Enter your phone number") },
                    enabled = !isNearVotingDays && !isSuccess,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("register_phone_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = batchInput,
                    onValueChange = { batchInput = it },
                    label = { Text("Class / Batch Info") },
                    enabled = !isNearVotingDays && !isSuccess,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (statusMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = statusMessage ?: "",
                        color = if (isSuccess) AlumniBlue else AlumniRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            if (!isSuccess) {
                Button(
                    onClick = {
                        val result = onSubmit(idInput, nameInput, codeInput, phoneInput, batchInput)
                        result.onSuccess {
                            isSuccess = true
                            statusMessage = "Application submitted successfully! Your account is now pending Admin Approval. The admin will review and grant access."
                        }.onFailure { err ->
                            statusMessage = err.message
                        }
                    },
                    enabled = !isNearVotingDays,
                    colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                    modifier = Modifier.testTag("submit_registration_button")
                ) {
                    Text("Submit for Approval")
                }
            } else {
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            if (!isSuccess) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
