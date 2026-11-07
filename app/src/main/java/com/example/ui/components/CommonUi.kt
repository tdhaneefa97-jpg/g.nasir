package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.ApprovalStatus
import com.example.data.model.Role
import com.example.ui.theme.AlumniBlue
import com.example.ui.theme.AlumniEmerald
import com.example.ui.theme.AlumniGold
import com.example.ui.theme.AlumniGoldAccent
import com.example.ui.theme.AlumniNavyDark
import com.example.ui.theme.AlumniRed

@Composable
fun RoleBadge(role: Role) {
    val (bgColor, textColor, label, icon) = when (role) {
        Role.ADMIN -> Quadruple(
            Color(0xFFFEF3C7),
            Color(0xFF92400E),
            "ADMIN 🛡️",
            Icons.Default.AdminPanelSettings
        )
        Role.ONE_DAY_ADMIN -> Quadruple(
            Color(0xFFFDE68A),
            Color(0xFF78350F),
            "1-DAY ADMIN 👑",
            Icons.Default.Star
        )
        Role.TEMPORARY_ADMIN -> Quadruple(
            Color(0xFFDCFCE7),
            Color(0xFF166534),
            "TEMP ADMIN (1-YR) ⚡",
            Icons.Default.Star
        )
        Role.MEMBER -> Quadruple(
            Color(0xFFEFF6FF),
            Color(0xFF1E40AF),
            "CLASSMATE",
            Icons.Default.Security
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SecurityBadge(isSecured: Boolean) {
    val bgColor = if (isSecured) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
    val textColor = if (isSecured) Color(0xFF166534) else Color(0xFF991B1B)
    val text = if (isSecured) "SECURED 🔒" else "UNSECURED 🔓"

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSecured) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AvatarCircle(name: String, size: Int = 44) {
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "U" }

    val gradient = Brush.linearGradient(
        listOf(AlumniNavyDark, AlumniBlue)
    )

    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(gradient)
            .border(1.5.dp, AlumniGoldAccent, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size * 0.4).sp
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Mandatory Phone Confirmation Dialog triggered upon first login
 */
@Composable
fun PhoneConfirmationDialog(
    account: Account,
    onConfirm: (phoneInput: String) -> Unit,
    onCancel: () -> Unit
) {
    var phoneInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = {},
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = AlumniBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Confirm Phone Number",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Welcome ${account.name} (${account.id})!",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Per School Alumni Portal rules, for confirmation you must enter the phone number registered with your ID to activate full access.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Registered ID: ${account.id}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Class Batch: ${account.batchInfo}", fontSize = 12.sp)
                        Text("Hint: Registered ending in ...${account.phone.takeLast(4)}", fontSize = 11.sp, color = AlumniEmerald)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = {
                        phoneInput = it
                        errorMessage = null
                    },
                    label = { Text("Enter Your Phone Number") },
                    placeholder = { Text(account.phone) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_phone_input")
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = AlumniRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Quick Test: Tap to prefill registered phone (${account.phone})",
                    fontSize = 11.sp,
                    color = AlumniBlue,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                TextButton(
                    onClick = { phoneInput = account.phone },
                    modifier = Modifier.testTag("prefill_phone_button")
                ) {
                    Text("Auto-fill registered phone", fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (phoneInput.isBlank()) {
                        errorMessage = "Please enter your phone number."
                    } else {
                        onConfirm(phoneInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                modifier = Modifier.testTag("confirm_phone_submit")
            ) {
                Text("Confirm & Unlock")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
                Text("Log Out")
            }
        }
    )
}

/**
 * App Policy Manual dialog / bottom sheet
 */
@Composable
fun PolicyManualDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = AlumniGoldAccent,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Classmate Hub Policy Manual",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Constitution & Portal Regulations",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(scrollState)
            ) {
                PolicySection(
                    number = "1",
                    title = "Student ID & Verification",
                    content = "Every classmate who left school has a unique ID and Secret Code. Data is shared with Admin for alumni verification."
                )
                PolicySection(
                    number = "2",
                    title = "Mandatory Phone Confirmation",
                    content = "On first login, each classmate must confirm their registered phone number corresponding to their ID. Once confirmed, full access is granted."
                )
                PolicySection(
                    number = "3",
                    title = "Dual-Key Master Admin Access",
                    content = "Admin login is strictly protected by a Master Passkey and a Security Code. Admin has authority to inspect any account, moderate chats, and oversee the alumni network."
                )
                PolicySection(
                    number = "4",
                    title = "Account Security & Deletion Rules",
                    content = "Accounts can be marked as 'SECURED'. A secured account cannot be deleted by anyone! To delete an account, an Admin must explicitly unsecure it first."
                )
                PolicySection(
                    number = "5",
                    title = "New Account Registration & Approval",
                    content = "Any classmate can request an account with their details. However, an Admin must approve the registration before the user can log in."
                )
                PolicySection(
                    number = "6",
                    title = "Chat Transparency System",
                    content = "The WhatsApp-styled chat features Public and Private rooms. Public chats are visible to all members. Private chats are visible to the two members AND monitored by Admin for school policy compliance."
                )
                PolicySection(
                    number = "7",
                    title = "Alumni Quizzes & 1-Day Admin",
                    content = "Admin can conduct quizzes. The winner of the quiz is awarded Honorary 1-Day Admin status with special permissions for 24 hours."
                )
                PolicySection(
                    number = "8",
                    title = "Annual Elections & Registration Freeze",
                    content = "Annual elections limit candidates to at most 3 members. Each member can vote only 1 time. Near voting days, no new IDs can be registered until results are proclaimed."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AlumniBlue),
                modifier = Modifier.testTag("close_policy_manual")
            ) {
                Text("Understood & Accept")
            }
        }
    )
}

@Composable
fun PolicySection(number: String, title: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(AlumniBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
